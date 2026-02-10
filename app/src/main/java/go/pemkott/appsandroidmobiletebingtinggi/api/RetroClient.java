package go.pemkott.appsandroidmobiletebingtinggi.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {


private static final String BASE_URL="https://absensi.kotakecol.gov/api/";
    private static RetroClient myClient;
    private final Retrofit retrofit;

    private RetroClient(){
        retrofit=new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
    }

    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .build();

    public static synchronized RetroClient getInstance(){
        if (myClient==null){
            myClient=new RetroClient();
        }
        return myClient;
    }

    public ApiAddProduk getApi(){
        return retrofit.create(ApiAddProduk.class);
    }

    public static synchronized RetroClient getInstance2(){
        if (myClient==null){
            myClient=new RetroClient();
        }
        return myClient;
    }

    public HttpService getApi2(){
        return retrofit.create(HttpService.class);
    }
}
