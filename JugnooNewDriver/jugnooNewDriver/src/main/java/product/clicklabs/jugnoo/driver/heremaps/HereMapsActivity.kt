package product.clicklabs.jugnoo.driver.heremaps

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.Fonts
import product.clicklabs.jugnoo.driver.utils.Log




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



        var jsMap:String = "" +
                "display = new here.xyz.maps.Map(document.getElementById(\"map\"), " +
                "{ zoomLevel: 18, " +
                "center: " +
                "   { longitude: 30.7333, " +
                "       latitude: 76.7794 " +
                "   }, " +
                "layers: " +
                "[imageLayer, herePlacesLayer, customPlacesLayer] " +
                "});"

        wv.postDelayed({
            wv.evaluateJavascript(
                    jsMap,
                    ValueCallback<String> { html ->
                        Log.d("HTML", html)
                        // code here
                    })
        }, 50000)


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