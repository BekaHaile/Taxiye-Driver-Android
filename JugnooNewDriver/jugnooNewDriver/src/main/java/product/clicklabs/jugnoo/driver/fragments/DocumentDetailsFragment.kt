package product.clicklabs.jugnoo.driver.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.document_details.*
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.DriverDocumentActivity
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.R.id.parentView
import product.clicklabs.jugnoo.driver.datastructure.DocInfo
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.inflate
import product.clicklabs.jugnoo.driver.utils.pxValue
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Parminder Saini on 12/09/18.
 */

const val ARGS_DOC_INFO = "args_doc_info"
const val ARGS_POS = "args_position"


class DocumentDetailsFragment:Fragment(){


    companion object {
        @JvmStatic
        fun newInstance(accessToken: String,docInfo: DocInfo,pos:Int) =
                DocumentDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                        putInt(ARGS_POS, pos)
                        putParcelable(ARGS_DOC_INFO, docInfo)
                    }
                }
    }

    private var TAG = DocumentDetailsFragment::class.qualifiedName
    private val  inputTypeMap = mapOf("date" to InputType.TYPE_CLASS_DATETIME,"varchar" to InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
    private val  gson = Gson()
    private  lateinit var docInfo:DocInfo
    private  lateinit var accessToken: String
    private  var pos: Int = 0
    private  var viewHolder :View?=null

    val documentInputFields = arrayListOf<DocumentInputField>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        docInfo = arguments!!.getParcelable(ARGS_DOC_INFO)
        accessToken = arguments!!.getString(Constants.KEY_ACCESS_TOKEN)
        pos = arguments!!.getInt(ARGS_POS)
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
        }

        setDocData(docInfo)
        viewHolder!!.run(addViewToParentConstraint(lastEdtId, labelTopMargin, sideMargin))



    }

    public fun setDocData(docInfo: DocInfo){
        this.docInfo = docInfo

        viewHolder = (activity as DriverDocumentActivity).documentListFragment.driverDocumentListAdapter.getDocumentListView(
                    pos, viewHolder, layoutInflater, activity as DriverDocumentActivity,true)



    }

    public fun submitInputData(){
        //if doc is editable and user has not uploaded image
        if(docInfo.status=="2" || docInfo.status=="4" || docInfo.isEditable==1 && docInfo.docCount>0) {

            //if no image has been uploaded
            if (docInfo.file == null && docInfo.file1 == null &&
                (docInfo.url==null   ||  (docInfo.url[0].isNullOrEmpty() && docInfo.url[1].isNullOrEmpty()))) {
                DialogPopup.alertPopup(requireActivity(), "", getString(R.string.upload_images_error))
                return

            }
        }


        if(documentInputFields.size==0){
            requireActivity().onBackPressed()
            return;
        }


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
                    requireActivity().onBackPressed()
                }
            }

            override fun onError(t: FeedCommonResponseKotlin?, message: String?, flag: Int): Boolean {
                return false
            }

        })


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



