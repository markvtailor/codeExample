package com.wallpaperscraft.keby.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.databinding.FragmentWebviewBinding

class WebViewFragment: Fragment(R.layout.fragment_webview) {

    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding!!

    private var web: WebView? = null

    private val args: WebViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        initWebView()

        val link = args.url

        handleLink(link)
    }


    private fun initWebView() {
        web = binding.webView
        web?.settings?.javaScriptEnabled = true

        val webClient: WebViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {

                val requestUrl = request?.url

                return if (requestUrl != null) {
                    view?.loadUrl(requestUrl.toString())
                    true
                } else false
            }
        }

        web?.webViewClient = webClient
    }



    private fun handleLink(link: String) {
        web?.loadUrl(link)
    }
}