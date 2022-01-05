package com.youreye.bussro.feature.settings

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityWebViewBinding

/**
 * [WebViewActivity]
 * SettingsActivity 에서 사용자가 더 많은 정보를 원할시 띄워줄 WebView
 */

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)

        initVar()
    }

    private fun initVar() {
        intent.getStringExtra("addr")?.let {
            binding.wvWebView.settings.apply {
                useWideViewPort = true
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                setSupportZoom(true)
                builtInZoomControls = true
            }

            binding.wvWebView.apply {
                webViewClient = object: WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        binding.pbWebView.visibility = View.VISIBLE
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        binding.pbWebView.visibility = View.GONE
                    }
                }
                webChromeClient = WebChromeClient()

                loadUrl(it)
            }
        }
    }

    override fun onBackPressed() {
        if (binding.wvWebView.canGoBack()) {
            binding.wvWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}