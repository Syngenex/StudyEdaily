import okhttp3.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

public class daily {
    private static String[] num= new String[]{};
    private static String[] psd=new String[]{};
    private static String[] scopeKey=new String[]{};
    private static String[] entityKey=new String[]{};
    private static String[] address=new String[]{};
    private static String pushPlusKey="";

    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {
        loadConfig();
        for (int i = 0; i< num.length; i++){
            List<String> Cookie = null;
            System.out.println((i+1)+".准备签到"+":"+ num[i]);
            Cookie=Login(num[i], psd[i]);
            Thread.sleep(10000+(int)(Math.random()*180000));
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

    private static void loadConfig(){
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream("src/config.properties")) {
            // 使用UTF-8编码读取文件内容
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        num = properties.getProperty("num").split(",");
        psd = properties.getProperty("psd").split(",");
        scopeKey = properties.getProperty("scopeKey").split(",");
        entityKey = properties.getProperty("entityKey").split(",");
        address = properties.getProperty("address").split(",");
        pushPlusKey = properties.getProperty("pushPlusKey");
    }

}
