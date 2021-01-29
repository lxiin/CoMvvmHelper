package com.kuky.comvvmhelper.ui.activity

import android.os.Bundle
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
import kotlinx.coroutines.flow.collectLatest

@ActivityConfig(statusBarColorString = "#008577")
class ImageDisplayActivity : BaseActivity<ActivityImageDisplayBinding>() {

    data class User(val name: String)

    private val mSaveEntity by lazy { intent.getBooleanExtra("switchOn", false) }

    override fun layoutId() = R.layout.activity_image_display

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.imagePath = "https://t7.baidu.com/it/u=4162611394,4275913936&fm=193&f=GIF"

        safeLaunch {
            block = {
                mSaveEntity.yes {
                    saveTransToDataStore("user", User("kuky"), { ParseUtils.instance().parseToJson(it) })
                    delay(1000)
                    fetchTransDataFromDataStore("user", { ParseUtils.instance().parseFromJson(it, User::class.java) })
                        .collectLatest { ePrint { "user: $it" } }
                }.otherwise {
                    saveToDataStore("username", "kuky")
                    delay(1_000)
                    fetchDataStoreData<String>("username").collectLatest {
                        ePrint { "username: $it" }
                    }
                }

            }

            onError = { ePrint { "error: $it" } }
        }
    }
}