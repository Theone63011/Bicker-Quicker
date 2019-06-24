package purdue.edu.bicker_quicker;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PersistValue extends Activity {
    private static SharedPreferences savedValues;

    public static void createPreferences() {

    }

    public PersistValue(Context context) {
        savedValues = context.getSharedPreferences("savedValues", context.MODE_PRIVATE);

    }

    public static void editBool(String keyName, Boolean boolVariable) {
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putBoolean(keyName, boolVariable);
        editor.commit();
    }

    public static void editString(String keyName, String stringVariable) {
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString(keyName, stringVariable);
        editor.commit();
    }

    public static Boolean getBool(String keyName, Boolean boolVariable) {
        return savedValues.getBoolean(keyName, boolVariable);
    }

    public static String getString(String keyName, String stringVariable) {
        return savedValues.getString(keyName, stringVariable);
    }

}