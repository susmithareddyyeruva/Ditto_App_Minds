
package core.language;

import android.content.Context;
import android.content.SharedPreferences;

//Added by vineetha for switch language popup
public class PreferenceManager {

    private static final String SHARED_PREFERENCES_FILE = "languages_";

    public static void saveIntForKey(Context context, String key, int data) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(key, data);
                editor.commit();
            }
        }

    }

    public static void clearsharedpreference(Context context) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

        }

    }


    public static int getIntForKey(Context context, String key, int defaultData) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                return sharedPreferences.getInt(key, defaultData);
            } else {
                return defaultData;
            }
        }
        return defaultData;

    }

    public static void saveBooleanForKey(Context context, String key, boolean data) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(key, data);
//        editor.commit();
                editor.apply();
            }
        }


    }

    public static void saveStringForKey(Context context, String key, String data) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, data);
                editor.commit();
            }
        }

    }

    public static String getStringForKey(Context context, String key, String defaultData) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                return sharedPreferences.getString(key, defaultData);
            } else {
                return defaultData;
            }
        }
        return defaultData;
    }


    public static boolean getBooleanForKey(Context context, String key, boolean defaultData) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                return sharedPreferences.getBoolean(key, defaultData);
            } else {
                return defaultData;
            }
        }
        return defaultData;

    }

    public static void removeStringForKey(Context context, String key) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(key);
                editor.commit();
            }
        }
    }


    public static void saveLongForKey(Context context, String key, long data) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(key, data);
                editor.commit();
            }
        }


    }

    public static long getLongForKey(Context context, String key, long defaultData) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                return sharedPreferences.getLong(key, defaultData);
            } else {
                return defaultData;
            }
        }
        return defaultData;

    }

    public static void saveFloatForKey(Context context, String key, float data) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat(key, data);
                editor.commit();
            }
        }


    }

    public static float getFloatForKey(Context context, String key, float defaultData) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                return sharedPreferences.getFloat(key, defaultData);
            } else {
                return defaultData;
            }
        }
        return defaultData;

    }



   /*void otherdetailsclear(Context context){
       Checkout.clearUserData(context);
       LanguageUtils.Companion.setDefaultLanguage(context);
   }*/


}
