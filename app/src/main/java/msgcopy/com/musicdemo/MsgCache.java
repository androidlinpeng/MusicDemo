package msgcopy.com.musicdemo;

import android.content.Context;

import java.io.File;

public class MsgCache{

    public static ACache get(){
        return get(MyApplication.getInstance());
    }

    public static ACache get(Context cxt){
       return get(cxt,false);
    }

	public static ACache get(Context cxt, boolean withUser){
//		String username= withUser ? Account.getUser().username : "common";
        String path = cxt.getFilesDir()
                + File.separator
                + "cache"
                + File.separator
                + "ACache";
        File file = new File(path);
        return ACache.get(file);
	}
}
