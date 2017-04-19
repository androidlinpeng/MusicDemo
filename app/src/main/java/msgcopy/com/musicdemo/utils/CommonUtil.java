package msgcopy.com.musicdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

	/**
	 * ???sdcard??????
	 * 
	 * @return true?????????????????
	 */
	public static boolean sdCardIsAvailable() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED))
			return false;
		return true;
	}

	/**
	 * Checks if there is enough Space on SDCard
	 * 
	 * @param updateSize
	 *            Size to Check
	 * @return True if the Update will fit on SDCard, false if not enough space on SDCard Will also return false, if the SDCard is
	 *         not mounted as read/write
	 */
	public static boolean enoughSpaceOnSdCard(long updateSize) {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED))
			return false;
		return (updateSize < getRealSizeOnSdcard());
	}

	/**
	 * get the space is left over on sdcard
	 */
	public static long getRealSizeOnSdcard() {
		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 * Checks if there is enough Space on phone self
	 * 
	 */
	public static boolean enoughSpaceOnPhone(long updateSize) {
		return getRealSizeOnPhone() > updateSize;
	}

	/**
	 * get the space is left over on phone self
	 */
	public static long getRealSizeOnPhone() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long realSize = blockSize * availableBlocks;
		return realSize;
	}

	/**
	 * ???????????dp???px
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static  int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  

    /** 
     * ???????????? px(????) ???λ ???? dp 
     */  
	public static  int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f)-15;  
    }  
	
	/** 
     * 将px值转换为sp值，保证文字大小不变 
     *  
     * @param pxValue 
     * @param fontScale 
     *            （DisplayMetrics类中属性scaledDensity） 
     * @return 
     */  
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  
  
    /** 
     * 将sp值转换为px值，保证文字大小不变 
     *  
     * @param spValue 
     * @param fontScale 
     *            （DisplayMetrics类中属性scaledDensity） 
     * @return 
     */  
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }  
	
	public static void hideSoftInput(Activity activity)
	{
    	InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    	View view = activity.getCurrentFocus();
    	IBinder b = null;
    	if(view != null)   	b = view.getWindowToken();
    	imm.hideSoftInputFromWindow(b, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public static void showSoftInput(Activity activity)
	{
    	InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    	View view = activity.getCurrentFocus();
    	IBinder b = null;
    	if(view != null)   	b = view.getWindowToken();
    	imm.showSoftInputFromInputMethod(b, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public static boolean isBlank(String s){
		return (s==null || s.equals("") || s.equals("null"));
	}

	public static boolean isBlank(Object s){
		return (s==null || s.equals("") || s.equals("null"));
	}
	
	public static boolean isPhoneNumber(String phone) {
//		Pattern pattern = Pattern.compile("((13[0-9]|15[0-3|5-9]|18[0|1|2|3|5-9])\\d{8})");
		if(!isBlank(phone) && phone.length()==11){
			Pattern pattern = Pattern.compile("((13[0-9]|15[0-3|5-9]|18[0-9]|1349|17[0|6-8]|14[5|7])\\d{8})");
			Matcher matcher = pattern.matcher(phone);
			
			if (matcher.matches()) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public static boolean isMail(String mail){
//		Pattern regex = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
		Pattern regex = Pattern.compile("\\w+((-\\w+)|(.\\w+))*@[A-Za-z0-9]+((.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+");
		Matcher matcher = regex.matcher(mail);
		return matcher.matches();
	}
	
	/**
	 * 将 2013-07-22T09:30:14 格式化为 2013-07-22 09:30:14
	 * 
	 * @param time
	 * @return
	 */
	public static String getFormatTime(String time){
		if(isBlank(time)){
			return time;
		}
		String[] s=time.split("T");
		if(s.length==2){
			time=s[0]+" "+s[1];
		}
		return time;
	}
	
	/**
	 * 计算传入时间与当前时间的间隔
	 * 例：3分钟前
	 * 
	 * @param time
	 * @return
	 */
	public static String getShowValue(String time){
		if(isBlank(time)){
			return time;
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date d1 = df.parse(time);
			Date d2 = new Date();
			long diff = d2.getTime() - d1.getTime();   // 这样得到的差值是微秒级别
			
			if(diff<0){
				return "0分钟前";
			}

			long days = diff / (1000 * 60 * 60 * 24);
			long minutes =diff/(1000*60);
			
			DateFormat dfhm = new SimpleDateFormat("HH:mm");
			DateFormat dfmd = new SimpleDateFormat("MM-dd");
//			if(minutes<60){
//				time=minutes+"分钟前";
//			}else{
//				time="今天 "+dfhm.format(d1);
//			}
//			if(days>=1){
//				time=dfmd.format(d1)+" "+dfhm.format(d1);
//			}
			
			if(minutes<60){
				time=minutes+"分钟前";
			}else{
				time=dfmd.format(d1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return time;
	}

    // 从网络获取时间
    // 格式 yyyy-MM-dd HH:mm:ss
	public static String getTimeFromInternet() {
        try {
            URL url = new URL("http://www.baidu.com");
            URLConnection uc = url.openConnection();
            uc.connect();
            long ld = uc.getDate();
            Date date = new Date(ld);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            return df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isHigherVersion(String newv, String oldv) {
		
		String[] new_v=newv.split("\\.");
		String[] old_v=oldv.split("\\.");
		for(int i=0;i<new_v.length;i++){
			if(Integer.valueOf(old_v[i])< Integer.valueOf(new_v[i])){
				return true;
			}else if(Integer.valueOf(old_v[i])== Integer.valueOf(new_v[i])){
				continue;
			}else{
				break;
			}
		}
		
		return false;
	}

}
