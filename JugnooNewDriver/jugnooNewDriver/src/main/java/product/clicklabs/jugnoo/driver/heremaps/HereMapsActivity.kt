package product.clicklabs.jugnoo.driver.heremaps

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_here_maps.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.Fonts
import product.clicklabs.jugnoo.driver.utils.Log
import product.clicklabs.jugnoo.driver.utils.Utils


class HereMapsActivity  : BaseFragmentActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_here_maps)

        val tvTitle: TextView = findViewById(R.id.title)
        tvTitle.text = getString(R.string.add_a_place)
        tvTitle.typeface = Fonts.mavenRegular(applicationContext)
        val backBtn = findViewById<View>(R.id.backBtn)
        backBtn.setOnClickListener { onBackPressed() }


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

        bAddPlace.setOnClickListener{
            startActivity(Intent(this@HereMapsActivity, HereMapsImageCaptureActivity::class.java))
        }
        bAddPlace.visibility = View.GONE

    }

    var scriptRun:Boolean = false
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
            if(!scriptRun && url.equals("https://xyz.api.here.com/maps/latest/xyz-maps-display.min.js")){

                var jsMap:String = Utils.readFileFromAssets(this@HereMapsActivity, "here/js/app.js")

                jsMap = jsMap.replace("<gps_latitude>", intent.getDoubleExtra(Constants.KEY_LATITUDE, 0.0).toString())
                jsMap = jsMap.replace("<gps_longitude>", intent.getDoubleExtra(Constants.KEY_LONGITUDE, 0.0).toString())

                wv.postDelayed({
                    wv.evaluateJavascript(
                            jsMap,
                            ValueCallback<String> { html ->
                                Log.d("HTML", html)
                                // code here
                                bAddPlace.visibility = View.VISIBLE
                            })

                }, 2000)
                scriptRun = true
            }
        }


    }

}