package com.myjb.mywebview

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.webkit.WebResourceErrorCompat
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_SETTLING
import com.google.android.material.snackbar.Snackbar
import com.myjb.mywebview.databinding.ActivityBottomMainBinding

private const val TAG = "BottomSheetActivity"

class BottomSheetActivity : AppCompatActivity() {

    private val binding: ActivityBottomMainBinding by lazy {
        ActivityBottomMainBinding.inflate(layoutInflater)
    }

    private val standardBottomSheetBehavior by lazy {
        BottomSheetBehavior.from(binding.bottomSheet.root)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.bottom_sheet_close) {
                finish()
                return@setOnMenuItemClickListener true
            }

            return@setOnMenuItemClickListener false
        }

        binding.menuMediaRewind.setOnClickListener { clickRewind() }
        binding.menuMediaPrevious.setOnClickListener { clickPrevious() }
        binding.menuMediaPlay.setOnClickListener { clickPlay() }
        binding.menuMediaNext.setOnClickListener { clickNext() }
        binding.menuMediaFastForward.setOnClickListener { clickFastForward() }

        //TODO Chrome Developer Tools - chrome://inspect
        // https://developer.android.com/reference/android/webkit/WebView#setWebContentsDebuggingEnabled(boolean)
        if (0 != (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        with(binding.webview) {
            settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                cacheMode = WebSettings.LOAD_NO_CACHE

                userAgentString = "userAgent"
            }

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    Log.e(
                        TAG,
                        "[onConsoleMessage] consoleMessage : ${consoleMessage?.message()}, lineNumber : ${consoleMessage?.lineNumber()}, sourceId : ${consoleMessage?.sourceId()}"
                    )
                    return super.onConsoleMessage(consoleMessage)
                }

                @Deprecated("deprecated in java")
                override fun onConsoleMessage(
                    message: String?,
                    lineNumber: Int,
                    sourceID: String?,
                ) {
                    Log.e(
                        TAG,
                        "[onConsoleMessage] message : $message, lineNumber : $lineNumber, sourceId : $sourceID"
                    )
                    @Suppress("deprecation")
                    super.onConsoleMessage(message, lineNumber, sourceID)
                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    Log.w(TAG, "[onProgressChanged] newProgress : $newProgress")
                    super.onProgressChanged(view, newProgress)
                }
            }

            webViewClient = object : WebViewClientCompat() {
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?,
                ) {
                    super.onReceivedSslError(view, handler, error)

                    if (error != null) {
                        Log.e(
                            TAG,
                            "[onReceivedSslError] primaryError : ${error.primaryError}, url : ${error.url}"
                        )
                    }

                    Log.e(TAG, "[onReceivedSslError] error : $error")
                }

                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceErrorCompat,
                ) {
                    super.onReceivedError(view, request, error)

                    if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_RESOURCE_ERROR_GET_CODE)) {
                        Log.e(TAG, "[onReceivedError] errorCode : ${error.errorCode}")
                    }

                    if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_RESOURCE_ERROR_GET_DESCRIPTION)) {
                        Log.e(TAG, "[onReceivedError] description : ${error.description}")
                    }
                }
            }

            addJavascriptInterface(JavascriptInterface(), "Native")

            loadUrl("https://naver.com")
        }

        addBottomSheetCallback()
    }

    inner class JavascriptInterface : BottomSheetJavaBridgeInterface {
        @android.webkit.JavascriptInterface
        override fun init() {
            showSnack("[init]")
        }

        @android.webkit.JavascriptInterface
        override fun close() {
            showSnack("[close]")
        }
    }

    private fun clickRewind() {
        closeSubSettingMenu()

        showSnack("Rewind")
    }

    private fun clickPrevious() {
        closeSubSettingMenu()

        showSnack("Previous")
    }

    private fun clickPlay() {
        //Bottom Sheet : Standard vs Modal
        if (0 != (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)) {
            showStandardBottomSheet()
        } else {
            showModalBottomSheet()
        }

        showSnack("Play")
    }

    private fun clickNext() {
        closeSubSettingMenu()

        showSnack("Next")
    }

    private fun clickFastForward() {
        closeSubSettingMenu()

        showSnack("FastForward")
    }

    private fun apiSubSetting(menu: String) {
        closeSubSettingMenu()

        showSnack("apiSubSetting : $menu")
    }

    private fun showSnack(text: String) {
        Log.e(TAG, "[showSnack] text : $text")

        Snackbar.make(binding.root, "[Inform] $text", Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.menuMediaRewind).show()
    }

    private fun showModalBottomSheet() {
        val modalBottomSheet = BottomSheetDialogFragment().apply {
        }
        modalBottomSheet.show(supportFragmentManager, BottomSheetDialogFragment.TAG)
    }

    private fun showStandardBottomSheet() {
        when (standardBottomSheetBehavior.state) {
            STATE_HIDDEN, STATE_COLLAPSED -> {
                standardBottomSheetBehavior.state = STATE_EXPANDED
            }

            else -> {
                standardBottomSheetBehavior.state = STATE_HIDDEN
            }
        }
    }

    private fun addBottomSheetCallback() {
        val callback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.e(TAG, "[onStateChanged] newState : $newState")

                binding.menuMediaPlay.isSelected =
                    (newState == STATE_SETTLING || newState == STATE_EXPANDED)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                Log.e(TAG, "[onSlide] slideOffset : $slideOffset")
            }
        }
        standardBottomSheetBehavior.addBottomSheetCallback(callback)

        binding.bottomSheet.recycler.apply {
            val bottomSheetDialogAdapter = BottomSheetDialogAdapter(unit = {
                Log.e(TAG, "[BottomSheetDialogAdapter] text : $it")
                apiSubSetting(menu = it)
            })
            layoutManager = LinearLayoutManager(context)

            bottomSheetDialogAdapter.submitList(resources.getStringArray(R.array.arrays_menu).toList())
            adapter = bottomSheetDialogAdapter
        }

        binding.bottomSheet.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.bottom_sheet_close) {
                closeSubSettingMenu()
                return@setOnMenuItemClickListener true
            }

            return@setOnMenuItemClickListener false
        }
    }

    private fun closeSubSettingMenu() {
        standardBottomSheetBehavior.state = STATE_HIDDEN
    }
}