package com.example.chatapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private final SharedPreferences sharedPreferences;

    /**
     * sets sharedPreferences
     *
     * @param context the application Context
     */
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    /**
     * adds a boolean to the shared preferences
     *
     * @param key the boolean key
     * @param value the boolean value
     */
    public void putBoolean(String key, Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * gets the boolean value of the key in the sharedPreferences
     *
     * @param key the boolean key
     * @return the Boolean value of the key
     */
    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * adds a string to the shared preferences
     *
     * @param key the string key
     * @param value the string value
     */
    public void putString(String key, String value){
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * gets the string by key in the sharedPreferences
     *
     * @param key the string key
     * @return the string value with the passed in key
     */
    public String getString(String key){
        return sharedPreferences.getString(key, null);
    }

    /**
     * clears the information within the sharedPreferences
     */
    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
