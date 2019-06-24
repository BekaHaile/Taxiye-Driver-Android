package product.clicklabs.jugnoo.driver.heremaps.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.android.synthetic.main.activity_here_maps.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.HomeActivity.activity
import product.clicklabs.jugnoo.driver.utils.*


class HereMapsActivity : BaseFragmentActivity(){


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

        bAddPlace.setOnClickListener {
            wv.loadUrl("javascript:Android.getIds(getCenter());");

        }
        bAddPlace.visibility = View.GONE

        wv.addJavascriptInterface(CustomJavaScriptInterface(this), "Android");
    }

    inner class CustomJavaScriptInterface(val mContext:Context) {

        var centre:String? = null

        @JavascriptInterface
        fun getIds(centre:String){
            this.centre = centre

            Log.e("CustomJavaScriptInterface", "centre=$centre")
            if(centre.contains(",")){
                val arr = centre.split(",");
                val lat = arr[0].toDouble()
                val lng = arr[1].toDouble()

                startActivity(Intent(mContext, HereMapsImageCaptureActivity::class.java)
                        .putExtra(Constants.KEY_LATITUDE, lat)
                        .putExtra(Constants.KEY_LONGITUDE, lng)
                )
            }
        }

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

                val gpsLatLng = LatLng(intent.getDoubleExtra(Constants.KEY_LATITUDE, 0.0),
                        intent.getDoubleExtra(Constants.KEY_LONGITUDE, 0.0))

                val boundsBuilder = LatLngBounds.Builder()
                boundsBuilder.include(gpsLatLng)
                val bounds:LatLngBounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(boundsBuilder, 15.0)


                jsMap = jsMap.replace("<here_auth_identifier>",
                        Prefs.with(this@HereMapsActivity).getString(Constants.DRIVER_HERE_AUTH_IDENTIFIER,
                                getString(R.string.driver_here_auth_identifier)))
                jsMap = jsMap.replace("<here_auth_secret>",
                        Prefs.with(this@HereMapsActivity).getString(Constants.DRIVER_HERE_AUTH_SECRET,
                                getString(R.string.driver_here_auth_secret)))

                jsMap = jsMap.replace("<gps_latitude>", gpsLatLng.latitude.toString())
                jsMap = jsMap.replace("<gps_longitude>", gpsLatLng.longitude.toString())

                jsMap = jsMap.replace("<ne_lat>", bounds.northeast.latitude.toString())
                jsMap = jsMap.replace("<ne_lng>", bounds.northeast.longitude.toString())
                jsMap = jsMap.replace("<sw_lat>", bounds.southwest.latitude.toString())
                jsMap = jsMap.replace("<sw_lng>", bounds.southwest.longitude.toString())

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