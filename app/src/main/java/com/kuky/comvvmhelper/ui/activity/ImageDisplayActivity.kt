package com.kuky.comvvmhelper.ui.activity

import android.os.Bundle
import androidx.datastore.preferences.core.*
import com.kk.android.comvvmhelper.anno.ActivityConfig
import com.kk.android.comvvmhelper.extension.otherwise
import com.kk.android.comvvmhelper.extension.safeLaunch
import com.kk.android.comvvmhelper.extension.yes
import com.kk.android.comvvmhelper.helper.ePrint
import com.kk.android.comvvmhelper.ui.BaseActivity
import com.kk.android.comvvmhelper.utils.*
import com.kuky.comvvmhelper.R
import com.kuky.comvvmhelper.databinding.ActivityImageDisplayBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

@ActivityConfig(statusBarColorString = "#008577")
class ImageDisplayActivity : BaseActivity<ActivityImageDisplayBinding>() {

    data class User(val name: String)

    private val mSaveEntity by lazy { intent.getBooleanExtra("switchOn", false) }

    override fun layoutId() = R.layout.activity_image_display

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.imagePath = "https://t7.baidu.com/it/u=2749005241,3756993511&fm=193&f=GIF"

        safeLaunch {
            block = {
                mSaveEntity.yes {
                    saveEntityToDataStore("user", User("kuky"), { ParseUtils.instance().parseToJson(it) })
                    delay(1000)
                    fetchEntityFromDataStore("user", { ParseUtils.instance().parseFromJson(it, User::class.java) })
                        .collectLatest { ePrint { "user: $it" } }
                }.otherwise {
                    saveDataToDataStore("username", "kuky")
                    delay(1_000)
                    fetchDataFromDataStore<String>("username").collectLatest {
                        ePrint { "username: $it" }
                    }
                }

                delay(5_000) // delay enough time to promise last action has completed
                ////////////////////////////////////////////////////////////////////////////////////////////
                // if has more than one data to write or read, do not call createDataStore() many times ////
                // (or before next write or read, you can promise last action has completed), //////////////
                // otherwise will write or read failed /////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////////////////////
                defaultDataStore().apply {
                    edit { store ->
                        store[intPreferencesKey("age")] = 29
                        store[stringPreferencesKey("name")] = "user"
                        store[floatPreferencesKey("weight")] = 70.0f
                    }

                    data.catch {
                        emit(emptyPreferences())
                    }.collectLatest { pref ->
                        ePrint { pref[intPreferencesKey("age")] }
                        ePrint { pref[stringPreferencesKey("name")] }
                        ePrint { pref[floatPreferencesKey("weight")] }
                    }
                }
            }

            onError = { ePrint { "error: $it" } }
        }
    }
}