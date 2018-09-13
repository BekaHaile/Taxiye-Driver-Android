package product.clicklabs.jugnoo.driver.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import com.squareup.picasso.RoundBorderTransform
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.document_details.*
import org.json.JSONArray
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.DocumentListFragment
import product.clicklabs.jugnoo.driver.DriverDocumentActivity
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.DocInfo
import product.clicklabs.jugnoo.driver.ui.DriverSetupFragment
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.inflate
import product.clicklabs.jugnoo.driver.utils.pxValue
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Parminder Saini on 12/09/18.
 */

const val ARGS_DOC_INFO = "args_doc_info"


class DocumentDetailsFragment:Fragment(){


    companion object {
        @JvmStatic
        fun newInstance(accessToken: String,docInfo: DocInfo) =
                DocumentDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                        putParcelable(ARGS_DOC_INFO, docInfo)
                    }
                }
    }

    private var TAG = DocumentDetailsFragment::class.qualifiedName
    private val  inputTypeMap = mapOf("date" to InputType.TYPE_CLASS_DATETIME,"varchar" to InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
    private val  gson = Gson()
    private  lateinit var docInfo:DocInfo
    private  lateinit var accessToken: String

    val documentInputFields = arrayListOf<DocumentInputField>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        docInfo = arguments!!.getParcelable(ARGS_DOC_INFO)
        accessToken = arguments!!.getString(Constants.KEY_ACCESS_TOKEN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.document_details)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var lastEdtId: Int? = null

        val labelTopMargin = 20.pxValue(requireContext())
        val inputTopMargin = 10.pxValue(requireContext())
        val sideMargin = labelTopMargin


        if (docInfo.listDocFieldsInfo!=null) {
            for (item in docInfo.listDocFieldsInfo) {

                val label = layoutInflater.inflate(R.layout.list_item_document_detail_label, null) as TextView
                label.run(addViewToParentConstraint(lastEdtId, labelTopMargin, sideMargin))
                label.text = item.label

                val editText = layoutInflater.inflate(R.layout.list_item_document_edit_input, null) as EditText
                editText.run(addViewToParentConstraint(label.id, inputTopMargin, sideMargin))

                lastEdtId = editText.id

                documentInputFields.add(DocumentInputField(item.key,item.label,item.value,
                        editText,
                         if(inputTypeMap.containsKey(item.type))inputTypeMap[item.type]!!else InputType.TYPE_TEXT_VARIATION_PERSON_NAME,
                        docInfo.isEditable==1,
                        requireContext()))


            }
        }else{
            parentView.visibility  = View.GONE
        }

        setDocData(docInfo)




    }

    public fun setDocData(docInfo:DocInfo){
        this.docInfo = docInfo

        addImageLayout.run(setImagesDataAsPerDocInfo(deleteImage1,deleteImage2,docInfo.file,docInfo.url[0]))
        addImageLayout2.run(setImagesDataAsPerDocInfo(deleteImage2,deleteImage2,docInfo.file1,docInfo.url[1]))
        if(docInfo.docCount<2){
            addImageLayout2.visibility = View.GONE
            deleteImage2.visibility = View.GONE
        }

//        for(i  in 0 until documentInputFields.size){
//
//            documentInputFields[i].isEditable = docInfo.isEditable==1
//
//        }


    }

    public fun submitInputData(){
        if(documentInputFields.size==0)return;



        val listInputFields = documentInputFields.map {
           ServerRequestInputFields(it.key, it.getValue())
        }

        val element = gson.toJsonTree(listInputFields, object : TypeToken<List<ServerRequestInputFields>>() {}.type)

        val fieldsInput = element.asJsonArray
        val map = hashMapOf(
                Constants.KEY_ACCESS_TOKEN to accessToken,
                "doc_type_num" to docInfo.docTypeNum.toString(),
                "doc_values" to fieldsInput.toString()

        )
        ApiCommonKt<FeedCommonResponseKotlin>(requireActivity()).execute(map,ApiName.UPDATE_DOC_FIELDS
                ,object: APICommonCallbackKotlin<FeedCommonResponseKotlin>(){
            override fun onSuccess(t: FeedCommonResponseKotlin?, message: String?, flag: Int) {
                for (i in 0 until  docInfo.listDocFieldsInfo.size){
                    docInfo.listDocFieldsInfo[i].value = listInputFields[i].value
                }
                DialogPopup.alertPopupWithListener(requireActivity(), "", message) {
                   //todo close this fragment and refresh if needed
                    requireActivity().onBackPressed()
                }
            }

            override fun onError(t: FeedCommonResponseKotlin?, message: String?, flag: Int): Boolean {
                return false
            }

        })


    }

    private fun setImagesDataAsPerDocInfo(deleteImageLayout:View, deleteImageLayout2:View, file: File?, url:String?): ImageView.() -> Unit {
        return {

            isEnabled = docInfo.isEditable != 0 && docInfo.status != "3"


            if (file!= null) {
                Picasso.with(requireContext()).load(file)
                        .transform(RoundBorderTransform()).resize(300, 300).centerCrop()
                        .into(this)

                if (docInfo.isEditable == 1) {
                    deleteImageLayout.visibility = View.VISIBLE
                } else {
                    deleteImageLayout.visibility = View.GONE
                }


                if (docInfo.status != "2") {
                    isEnabled = false
                } else {
                    isEnabled = true

                    //diff
                    deleteImageLayout.visibility = View.GONE
                    deleteImageLayout2.visibility = View.GONE

                }
            } else {

                setImageResource(R.drawable.ic_addimage)
                deleteImageLayout.visibility = View.GONE

            }

            if (docInfo.status == "2" || !url.isNullOrEmpty()) {

                try {


                    Picasso.with(activity).load(url)
                            .transform(RoundBorderTransform()).resize(300, 300).centerCrop()
                            .into(addImageLayout)
                    if (docInfo.isEditable == 1) {
                        deleteImageLayout.visibility = View.VISIBLE
                    } else {
                        deleteImageLayout.visibility = View.GONE
                    }



                    if (docInfo.status != "2") {
                        isEnabled = false
                    } else {
                        isEnabled = true




                        if (id == addImageLayout.id) {
                            setImageResource(R.drawable.reload_image)
                            deleteImageLayout.visibility = View.GONE
                            deleteImageLayout2.visibility = View.GONE
                            docInfo.file = null
                            docInfo.file1 = null
                        }

                    }

                    if (id == addImageLayout.id) {
                        if (docInfo.status == "4") {
                            isEnabled = true
                        }
                    }


                } catch (e: Exception) {
                    deleteImageLayout.visibility = View.GONE
                    e.printStackTrace()
                }

            }


            if (docInfo.status == "3" || docInfo.status == "1") {
                visibility = View.GONE
                deleteImageLayout.visibility = View.GONE
            } else {
                visibility = View.VISIBLE
            }

            setOnClickListener{
                ((requireActivity() as DriverDocumentActivity).supportFragmentManager.findFragmentByTag(DocumentListFragment::class.java.name) as DocumentListFragment).
                        addImageLayotOnClick(0,requireActivity() as DriverDocumentActivity,if(id==R.id.addImageLayout)0 else 1)
            }

            deleteImageLayout.setOnClickListener{
                ((requireActivity() as DriverDocumentActivity).supportFragmentManager.findFragmentByTag(DocumentListFragment::class.java.name) as DocumentListFragment).
                        deletImageLayoutOnClick(0,requireActivity() as DriverDocumentActivity,if(id==R.id.addImageLayout)0 else 1)
            }
        }

    }

    /**
     * adds view below another view
     * @param idViewAbove view to which bottom
     * @param labelTopMargin margin from top view
     * @param sideMargin margin from sides (View is aligned to parent)
     */
    private fun addViewToParentConstraint(idViewAbove: Int?, labelTopMargin: Int, sideMargin: Int): View.() -> Unit {
        return {
            id = ViewCompat.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            parentView.addView(this)

            ConstraintSet().apply {
                clone(parentView)
                idViewAbove?.run {
                    connect(id, ConstraintSet.TOP, this, ConstraintSet.BOTTOM, labelTopMargin)
                }?: connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, labelTopMargin)
                connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, sideMargin)
                connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, sideMargin)
            }.applyTo(parentView)
        }
    }
}

