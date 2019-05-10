package product.clicklabs.jugnoo.driver.heremaps

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.Fonts
import product.clicklabs.jugnoo.driver.utils.Log
import product.clicklabs.jugnoo.driver.utils.Utils


class HereMapsActivity  : BaseFragmentActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(product.clicklabs.jugnoo.driver.R.layout.activity_here_maps)

        val tvTitle: TextView = findViewById(product.clicklabs.jugnoo.driver.R.id.title)
        tvTitle.text = getString(product.clicklabs.jugnoo.driver.R.string.add_a_place)
        tvTitle.typeface = Fonts.mavenRegular(applicationContext)
        val backBtn = findViewById<View>(product.clicklabs.jugnoo.driver.R.id.backBtn)
        backBtn.setOnClickListener { onBackPressed() }


        val wv:WebView = findViewById(product.clicklabs.jugnoo.driver.R.id.wv)
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



        var jsMap:String = Utils.readFileFromAssets(this, "here/js/app.js")

        jsMap = jsMap.replace("<gps_latitude>", intent.getDoubleExtra(Constants.KEY_LATITUDE, 0.0).toString())
        jsMap = jsMap.replace("<gps_longitude>", intent.getDoubleExtra(Constants.KEY_LONGITUDE, 0.0).toString())

        wv.postDelayed({
            wv.evaluateJavascript(
                    jsMap,
                    ValueCallback<String> { html ->
                        Log.d("HTML", html)
                        // code here
                    })

        }, 500)

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