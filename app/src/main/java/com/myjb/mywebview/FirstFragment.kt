package com.myjb.mywebview

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.webkit.WebViewClientCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.myjb.mywebview.databinding.FragmentFirstBinding
import org.json.JSONObject

private const val TAG = "FirstFragment"

class FirstFragment : Fragment() {

    private val binding: FragmentFirstBinding by lazy {
        FragmentFirstBinding.inflate(layoutInflater)
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
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
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
            webViewClient = object : WebViewClientCompat() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest,
                ): Boolean {
                    val result = super.shouldOverrideUrlLoading(view, request)
                    Log.e(TAG, "[shouldOverrideUrlLoading] result : $result")
                    return result
                }
            }

            addJavascriptInterface(JavaBridgeInterface(::showSnackBar), "Native")

            loadUrl(getAssetUrl())
        }
    }

    private fun getAssetUrl(): String {
        return "file:///android_asset/first.html"
    }

    private fun showSnackBar(webViewData: WebViewData) {
        Snackbar.make(
            binding.root,
            "title : ${webViewData.title}\ndescription : ${webViewData.description}",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    class JavaBridgeInterface(val showToast: (WebViewData) -> Unit) : FirstJavaBridgeInterface {
        @android.webkit.JavascriptInterface
        override fun messageAsEmpty() {
            Log.e(TAG, "[messageAsEmpty]")

            showToast(WebViewData(title = "Not text", "The bridge was called."))
        }

        @android.webkit.JavascriptInterface
        override fun messageAsText(text: String) {
            Log.e(TAG, "[messageAsText] text: : $text")

            showToast(
                WebViewData(
                    title = text,
                    "The bridge passed parameters in plain text format."
                )
            )
        }

        @android.webkit.JavascriptInterface
        override fun messageAsJson(json: String) {
            Log.e(TAG, "[messageAsJson] json: : $json")

            //TODO use JSONObject
            val usingJSONObject = JSONObject(json)

            //TODO use Gson converter
            val usingGson = Gson().fromJson(json, WebViewData::class.java)
            showToast(usingGson)
        }
    }
}