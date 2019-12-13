package itk.jy.real_investigate.Preference;

import android.content.Context;
import android.content.SharedPreferences;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class ConfigPreference {
    //항공사진, 배경사진, 하이브리드 종류 get
    public static String getSelectMap(Context context) {
        SharedPreferences prefer = getDefaultSharedPreferences(context);
        return prefer.getString("selectMap","PHOTO");
    }
    //항공사진, 배경사진, 하이브리드 종류 set
    public static void setSelectMap(Context context, String value) {
        SharedPreferences prefer = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefer.edit();
        editor.putString("selectMap", value);
        editor.apply();
    }

    //GPS 고정 여부 get
    public static String getGpsFix(Context context) {
        SharedPreferences prefer = getDefaultSharedPreferences(context);
        return prefer.getString("selectGPSFix","NONFIX");
    }
    //GPS 고정 여부 set
    public static void setGpsFix(Context context, String value) {
        SharedPreferences prefer = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefer.edit();
        editor.putString("selectGPSFix", value);
        editor.apply();
    }

    //지적선 출력 여부 get
    public static boolean getJijuk(Context context) {
        SharedPreferences prefer = getDefaultSharedPreferences(context);
        return prefer.getBoolean("jjk_OnOff",true);
    }
    //지적선 출력 여부 set
    public static void setJijuk(Context context, boolean value) {
        SharedPreferences prefer = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefer.edit();
        editor.putBoolean("jjk_OnOff", value);
        editor.apply();
    }

    //지번 출력 여부 get
    public static boolean getJibun(Context context) {
        SharedPreferences prefer = getDefaultSharedPreferences(context);
        return prefer.getBoolean("jbn_OnOff",true);
    }
    //지번 출력 여부 set
    public static void setJibun(Context context, boolean value) {
        SharedPreferences prefer = getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefer.edit();
        editor.putBoolean("jbn_OnOff", value);
        editor.apply();
    }

}
