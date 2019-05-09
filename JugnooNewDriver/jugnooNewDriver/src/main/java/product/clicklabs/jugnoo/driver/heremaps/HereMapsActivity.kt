package product.clicklabs.jugnoo.driver.heremaps

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.Fonts
import product.clicklabs.jugnoo.driver.utils.Log


class HereMapsActivity  : BaseFragmentActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_here_maps)

        val tvTitle: TextView = findViewById(R.id.title)
        tvTitle.text = getString(R.string.add_a_place)
        tvTitle.typeface = Fonts.mavenRegular(applicationContext)
        val backBtn = findViewById<View>(R.id.backBtn)
        backBtn.setOnClickListener { onBackPressed() }


        val wv:WebView = findViewById(R.id.wv)
        wv.webViewClient = MyWebViewClient()
        wv.settings.javaScriptEnabled = true

        // Get the Android assets folder path
        val folderPath = "file:android_asset/here/"
        // Get the HTML file name
        val fileName = "index.html"
        // Get the exact file location
        val file = folderPath + fileName
        // Render the HTML file on WebView
        wv.loadUrl(file)

    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
            return
        }
        super.onBackPressed()
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.w("MyWebViewClient", "onPageStarted url = $url")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.w("MyWebViewClient", "onPageFinished url = $url")
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
            Log.w("MyWebViewClient", "onLoadResource url = $url")
        }


    }

}