package com.kuky.comvvmhelper.ui.activity

import android.os.Bundle
import com.kk.android.comvvmhelper.anno.ActivityConfig
import com.kk.android.comvvmhelper.anno.WindowState
import com.kk.android.comvvmhelper.extension.safeLaunch
import com.kk.android.comvvmhelper.helper.ePrint
import com.kk.android.comvvmhelper.ui.BaseActivity
import com.kk.android.comvvmhelper.utils.*
import com.kuky.comvvmhelper.R
import com.kuky.comvvmhelper.databinding.ActivityImageDisplayBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

@ActivityConfig(statusBarColorString = "#008577")
class ImageDisplayActivity : BaseActivity<ActivityImageDisplayBinding>() {

    data class User(val name: String)

    override fun layoutId() = R.layout.activity_image_display

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.imagePath = "https://t7.baidu.com/it/u=4162611394,4275913936&fm=193&f=GIF"

        safeLaunch {
            block = {
                saveToDataStore("username", "kuky")
                saveTransToDataStore("user", User("kuky"), { ParseUtils.instance().parseToJson(it) })

                delay(1000)

                fetchDataStoreData<String>("username")
                fetchTransDataFromDataStore("user", { ParseUtils.instance().parseFromJson(it, User::class.java) })
                    .collect { ePrint { "user: $it" } }
            }

            onError = { ePrint { "error: $it" } }
        }
    }
}