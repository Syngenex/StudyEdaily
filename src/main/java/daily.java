import okhttp3.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;

public class daily {

    private static List<String> num=List.of(new String[]{"num1","num2"});     //账号
    private static List<String> psd=List.of(new String[]{"psd1","psd2"});    //密码
    private static List<String> scopeKey=List.of(new String[]{"***","***"});      //scopeKey,自行抓包获取
    private static List<String> entityKey=List.of(new String[]{"***","***"});     //entityKey,自行抓包获取
    private static List<String> address=List.of(new String[]{"**省**市**区**村#经度值#纬度值", "**省**市**区**村#经度值#纬度值"});       //位置信息，经纬度值小数点后六位
    private static String pushPlusKey="3d515bc0bb14410dba242b1b2c989f87";   //PushPlus推送Token
    public static List<String> Cookie=null;      //cookie自动获取，不用填写
    private static String failNum="";     //签到失败账号，不用填写


    public static void main(String[] args) throws UnsupportedEncodingException {
        for (int i=0; i<num.size(); i++){
            Cookie=null;
            System.out.println((i+1)+".准备签到"+":"+num.get(i));
            Cookie=Login(num.get(i),psd.get(i));
            if (Cookie==null){//登录失败
                System.out.println(num.get(i)+"登录失败");
                failNum=failNum+(num.get(i)+"，");
            }else{//登录成功
                Checked(scopeKey.get(i),entityKey.get(i),address.get(i),Cookie);
                System.out.println("签到成功"+(i+1)+num.get(i)+"Success");
            }
        }
        if (failNum!=""){
            PushToPushPluse(pushPlusKey,"失败账号："+failNum.toString());
        }else{
            PushToPushPluse(pushPlusKey,"运行成功，为确保漏签请到APP查看是否成功签到");
        }
    }

    private static List Login(String num, String psd) throws UnsupportedEncodingException {
        final Base64.Encoder encoder = Base64.getEncoder();
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
            if (str.contains("true")){
                //获取cookie
                if (response.isSuccessful()){
                    Headers headers=response.headers();
                    cookie= headers.values("Set-Cookie");
                    System.out.println("Cookie"+cookie);
                }
                System.out.println(str.substring(str.indexOf("userName\":\"")+11,str.indexOf("\",\"userKey\""))+"登陆成功");
            }else {
                cookie=null;
                //System.out.println("登录失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("未知错误");
        }
        return cookie;
    }

    private static void Checked(String scopeKey,String entityKey,String address,List<String> Cookie){
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
            String str =response.body().string();
            System.out.println("签到成功:"+str);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("未知错误:"+e);
        }
    }

    private static void PushToPushPluse(String pushPlusKey,String sayToPush){
        OkHttpClient okHttpClient=new OkHttpClient();
        FormBody formBody =new FormBody.Builder()
                .add("token",pushPlusKey)
                .add("content",sayToPush).build();
        Request request =new Request.Builder()
                .url("http://www.pushplus.plus/send")
                .post(formBody)
                .build();
        Call call=okHttpClient.newCall(request);
        try {
            Response response=call.execute();
            String str =response.body().string();
            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("推送错误:"+e);
        }
    }

}
