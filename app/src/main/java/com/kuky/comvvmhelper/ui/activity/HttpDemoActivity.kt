package com.kuky.comvvmhelper.ui.activity

import android.os.Bundle
import android.os.Environment
import android.view.ViewGroup
import com.kk.android.comvvmhelper.anno.ActivityConfig
import com.kk.android.comvvmhelper.anno.PublicDirectoryType
import com.kk.android.comvvmhelper.extension.*
import com.kk.android.comvvmhelper.helper.*
import com.kk.android.comvvmhelper.listener.OnErrorReloadListener
import com.kk.android.comvvmhelper.ui.BaseActivity
import com.kk.android.comvvmhelper.utils.dp2px
import com.kk.android.comvvmhelper.widget.RequestStatusCode
import com.kuky.comvvmhelper.R
import com.kuky.comvvmhelper.databinding.ActivityHttpDemoBinding
import com.kuky.comvvmhelper.helper.ApiService
import com.kuky.comvvmhelper.viewmodel.HttpViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

@ActivityConfig(statusBarColorString = "#008577")
class HttpDemoActivity : BaseActivity<ActivityHttpDemoBinding>() {

    private val mEnabledDownload by lazy { intent.getBooleanExtra("switchOn", false) }

    private var mRequestJob: Job? = null

    private val mViewModel by viewModel<HttpViewModel>()

    override fun layoutId() = R.layout.activity_http_demo

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.holder = this
        mBinding.showDownload = mEnabledDownload
        mBinding.requestCode = RequestStatusCode.Loading
        mBinding.reload = OnErrorReloadListener { requestByRetrofit() }
        mBinding.scroller.layoutParams = (mBinding.scroller.layoutParams as ViewGroup.MarginLayoutParams)
            .apply {
                topMargin = mEnabledDownload.yes { 60f.dp2px().toInt() }.otherwise { 0 }
            }

        delayLaunch(1_000) { requestByRetrofit() }

        // ViewModel Manager Pool
        mViewModel.getSingleLiveEvent<String>("modelText").run {
            observe(this@HttpDemoActivity, { ePrint { it } })

            postValue("Model Text ABC")
        }
    }

    fun download() {
        mRequestJob?.cancel()
        mRequestJob = launch {
            toast("start download")

            DownloadHelper.instance(this@HttpDemoActivity).simpleDownload {
                downloadUrl = "https://t7.baidu.com/it/u=4162611394,4275913936&fm=193&f=GIF"

                storedFilePath = File(Environment.DIRECTORY_DCIM, "$packageName/a.jpg").absolutePath

                qWrapper = QWrapper("a.jpg", "${Environment.DIRECTORY_DCIM}/$packageName", downloadType = PublicDirectoryType.PICTURES)

                onProgressChange = { ePrint { "progress: $it" } }

                onDownloadFinished = { toast("download finished") }

                onDownloadFailed = { toast("download failed") }
            }
        }
    }

    private fun requestByHttp() {
        mRequestJob?.cancel()
        mRequestJob = launch(Dispatchers.IO) {
            http {
                baseUrl = "https://github.com/kukyxs"

                onSuccess = {
                    mBinding.requestResult = it.checkText().renderHtml().toString()
                    mBinding.requestCode = RequestStatusCode.Succeed
                }

                onFail = {
                    mBinding.requestCode = RequestStatusCode.Error
                }
            }
        }
    }

    private fun requestByRetrofit() {
        mRequestJob?.cancel()
        mRequestJob = covLaunch(Dispatchers.IO, onRun = {
            val result = createService<ApiService>().requestRepositoryInfo()
            workOnMain {
                mBinding.requestCode = (result.errorCode == 0).yes {
                    mBinding.requestResult = result.toString()
                    RequestStatusCode.Succeed
                }.otherwise { RequestStatusCode.Error }

                workOnIO {
                    val tops = createService<ApiService>().requestTop("ef69c9ea662b4ca4ac768d4f70b921af")
                    ePrint { tops }
                }
            }
        }, onError = { _, throwable ->
            ePrint { throwable }
            mBinding.requestCode = RequestStatusCode.Error
            launch(Dispatchers.Main) { longToast(throwable.message ?: "") }
        })
    }
}