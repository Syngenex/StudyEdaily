import okhttp3.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;

public class daily {
    private static String[] num= new String[]{"******"};     //账号
    private static String[] psd=new String[]{"******"};    //密码
    private static String[] scopeKey=new String[]{"******"};      //scopeKey,自行抓包获取
    private static String[] entityKey=new String[]{"******"};     //entityKey,自行抓包获取
    private static String[] address=new String[]{"******#*#*"};       //位置信息，经纬度值小数点后六位
    private static String pushPlusKey="******";   //PushPlus推送Token

    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {
        for (int i = 0; i< num.length; i++){
            List<String> Cookie = null;
            System.out.println((i+1)+".准备签到"+":"+ num[i]);
            Cookie=Login(num[i], psd[i]);
            Thread.sleep(10000);
            Checked(scopeKey[i], entityKey[i], address[i],Cookie);
            System.out.println("签到成功"+(i+1)+ num[i]);
        }
        PushToPushPluse(pushPlusKey,"程序运行成功，并不代表签到成功，为确保漏签请到APP查看是否成功签到");
    }

    private static List Login(String num, String psd) throws UnsupportedEncodingException {
        final Base64.Encoder encoder = Base64.getEncoder();
        final byte[] textByte = psd.getBytes("UTF-8");
        final String encod_psd = encoder.encodeToString(textByte);

        List<String> cookie = null;
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
            String str = response.body().string();
            System.out.println(str);
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
            String str = response.body().string();
            System.out.println(str);
        } catch (IOException e) {
            System.out.println("超时，但是可以签到成功");
        }
    }

    private static void PushToPushPluse(String pushPlusKey,String sayToPush){
        OkHttpClient okHttpClient=new OkHttpClient();
        FormBody formBody =new FormBody.Builder()
                .add("token",pushPlusKey)
                .add("content",sayToPush).build();
        Request request =new Request.Builder()
                //.url("http://www.pushplus.plus/send?title=得实签到")
                .url("http://www.pushplus.plus/send?title=得实签到&topic=2020030302")
                .post(formBody)
                .build();
        Call call=okHttpClient.newCall(request);
        try {
            Response response=call.execute();
            String str = response.body().string();
            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("推送错误:"+e);
        }
    }

}
