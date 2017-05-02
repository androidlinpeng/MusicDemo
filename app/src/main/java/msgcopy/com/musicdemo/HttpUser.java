package msgcopy.com.musicdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import msgcopy.com.musicdemo.modul.NewSong;
import msgcopy.com.musicdemo.modul.SongList;
import msgcopy.com.musicdemo.modul.Songurl;
import msgcopy.com.musicdemo.modul.online.SongLry;
import msgcopy.com.musicdemo.modul.online.SongSearch;
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
    private static final String USER_AGENT = "User-Agent";

    private Retrofit retrofit;
    private UserService userService;

    public void getSongLry(Subscriber<SongLry> subscriber, String songid) {
        userService = getRetrofit().create(UserService.class);
        userService.getSongLry(songid)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getSongSearch(Subscriber<SongSearch> subscriber, String query) {
        userService = getRetrofit().create(UserService.class);
        userService.getSongSearch(query)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

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
            builder.addHeader(USER_AGENT,makeUserAgent());
            return chain.proceed(builder.build());

        }
    }
    private String makeUserAgent() {
        return Build.BRAND + "/" + Build.MODEL + "/" + Build.VERSION.RELEASE;
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

































