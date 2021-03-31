package product.clicklabs.jugnoo.driver.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.github.gcacace.signaturepad.views.SignaturePad
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_customer_signature.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin
import product.clicklabs.jugnoo.driver.utils.AppStatus
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Fonts
import product.clicklabs.jugnoo.driver.utils.Utils
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.MultipartTypedOutput
import retrofit.mime.TypedFile
import retrofit.mime.TypedString
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CustomerSignatureFragment : BaseFragment() {
    private val TAG = AddSignatureFragment::class.java.simpleName

    private var isSigned = false

    private var signatureFile: File? = null

    private var engagementID:Int = 0
    private var deliveryID:Int = 0

    private var callback: Callback? = null

    override fun getTitle(): String {
        return getString(R.string.customer_signature)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Callback){
            callback = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parentView = inflater.inflate(R.layout.fragment_customer_signature, container, false)

        (parentView.findViewById(R.id.title) as TextView).setText(R.string.customer_signature)

        arguments?.run{
            engagementID = getInt(Constants.KEY_ENGAGEMENT_ID)
            deliveryID = getInt(Constants.KEY_DELIVERY_ID)
        }

        return parentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvSignaturePlaceholder.typeface = Fonts.mavenMedium(requireContext())

        backBtn.setOnClickListener { backPress() }
        bSubmit.setOnClickListener{
            Utils.hideSoftKeyboard(requireActivity(), etCustomerName)
            verifyAndSendUserSignature()
        }
        tvReset.setOnClickListener{
            spSignaturePad.clear()
        }
        spSignaturePad.setOnSignedListener(object: SignaturePad.OnSignedListener{
            override fun onStartSigning() {
                tvReset.visibility = View.VISIBLE
                tvSignaturePlaceholder.visibility = View.GONE
            }

            override fun onClear() {
                tvReset.visibility = View.GONE
                tvSignaturePlaceholder.visibility = View.VISIBLE
                isSigned = false
            }

            override fun onSigned() {
                isSigned = true
            }

        })

        etCustomerName.setOnEditorActionListener { v, actionId, event ->
            Utils.hideSoftKeyboard(requireActivity(), etCustomerName)
            false
        }

        llContent.visibility = View.GONE
        scrollView.post{
            llContent.visibility = View.VISIBLE
            val params = rlSignaturePad.layoutParams as LinearLayout.LayoutParams
            params.height = scrollView.measuredHeight - Utils.dpToPx(requireContext(), 175F)
            rlSignaturePad.layoutParams = params
        }

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }




    /**
     * Method to verify whether the User has signed
     */
    private fun verifyAndSendUserSignature() {
        if (!isSigned) {
            Toast.makeText(activity, R.string.provide_sign_msg, Toast.LENGTH_LONG).show()
            return
        }
        if (etCustomerName!!.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
            Toast.makeText(activity, R.string.pls_type_name, Toast.LENGTH_LONG).show()
            return
        }
        try {
            saveUserSignature(etCustomerName.text.toString().trim())
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }




    private fun backPress() {
        callback?.deliverySignatureBackPressed()
    }

    override fun onPause() {
        super.onPause()
        backPress()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }



    @Throws(OutOfMemoryError::class, IOException::class)
    private fun saveUserSignature(name:String) {

        // Create a file to write bitmap data
        val bImage = spSignaturePad.signatureBitmap
        if (bImage == null) {
            Toast.makeText(requireContext(), R.string.unable_to_capture_signature, Toast.LENGTH_SHORT).show()
            return
        }

        signatureFile = compressBitmapToFile(requireContext(), bImage, CompressFormat.JPEG, 100, "customer_signature.jpg")

        uploadPicToServer(signatureFile!!, name)
    }

    private fun uploadPicToServer(signatureFile: File, recipientName:String){
        if (AppStatus.getInstance(requireContext()).isOnline(requireContext())) {
            DialogPopup.showLoadingDialog(requireContext(), getString(R.string.uploading))

            val multipartTypedOutput = MultipartTypedOutput()
            multipartTypedOutput.addPart(Constants.KEY_ORDER_IMAGES, TypedFile("image/*", signatureFile))
            multipartTypedOutput.addPart(Constants.KEY_ENGAGEMENT_ID, TypedString(engagementID.toString()))
            multipartTypedOutput.addPart(Constants.KEY_RECIPIENT_NAME, TypedString(recipientName))
            if (deliveryID != 0) {
                multipartTypedOutput.addPart(Constants.KEY_DELIVERY_ID, TypedString(deliveryID.toString()))
            }
            multipartTypedOutput.addPart(Constants.KEY_ACCESS_TOKEN, TypedString(Data.userData.accessToken))
            HomeUtil.putDefaultParams(multipartTypedOutput)

            RestClient.getApiServices().uploadImagesRide(multipartTypedOutput, object : retrofit.Callback<FeedCommonResponseKotlin?> {
                override fun success(feedCommonResponseKotlin: FeedCommonResponseKotlin?, response: Response) {
                    DialogPopup.dismissLoadingDialog()
                    if(feedCommonResponseKotlin == null){
                        return
                    }
                    if(feedCommonResponseKotlin.flag == ApiResponseFlags.ACTION_COMPLETE.kOrdinal){
                        Utils.showToast(requireContext(), feedCommonResponseKotlin.message)
                        callback?.markDeliverySignatureTaken(engagementID, deliveryID, recipientName)
                    } else {
                        DialogPopup.alertPopup(requireActivity(), "", feedCommonResponseKotlin.message)
                    }
                }

                override fun failure(error: RetrofitError) {
                    DialogPopup.alertPopup(requireActivity(), "", Data.SERVER_NOT_RESOPNDING_MSG)
                    DialogPopup.dismissLoadingDialog()
                }
            })


        } else {
            DialogPopup.alertPopup(requireActivity(), "", Data.CHECK_INTERNET_MSG)
        }
    }


    private fun compressBitmapToFile(context: Context, src: Bitmap, format: CompressFormat,
                               quality: Int, fileName: String): File? {
        val bos = ByteArrayOutputStream()
        src.compress(format, quality, bos)
        val f = File(context.filesDir, fileName)
        try {
            f.createNewFile()
            val fos = FileOutputStream(f)
            fos.write(bos.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return f
    }

    interface Callback{
        fun markDeliverySignatureTaken(engagementId: Int, deliveryId: Int, recipientName: String?)
        fun deliverySignatureBackPressed()
    }

    companion object{

        fun newInstance(engagementId:Int, deliveryId:Int):CustomerSignatureFragment{
            val fragment = CustomerSignatureFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_ENGAGEMENT_ID, engagementId)
            bundle.putInt(Constants.KEY_DELIVERY_ID, deliveryId)

            fragment.arguments = bundle
            return fragment
        }

    }
}