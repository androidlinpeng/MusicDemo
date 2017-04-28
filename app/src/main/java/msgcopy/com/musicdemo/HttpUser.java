package msgcopy.com.musicdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import msgcopy.com.musicdemo.modul.NewSong;
import msgcopy.com.musicdemo.modul.SongList;
import msgcopy.com.musicdemo.modul.Songurl;
import msgcopy.com.musicdemo.service.UserService;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liang on 2017/4/17.
 */

public class HttpUser {
    private static final String TAG = "HttpUser";
    public static final int HTTP_CACHE_SIZE = 100 * 1024 * 1024;
    public static final int HTTP_READ_TIMEOUT = 15 * 1000;
    public static final int HTTP_WRITE_TIMEOUT = 10 * 1000;
    public static final int HTTP_CONNECT_TIMEOUT = 10 * 1000;
    private static final String HTTP_CACHE_DIR = "http";

    private Retrofit retrofit;
    private UserService userService;

    public void getSongPath(Subscriber<Songurl> subscriber, String songid) {
        userService = getRetrofit().create(UserService.class);
        userService.getSongPath(songid)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getGetData(Subscriber<NewSong> subscriber,String type,String size) {
        userService = getRetrofit().create(UserService.class);
        userService.getGetData(type,size)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }
    public void getSongListData(Subscriber<SongList> subscriber) {
        userService = getRetrofit().create(UserService.class);
        userService.getSongListData()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }


    public Retrofit getRetrofit() {
        //
        Gson gson = new GsonBuilder().create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);
        //
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //
        OkHttpClient.Builder okbuilder = new OkHttpClient.Builder();
        okbuilder.readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS);
        okbuilder.writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.SECONDS);
        okbuilder.connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        okbuilder.cache(new Cache(getHttpCacheDir(MyApplication.getInstance()), HTTP_CACHE_SIZE));
        okbuilder.interceptors().add(new ReceivedCookiesInterceptor(MyApplication.getInstance()));
        okbuilder.interceptors().add(new AddCookiesInterceptor(MyApplication.getInstance()));

        // 适配器
        retrofit = new Retrofit.Builder()
                .client(okbuilder.build())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(APIUrl.URL_DOMAIN)
                .build();
        return retrofit;
    }

    public class AddCookiesInterceptor implements Interceptor {
        private Context context;

        public AddCookiesInterceptor(Context context) {
            super();
            this.context = context;
        }
        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request.Builder builder = chain.request().newBuilder();

            Context context= MyApplication.getInstance();
            String uuid = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

            builder.addHeader("Accept-Encoding","gzip");
            builder.addHeader("User-Agent","android_5.9.9.4;baiduyinyue");
//            builder.addHeader("cuid","38D9C3D58F2B4550E00B41F2819D36C4");
//            builder.addHeader("deviceid",uuid);
            builder.addHeader("cuid","BC8054D71DA30123B485889EAB3D3F7A");
            builder.addHeader("deviceid","869161027041482");
            builder.addHeader("Connection","Keep-Alive");
            builder.addHeader("Host","baifen.music.baidu.com");
            return chain.proceed(builder.build());

        }
    }


    public class ReceivedCookiesInterceptor implements Interceptor {

        private Context context;

        public ReceivedCookiesInterceptor(Context context) {
            super();
            this.context = context;
        }
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            //这里获取请求返回的cookie
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                setCookie(context,String.valueOf(originalResponse.headers("Set-Cookie")));
            }
            return originalResponse;
        }
    }

    private static final String COOKIE = "cookie";

    public String getCookie(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(COOKIE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(COOKIE, "");
    }
    public String setCookie(Context context,String cookie){
        SharedPreferences sharedPreferences = context.getSharedPreferences(COOKIE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COOKIE, cookie);
        editor.commit();
        return null;
    }

    //缓存
    public static File getHttpCacheDir(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return new File(context.getExternalCacheDir(), HTTP_CACHE_DIR);
        }
        return new File(context.getCacheDir(), HTTP_CACHE_DIR);
    }
}

































