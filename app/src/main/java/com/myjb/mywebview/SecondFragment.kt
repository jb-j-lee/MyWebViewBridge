package com.myjb.mywebview

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.webkit.WebResourceErrorCompat
import androidx.webkit.WebResourceRequestCompat
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewFeature
import com.myjb.mywebview.databinding.FragmentSecondBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KSuspendFunction0

private const val TAG = "SecondFragment"

class SecondFragment : Fragment() {

    private val binding: FragmentSecondBinding by lazy {
        FragmentSecondBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        //TODO Chrome Developer Tools - chrome://inspect
        // https://developer.android.com/reference/android/webkit/WebView#setWebContentsDebuggingEnabled(boolean)
        if (0 != (requireActivity().applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        binding.webview.settings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = false
            useWideViewPort = false
            cacheMode = WebSettings.LOAD_NO_CACHE

            userAgentString = "userAgent"
        }

        with(binding.webview) {
            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message?,
                ): Boolean {
                    val result = super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
                    Log.e(
                        TAG,
                        "[onCreateWindow] result : $result, isDialog : $isDialog, isUserGesture : $isUserGesture, resultMsg : $resultMsg"
                    )
                    return result
                }

                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    Log.e(
                        TAG,
                        "[onConsoleMessage] consoleMessage : ${consoleMessage?.message()}, lineNumber : ${consoleMessage?.lineNumber()}, sourceId : ${consoleMessage?.sourceId()}"
                    )
                    return super.onConsoleMessage(consoleMessage)
                }

                override fun onJsConfirm(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?,
                ): Boolean {
                    Log.e(TAG, "[onJsConfirm] url : $url, message : $message, result : $result")
                    return super.onJsConfirm(view, url, message, result)
                }

                override fun onJsBeforeUnload(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?,
                ): Boolean {
                    Log.e(
                        TAG, "[onJsBeforeUnload] url : $url, message : $message, result : $result"
                    )
                    return super.onJsBeforeUnload(view, url, message, result)
                }

                override fun onJsPrompt(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    defaultValue: String?,
                    result: JsPromptResult?,
                ): Boolean {
                    Log.e(TAG, "[onJsPrompt] url : $url, message : $message, result : $result")
                    return super.onJsPrompt(view, url, message, defaultValue, result)
                }

                override fun onJsAlert(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?,
                ): Boolean {
                    Log.e(TAG, "[onJsAlert] url : $url, message : $message, result : $result")
                    return super.onJsAlert(view, url, message, result)
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
                    @Suppress("deprecation") super.onConsoleMessage(message, lineNumber, sourceID)
                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    Log.w(TAG, "[onProgressChanged] newProgress : $newProgress")
                    super.onProgressChanged(view, newProgress)
                }

                override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                    Log.w(TAG, "[onReceivedIcon]")
                    super.onReceivedIcon(view, icon)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    Log.w(TAG, "[onReceivedTitle] title : $title")
                    super.onReceivedTitle(view, title)
                }

                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    Log.e(TAG, "[onShowCustomView]")
                    super.onShowCustomView(view, callback)
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?,
                ): Boolean {
                    Log.e(TAG, "[onShowFileChooser]")
                    return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
                }
            }

            webViewClient = object : WebViewClientCompat() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest,
                ): Boolean {
                    val isRedirect = try {
                        if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_RESOURCE_REQUEST_IS_REDIRECT)) {
                            WebResourceRequestCompat.isRedirect(request)
                        } else {
                            false
                        }
                    } catch (e: UnsupportedOperationException) {
                        e.printStackTrace()
                        false
                    }
                    Log.e(
                        TAG,
                        "[shouldOverrideUrlLoading] view url : ${view.url}, request url : ${request.url}"
                    )
                    Log.e(TAG, "[shouldOverrideUrlLoading] isRedirect : $isRedirect")

                    val url = request.url.toString()
                    val result = !(url.startsWith("http://") || url.startsWith("https://"))
                    Log.e(TAG, "[shouldOverrideUrlLoading] result : $result")
                    return result
                }

                override fun onLoadResource(view: WebView?, url: String?) {
                    Log.i(TAG, "[onLoadResource] url : $url")

                    super.onLoadResource(view, url)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    Log.e(TAG, "[onPageStarted] url : $url")
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    Log.e(TAG, "[onPageFinished] url : $url")
                    super.onPageFinished(view, url)
                }

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

            addJavascriptInterface(JavaBridgeInterface(::showSnackBar, ::receiveMessage), "Native")

            loadUrl(getAssetUrl())
        }
    }

    private fun getAssetUrl(): String {
        return "file:///android_asset/second.html"
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, "Bridge : $text", Snackbar.LENGTH_SHORT).show()
    }

    private suspend fun receiveMessage() {
        Log.e(TAG, "[receiveMessage]")

        suspendCoroutine {
            binding.webview.evaluateJavascript("javascript:receiveMessage()") { callBack ->
                Log.e(TAG, "[receiveMessage] callBack : $callBack")

                showSnackBar("receiveMessage")
                it.resume(callBack)
            }
        }
    }

    class JavaBridgeInterface(
        val showSnackBar: (String) -> Unit,
        val next: KSuspendFunction0<Unit>,
    ) : SecondJavaBridgeInterface {
        @android.webkit.JavascriptInterface
        override fun init() {
            Log.e(TAG, "[init]")

            showSnackBar("init")
        }

        @android.webkit.JavascriptInterface
        override fun change(status: String) {
            Log.e(TAG, "[change] status: : $status")

            showSnackBar(status)

            CoroutineScope(Dispatchers.Main).launch {
                next()
            }
        }

        @android.webkit.JavascriptInterface
        override fun callback(message: String) {
            Log.e(TAG, "[callback] message: : $message")
        }

        @android.webkit.JavascriptInterface
        override fun close() {
            Log.e(TAG, "[close]")

            showSnackBar("close")
        }
    }
}