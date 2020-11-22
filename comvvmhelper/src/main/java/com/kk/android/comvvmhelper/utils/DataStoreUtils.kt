package com.kk.android.comvvmhelper.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * @author kuky.
 * @description
 */
fun Context.defaultDataStore(): DataStore<Preferences> =
    createDataStore(name = "${packageName}_data_store")

/**
 * only support Int, Long, Boolean, Float, Double, String
 */
inline fun <reified T : Any> Context.fetchDataStoreData(
    keyName: String, noinline default: (() -> T?)? = null
): Flow<T?> = defaultDataStore().data.catch {
    emit(emptyPreferences())
}.map { pref ->
    pref[preferencesKey<T>(keyName)] ?: default?.invoke()
}

/**
 * only support Int, Long, Boolean, Float, Double, String
 */
suspend inline fun <reified T : Any> Context.saveToDataStore(keyName: String, value: T) =
    defaultDataStore().edit { store ->
        store[preferencesKey<T>(keyName)] = value
    }

/**
 * T: class Type
 * @param trans the func translating nonnull String value to instance of T
 */
inline fun <reified T : Any> Context.fetchTransDataFromDataStore(
    keyName: String, noinline trans: (String) -> T?, noinline default: (() -> T?)? = null
): Flow<T?> = defaultDataStore().data.catch {
    emit(emptyPreferences())
}.map { pref ->
    trans.invoke(pref[preferencesKey(keyName)] ?: "") ?: default?.invoke()
}

/**
 * @param trans the func translating instance of T to String
 */
suspend inline fun <reified T : Any> Context.saveTransToDataStore(
    keyName: String, value: T, noinline trans: (T?) -> String = { Gson().toJson(it) }
) {
    defaultDataStore().edit { store ->
        store[preferencesKey<String>(keyName)] = trans(value)
    }
}