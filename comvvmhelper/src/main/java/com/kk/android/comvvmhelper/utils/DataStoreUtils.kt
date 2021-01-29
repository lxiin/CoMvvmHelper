package com.kk.android.comvvmhelper.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * @author kuky.
 * @description
 */
fun Context.defaultDataStore(): DataStore<Preferences> =
    createDataStore(name = "${packageName}_data_store")

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> preferencesKey(name: String): Preferences.Key<T> {
    return when (T::class) {
        Int::class -> intPreferencesKey(name)
        String::class -> stringPreferencesKey(name)
        Boolean::class -> booleanPreferencesKey(name)
        Float::class -> floatPreferencesKey(name)
        Long::class -> longPreferencesKey(name)
        Double::class -> doublePreferencesKey(name)
        Set::class -> throw IllegalArgumentException("Use `preferencesSetKey` to create keys for Sets. ")
        else -> throw IllegalArgumentException("Type not supported: ${T::class.java}")
    } as Preferences.Key<T>
}

/**
 * only support Int, Long, Boolean, Float, Double, String
 */
@OptIn(ExperimentalCoroutinesApi::class)
inline fun <reified T : Any> Context.fetchDataStoreData(
    keyName: String, noinline default: (() -> T?)? = null
): Flow<T?> = channelFlow {
    defaultDataStore().data.catch {
        emit(emptyPreferences())
    }.map { pref ->
        pref[preferencesKey<T>(keyName)] ?: default?.invoke()
    }.collectLatest { send(it) }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun Context.fetchStringSetData(keyName: String, default: (() -> Set<String>?)? = null):
        Flow<Set<String>> = channelFlow {
    defaultDataStore().data.catch {
        emit(emptyPreferences())
    }.map { pref ->
        pref[stringSetPreferencesKey(keyName)] ?: default?.invoke() ?: mutableSetOf()
    }.collectLatest { send(it) }
}

/**
 * only support Int, Long, Boolean, Float, Double, String
 */
suspend inline fun <reified T : Any> Context.saveToDataStore(keyName: String, value: T) =
    defaultDataStore().edit { store ->
        store[preferencesKey<T>(keyName)] = value
    }

suspend fun Context.saveStringSetToDataStore(keyName: String, value: Set<String>? = null) =
    defaultDataStore().edit { store ->
        store[stringSetPreferencesKey(keyName)] = value ?: mutableSetOf()
    }

/**
 * T: class Type
 * @param trans the func translating nonnull String value to instance of T
 */
@OptIn(ExperimentalCoroutinesApi::class)
inline fun <reified T : Any> Context.fetchTransDataFromDataStore(
    keyName: String, noinline trans: (String) -> T?, noinline default: (() -> T?)? = null
): Flow<T?> = channelFlow {
    defaultDataStore().data.catch {
        emit(emptyPreferences())
    }.map { pref ->
        trans.invoke(pref[preferencesKey(keyName)] ?: "") ?: default?.invoke()
    }.collectLatest { send(it) }
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