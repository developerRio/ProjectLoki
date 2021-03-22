package com.originalstocks.projectloki.data.helpers

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences


object SharedPref {

    const val AUTH_KEY = "authKey"

    private var mSharedPref: SharedPreferences? = null

    fun init(context: Context) {
        if (mSharedPref == null) mSharedPref =
            context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    }

    fun read(key: String?, defValue: String?): String? {
        return mSharedPref!!.getString(key, defValue)
    }

    fun write(key: String?, value: String?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    fun read(key: String?, defValue: Boolean): Boolean {
        return mSharedPref!!.getBoolean(key, defValue)
    }

    fun write(key: String?, value: Boolean) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.apply()
    }

    fun writeSet(key: String?, value: Set<String>) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putStringSet(key, value)
        prefsEditor.apply()
    }

    fun readSet(key: String?, defValue: Set<String>): Set<String>? {
        return mSharedPref!!.getStringSet(key, defValue)
    }

    fun read(key: String?, defValue: Int): Int {
        return mSharedPref!!.getInt(
            key,
            defValue
        )
    }

    fun write(key: String?, value: Int?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putInt(key, value!!).apply()
    }

    fun removeDataByKey(key: String?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.remove(key).apply()
    }

    fun clearData() {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.clear()
        prefsEditor.apply()
    }
}