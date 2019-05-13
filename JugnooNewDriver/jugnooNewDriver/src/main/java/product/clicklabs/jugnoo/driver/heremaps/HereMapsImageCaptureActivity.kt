package product.clicklabs.jugnoo.driver.heremaps

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Toast
import com.kbeanie.multipicker.api.CacheLocation
import com.kbeanie.multipicker.api.CameraImagePicker
import com.kbeanie.multipicker.api.Picker
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback
import com.kbeanie.multipicker.api.entity.ChosenImage
import kotlinx.android.synthetic.main.activity_here_maps_image_capture.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.*
import product.clicklabs.jugnoo.driver.utils.PermissionCommon.REQUEST_CODE_CAMERA
import java.io.File


class HereMapsImageCaptureActivity  : BaseFragmentActivity(), PermissionCommon.PermissionListener, ImagePickerCallback{

    private var mPermissionCommon: PermissionCommon? = null
    private var mCameraImagePicker: CameraImagePicker? = null
    private var imgPixel = 1600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_here_maps_image_capture)

        bRetake.setOnClickListener{
            chooseImageFromCamera()
        }
        bUsePhoto.setOnClickListener{

        }

        ivReviewImage.postDelayed({chooseImageFromCamera()}, 500)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                getMCameraImagePicker().submit(data)
            }
        } catch (e: Exception) {}
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (mPermissionCommon != null) {
            mPermissionCommon!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun permissionGranted(requestCode: Int) {
        if (requestCode == REQUEST_CODE_CAMERA) run {
            getMCameraImagePicker().pickImage()
        }
    }

    override fun permissionDenied(requestCode: Int, neverAsk: Boolean): Boolean {
        finish()
        return false
    }

    override fun onRationalRequestIntercepted() {
    }


    private fun chooseImageFromCamera() {

        if (mPermissionCommon == null) {
            mPermissionCommon = PermissionCommon(this).setCallback(this)
        }

        mPermissionCommon!!.getPermission(REQUEST_CODE_CAMERA, android.Manifest.permission.CAMERA)
    }

    private fun getMCameraImagePicker(): CameraImagePicker {
        if (mCameraImagePicker == null) {
            mCameraImagePicker = CameraImagePicker(this)
            mCameraImagePicker!!.setCacheLocation(CacheLocation.INTERNAL_APP_DIR)
            mCameraImagePicker!!.setImagePickerCallback(this)
            mCameraImagePicker!!.shouldGenerateThumbnails(false)
            mCameraImagePicker!!.shouldGenerateMetadata(false)
        }
        return mCameraImagePicker!!
    }


    override fun onImagesChosen(list: MutableList<ChosenImage>?) {
        if (list != null && list.size > 0) {
            runOnUiThread(Runnable {
                Log.v("onImageChosen called", "onImageChosen called")
                try {
                    val image = list!!.get(0)
                    if (image != null) {

                        val opt: BitmapFactory.Options
                        opt = BitmapFactory.Options()
                        opt.inTempStorage = ByteArray(16 * 1024)
                        val height11 = image.height
                        val width11 = image.width
                        val mb = (width11 * height11 / 1024000).toFloat()

                        if (mb > 4f)
                            opt.inSampleSize = 4
                        else if (mb > 3f)
                            opt.inSampleSize = 2

                        opt.inPreferredConfig = Bitmap.Config.ARGB_8888
                        val bitmap = BitmapFactory.decodeFile(image.originalPath, opt)

                        val uri = PhotoProvider.getPhotoUri(File(image.originalPath))
                        val rotate = Utils.getCameraPhotoOrientation(this, uri, image.originalPath)
                        val rotatedBitmap: Bitmap?
                        val rotateMatrix = Matrix()
                        rotateMatrix.postRotate(rotate.toFloat())
                        rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotateMatrix, false)


                        var newBitmap: Bitmap? = null
                        if (rotatedBitmap != null) {
                            val oldHeight = rotatedBitmap.height.toDouble()
                            val oldWidth = rotatedBitmap.width.toDouble()

                            if (oldWidth > oldHeight) {
                                val newHeight = imgPixel
                                val newWidth = (oldWidth / oldHeight * imgPixel).toInt()
                                newBitmap = Utils.getResizedBitmap(rotatedBitmap, newHeight, newWidth)
                            } else {
                                val newWidth = imgPixel
                                val newHeight = (oldHeight / oldWidth * imgPixel).toInt()
                                newBitmap = Utils.getResizedBitmap(rotatedBitmap, newHeight, newWidth)
                            }
                        }

                        val f: File
                        if (newBitmap != null) {
                            f = Utils.compressToFile(this, newBitmap, Bitmap.CompressFormat.JPEG, 100)
                            ivReviewImage.setImageBitmap(newBitmap)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        }
    }

    override fun onError(message: String?) {
        Utils.showToast(this, message, Toast.LENGTH_LONG)
        finish()
    }


}