class DocumentInputField(
        var key: String,
        var label: String,
        value:String?,
        var inputField: EditText,
        var inputType: Int,
        isEditable:Boolean,
        var context: Context) {

    val FORMAT_UTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val DOB_DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())




    var isEditable = isEditable
        set(value) {
            field = value
            inputField.isEnabled = value
        }
    var value = value
        set(value) {
            field = value
            setText(value)
        }

    val calendar: Calendar by lazy {
        Calendar.getInstance()
    }


    private val datePickerDialog: DatePickerDialog by lazy {
        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            inputField.setText(DOB_DATE_FORMAT.format(calendar.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    init {
        FORMAT_UTC.timeZone = TimeZone.getTimeZone("UTC")
        inputField.inputType = inputType
        inputField.hint = label
        setText(value)
        inputField.isEnabled = isEditable


    }

    private fun setText(value: String?) {
        if (inputType == InputType.TYPE_CLASS_DATETIME) {
            try {
                if (!value.isNullOrBlank()) {
                    val date = FORMAT_UTC.parse(value)
                    calendar.timeInMillis = date.time
                    inputField.setText(DOB_DATE_FORMAT.format(calendar.timeInMillis))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                inputField.setText(value)
            }

            inputField.isFocusableInTouchMode = false
            inputField.setOnClickListener {
                datePickerDialog.show()
            }

        } else if (!value.isNullOrEmpty()) {
            inputField.setText(value)
            inputField.setSelection(value!!.length)
        }
    }

    val getValue = {
        if(inputType==InputType.TYPE_CLASS_DATETIME){
            FORMAT_UTC.format(calendar.timeInMillis)
        }else{
            inputField.text.toString()
        }
    }




}

class ServerRequestInputFields( @SerializedName("key") var key:String,
                                @SerializedName("value") var value:String)



