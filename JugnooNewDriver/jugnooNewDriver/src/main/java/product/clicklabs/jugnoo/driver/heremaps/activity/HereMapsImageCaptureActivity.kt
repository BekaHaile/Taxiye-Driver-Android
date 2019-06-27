package product.clicklabs.jugnoo.driver.heremaps.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_here_maps_image_capture.*
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.Utils
import java.io.File


class HereMapsImageCaptureActivity  : ImageCaptureBaseActivity(){

    companion object {
        var closeActivity = false
    }

    private var fileCaptured:File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_here_maps_image_capture)
        closeActivity = false

        bRetake.setOnClickListener{
            chooseImageFromCamera()
        }
        bUsePhoto.setOnClickListener{
            if(fileCaptured!= null) {
                startActivity(Intent(this@HereMapsImageCaptureActivity, HereMapsFeedbackActivity::class.java)
                        .putExtra(Constants.KEY_FILE_SELECTED, fileCaptured!!.absolutePath)
                        .putExtra(Constants.KEY_LATITUDE, intent.getDoubleExtra(Constants.KEY_LATITUDE, 0.0))
                        .putExtra(Constants.KEY_LONGITUDE, intent.getDoubleExtra(Constants.KEY_LONGITUDE, 0.0))
                )
            } else {
                Utils.showToast(this@HereMapsImageCaptureActivity, getString(R.string.please_capture_an_image_first))
            }
        }

        ivReviewImage.postDelayed({chooseImageFromCamera()}, 500)

    }

    override fun onResume() {
        super.onResume()
        if(closeActivity){
            finish()
            closeActivity = false
        }
    }

    override fun imageCaptured(bitmap: Bitmap?, file: File?) {
        ivReviewImage.setImageBitmap(bitmap)
        fileCaptured = file
    }

    override fun imageError(message: String?) {
        Utils.showToast(this, message, Toast.LENGTH_LONG)
        finish()
    }


}