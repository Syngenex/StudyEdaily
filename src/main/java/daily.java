import okhttp3.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;

public class daily {

    private static String num="************";       //账号
    private static String psd="******";     //密码
    public static List<String> Cookie=null;       //cookie，自动获取不用填
    private static String scopeKey="********";      //scopeKey
    private static String entityKey="********";     //entityKey
    private static String address="**省**市**区**村#经度值#纬度值";       //位置信息，经纬度值小数点后六位

    public static void main(String[] args) throws UnsupportedEncodingException {
        Cookie=Login(num,psd);
        System.out.println("签到返回："+Checked(scopeKey,entityKey,address,Cookie));
    }

    private static List Login(String num, String psd) throws UnsupportedEncodingException {
        final Base64.Encoder encoder = Base64.getEncoder();;
        final byte[] textByte = psd.getBytes("UTF-8");
        final String encod_psd = encoder.encodeToString(textByte);

        List<String> cookie=null;
        OkHttpClient okHttpClient=new OkHttpClient();
        FormBody formBody =new FormBody.Builder()
                .add("loginName",num)
                .add("password",encod_psd).build();
        Request request =new Request.Builder()
                .url("http://103.239.153.192:8081/suite/appLogin/login.do")
                .post(formBody)
                .build();
        Call call=okHttpClient.newCall(request);
        try {
            Response response=call.execute();
            String str =response.body().string();
            //判断是否登陆成功
            if (str.indexOf("true")>=0){//登录成功
                System.out.println("返回体："+str);
                //adapter.name =str.substring(str.indexOf("userName\":\"")+11,str.indexOf("\",\"userKey\""));
            }else {
                System.out.println("登录失败");
            }
            //获取cookie
            if (response.isSuccessful()){
                Headers headers=response.headers();
                cookie= headers.values("Set-Cookie");
                System.out.println("Cookie"+cookie);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("未知错误");
        }
        return cookie;
    }

    private static String Checked(String scopeKey,String entityKey,String address,List<String> Cookie){
        OkHttpClient okHttpClient=new OkHttpClient();
        FormBody formBody =new FormBody.Builder()
                .add("scopeKey",scopeKey)
                .add("entityKey",entityKey)
                .add("address",address)
                .add("tag","").build();
        Request request =new Request.Builder()
                .url("http://103.239.153.192:8081/suite/appHippo/signIn_signInForStudent.do")
                .post(formBody)
                .addHeader("Cookie",Cookie.get(0))
                .build();
        Call call=okHttpClient.newCall(request);
        try {
            Response response=call.execute();
            //返回体
            String str =response.body().string();
            return str;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("未知错误");
            return e.toString();
        }
    }

}
