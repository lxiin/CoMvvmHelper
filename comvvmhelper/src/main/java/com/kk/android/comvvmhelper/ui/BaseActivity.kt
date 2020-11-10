@file:Suppress("MemberVisibilityCanBePrivate")

package com.kk.android.comvvmhelper.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.kk.android.comvvmhelper.anno.ActivityConfig
import com.kk.android.comvvmhelper.anno.StatusBarTextColorMode
import com.kk.android.comvvmhelper.anno.WindowState
import com.kk.android.comvvmhelper.extension.otherwise
import com.kk.android.comvvmhelper.extension.yes
import com.kk.android.comvvmhelper.helper.ActivityStackManager
import com.kk.android.comvvmhelper.helper.KLogger
import com.kk.android.comvvmhelper.utils.setColorForStatusBar
import com.kk.android.comvvmhelper.utils.translucentStatusBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 * @author kuky.
 * @description
 */
abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity(), CoroutineScope by MainScope(), KLogger {
    protected val mBinding: VB by lazy {
        DataBindingUtil.setContentView(this, layoutId()) as VB
    }

    private val mActivityConfig by lazy {
        javaClass.getAnnotation(ActivityConfig::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityStackManager.addActivity(this)
        mBinding.lifecycleOwner = this
        mActivityConfig?.let { config ->
            if (config.windowState == WindowState.TRANSLUCENT_STATUS_BAR) {
                translucentStatusBar(true)
            } else {
                setColorForStatusBar(config.statusBarColor)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility =
                    (config.statusBarTextColorMode == StatusBarTextColorMode.Dark).yes {
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }.otherwise { View.SYSTEM_UI_FLAG_VISIBLE }
            }
        }
        initActivity(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        mBinding.unbind()
        ActivityStackManager.removeActivity(this)
    }

    abstract fun layoutId(): Int

    abstract fun initActivity(savedInstanceState: Bundle?)
}