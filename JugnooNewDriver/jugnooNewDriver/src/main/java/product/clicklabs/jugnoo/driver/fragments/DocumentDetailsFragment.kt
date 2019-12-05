package product.clicklabs.jugnoo.driver.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.appcompat.app.AppCompatActivity
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.picker.CountryPickerDialog
import com.picker.OnCountryPickerListener
import kotlinx.android.synthetic.main.document_details.*
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.DriverDocumentActivity
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.DocInfo
import product.clicklabs.jugnoo.driver.retrofit.model.DocFieldsInfo
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin
import product.clicklabs.jugnoo.driver.ui.models.SearchDataModel
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Utils
import product.clicklabs.jugnoo.driver.utils.inflate
import product.clicklabs.jugnoo.driver.utils.pxValue
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Parminder Saini on 12/09/18.
 */

const val ARGS_DOC_INFO = "args_doc_info"
const val ARGS_POS = "args_position"


class DocumentDetailsFragment: Fragment(){


    companion object {
        @JvmStatic
        fun newInstance(accessToken: String,docInfo: DocInfo,pos:Int) =
                DocumentDetailsFragment().apply {
                    arguments = Bundle().apply {
                        val gson = Gson()
                        putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                        putInt(ARGS_POS, pos)
                        putString(ARGS_DOC_INFO, gson.toJson(docInfo))
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
    private var listener:InteractionListener? = null

    val documentInputFields = hashMapOf<String,DocumentInputField>()

    enum class FieldTypes(val type: String){
        DATE("date"),
        TEXT("text"),
        SET_SS("set-ss"),
        SET_MS("set-ms"),
        SET_SS_REF("set-ss-ref"),
        TEXT_NS("text-ns"),
        TEXT_NS_SPACE("text-ns-allow-space"),
        TEXT_NS_HYPHEN("text-ns-allow-hyphen"),
        DATE_PAST("date-past"),
        DATE_FUTURE("date-future"),
        INT("int")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gson = Gson()
        docInfo = gson.fromJson(arguments!!.getString(ARGS_DOC_INFO), DocInfo::class.java)
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

        setDocData(docInfo)
        viewHolder!!.run(addViewToParentConstraint(lastEdtId, labelTopMargin, sideMargin))
        lastEdtId = viewHolder!!.id

        listener?.setSubmitButtonVisibility(if(docInfo.listDocFieldsInfo == null || docInfo.listDocFieldsInfo.size == 0) View.GONE else View.VISIBLE)

        if (docInfo.listDocFieldsInfo!=null) {
            for (item in docInfo.listDocFieldsInfo) {

                val label = layoutInflater.inflate(R.layout.list_item_document_detail_label, null) as TextView
                label.run(addViewToParentConstraint(lastEdtId, labelTopMargin, sideMargin))
                label.text = item.label + if (item.isMandatory) "*" else ""

                val editText = layoutInflater.inflate(R.layout.list_item_document_edit_input, null) as EditText
                editText.run(addViewToParentConstraint(label.id, inputTopMargin, sideMargin))

                lastEdtId = editText.id
                val docField = DocumentInputField(item.key,item.label,item.value,
                        editText, item.type,
                        docInfo.isDocInfoEditable, item.set, item.setValue as ArrayList<String>?, requireContext(), item.isSecured,
                        isMandatory = item.isMandatory)

                documentInputFields[item.key] = docField

            }


            for (item in docInfo.listDocFieldsInfo) {

                item.run {
                    if(!refKey.isNullOrEmpty() && documentInputFields.containsKey(refKey)){
                       documentInputFields[key]?.child = documentInputFields[refKey]
                    }
                    // check confirm_key and save confirm parent
                    if(!confirmKey.isNullOrEmpty() && documentInputFields.containsKey(confirmKey)){
                        documentInputFields[key]?.confirmParent = documentInputFields[confirmKey]
                    }
                }

            }
        }




    }

    public fun setDocData(docInfo: DocInfo){
        this.docInfo = docInfo

        viewHolder = (activity as DriverDocumentActivity).documentListFragment.driverDocumentListAdapter.getDocumentListView(
                    pos, viewHolder, layoutInflater, activity as DriverDocumentActivity,true, true)


    }

    public fun submitInputData(){
        //if doc is editable and user has not uploaded image
        if((docInfo.status=="2" || docInfo.status=="4" || docInfo.isEditable==1) && docInfo.docCount>0) {

            //if no image has been uploaded
            if (docInfo.docCount > 0 && docInfo.checkIfURLEmpty()) {
                DialogPopup.alertPopup(requireActivity(), "", getString(R.string.upload_images_error))
                return

            }
        }


        if(documentInputFields.size==0){
            requireActivity().onBackPressed()
            return;
        }

        val keyValueMap = hashMapOf<String, ServerRequestInputFields>()
        var confirmErrors = ""
        var isFirst = true
        val listInputFields = documentInputFields.values.map {
            if (it.isMandatory && it.getValue().isNullOrBlank()) {
                Utils.showToast(requireActivity(), getString(R.string.error_empty_mandatory_field, it.label))
                return
            }
            if(it.inputType == FieldTypes.SET_SS.type
                    || it.inputType == FieldTypes.SET_MS.type
                    || it.inputType == FieldTypes.SET_SS_REF.type){
                keyValueMap[it.key] = ServerRequestInputFields(it.key, it.getValue(), it.setValue)
            } else {
                keyValueMap[it.key] = ServerRequestInputFields(it.key, it.getValue(), null)
            }

            //Error Message for confirm fields
            if(it.confirmParent != null && !it.getValue().equals(it.confirmParent!!.getValue())){
                confirmErrors += (if(isFirst) "" else ", ").plus(it.confirmParent!!.label)
                isFirst = false
            }
            keyValueMap[it.key]
        }

        //Show error Message for confirm field if not matched with parent
        if(!confirmErrors.isEmpty()){
            Utils.showToast(activity, confirmErrors.plus(" ").plus(activity?.resources?.getString(R.string.not_match)))
            return
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
                    if(keyValueMap.containsKey(docInfo.listDocFieldsInfo[i].key)) {
                        docInfo.listDocFieldsInfo[i].value = keyValueMap[docInfo.listDocFieldsInfo[i].key]!!.value
                    }
                }
                DialogPopup.alertPopupWithListener(requireActivity(), "", message) {
                    listener?.updateDocInfo(pos, docInfo);
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is InteractionListener){
            listener = context
        }
    }

    interface InteractionListener{
        fun updateDocInfo(pos: Int, docInfo: DocInfo)
        fun setSubmitButtonVisibility(visibility:Int)
    }
}

class DocumentInputField(
        var key: String,
        var label: String,
        value:String?,
        var inputField: EditText,
        var inputType: String,
        isEditable:Boolean,
        var set: List<DocFieldsInfo>?,
        var setValue: ArrayList<String>?,
        var context: Context,
        var isSecured: Int,
        child:DocumentInputField? = null,
        var confirmParent:DocumentInputField? = null,
        var isMandatory: Boolean) {

    val FORMAT_UTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val DOB_DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val blockCharacterSet = "@#₹_&-+()/*\"':;!?~`|•√``™£¢∞§¶•ªº–≠œ∑´´†¥¨ˆˆπ“‘«åß∂ƒ©˙∆˚¬…æΩ≈ç√∫˜˜≤≥ç÷×€°®✓ !\"#\$%&'()*+,-./:;<=>?@[\\]^_`{|}~"



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

    var child = child
        set(child) {
            field = child
            if(set != null) {
                for (df in set!!.iterator()) {
                    if (df.isSelected) {
                        child?.run {
                            set = df.set
                        }
                    }
                }
            }
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
        inputField.hint = label

        if((inputType == DocumentDetailsFragment.FieldTypes.SET_SS.type
                        || inputType == DocumentDetailsFragment.FieldTypes.SET_MS.type)
                && (set == null || set!!.isEmpty())){
            inputType = DocumentDetailsFragment.FieldTypes.TEXT.type
        }

        if(inputType == DocumentDetailsFragment.FieldTypes.SET_SS.type
                || inputType == DocumentDetailsFragment.FieldTypes.SET_MS.type
                || inputType == DocumentDetailsFragment.FieldTypes.SET_SS_REF.type) {
            if (setValue == null) {
                setValue = ArrayList<String>()
            }

            if (setValue!!.size == 0 && value != null && !value.isEmpty()) {
                val arr = value.split(",");
                for(str in arr){
                    setValue!!.add(str.trim())
                }
                for (df in set!!.iterator()) {
                    df.isSelected = setValue!!.contains(df.value)
                }
            }
        }
        setText(value)
        inputField.isEnabled = isEditable

        //id is_secure == 1 then setting input type to Password_type
        if(isSecured == 1) {
            inputField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    private fun setText(value: String?) {

        when (inputType){
            DocumentDetailsFragment.FieldTypes.INT.type ->{
                inputField.inputType = InputType.TYPE_CLASS_NUMBER
                inputField.setText(value)
                inputField.setSelection(value!!.length)
            }
            DocumentDetailsFragment.FieldTypes.TEXT.type ->{
                inputField.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                inputField.setText(value)
                inputField.setSelection(value!!.length)
            }
            DocumentDetailsFragment.FieldTypes.TEXT_NS.type,
            DocumentDetailsFragment.FieldTypes.TEXT_NS_SPACE.type,
            DocumentDetailsFragment.FieldTypes.TEXT_NS_HYPHEN.type->{
                val filter = InputFilter(){ source, start, end, dest, dstart, dend->
                    if (source != null && blockCharacterSet.contains(("" + source))) {
                        if(inputType == DocumentDetailsFragment.FieldTypes.TEXT_NS_SPACE.type && source.contains(" ")){
                            null
                        } else if(inputType == DocumentDetailsFragment.FieldTypes.TEXT_NS_HYPHEN.type && source.contains("-")){
                            null
                        } else {
                            ""
                        }
                    } else {
                        null
                    }
                }
                inputField.filters = arrayOf(filter);
                inputField.setText(value)
                inputField.setSelection(value!!.length)
            }
            DocumentDetailsFragment.FieldTypes.DATE.type,
            DocumentDetailsFragment.FieldTypes.DATE_PAST.type,
            DocumentDetailsFragment.FieldTypes.DATE_FUTURE.type->{
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
                if(inputType == DocumentDetailsFragment.FieldTypes.DATE_PAST.type){
                    datePickerDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
                } else if(inputType == DocumentDetailsFragment.FieldTypes.DATE_FUTURE.type){
                    datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
                }
                inputField.setOnClickListener {
                    datePickerDialog.show()
                }

            }
            DocumentDetailsFragment.FieldTypes.SET_SS.type,
            DocumentDetailsFragment.FieldTypes.SET_MS.type,
            DocumentDetailsFragment.FieldTypes.SET_SS_REF.type->{
                inputField.isFocusableInTouchMode = false
                inputField.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_down_vector,0)

                val listener = object : CountryPickerDialog.CountryPickerDialogInteractionListener<DocFieldsInfo>{
                    override fun getAllCountries(): MutableList<DocFieldsInfo> {
                        return (set as MutableList)
                    }

                    override fun sortCountries(searchResults: MutableList<DocFieldsInfo>?) {
                    }

                    override fun canSearch(): Boolean {
                        return false
                    }
                }
                val pickListener = OnCountryPickerListener<DocFieldsInfo> { country ->
                    if(country != null) {
                        if(inputType == DocumentDetailsFragment.FieldTypes.SET_SS.type
                            ||inputType == DocumentDetailsFragment.FieldTypes.SET_SS_REF.type) {
                            setValue!!.clear()
                            for (df in set!!.iterator()) {
                                df.isSelected = false
                            }
                        }
                        if(!setValue!!.contains(country.value)) {
                            setValue!!.add(country.value)
                            country.isSelected = true
                            child?.run {
                                set = country.set
                                setValue?.clear()
                                setText("")
                            }
                        } else {
                            setValue!!.remove(country.value)
                            country.isSelected = false
                        }
                        setTextForSetValues()
                    }
                }

                inputField.setOnClickListener{
                    showSelectionDialog(listener,pickListener,label+"-dialog",label, (inputType == DocumentDetailsFragment.FieldTypes.SET_MS.type))
                }
                setTextForSetValues()
            }

        }
    }

    val getValue = {
        when (inputType){
            DocumentDetailsFragment.FieldTypes.DATE.type,
            DocumentDetailsFragment.FieldTypes.DATE_PAST.type,
            DocumentDetailsFragment.FieldTypes.DATE_FUTURE.type -> {
                FORMAT_UTC.format(calendar.timeInMillis)
            }
            DocumentDetailsFragment.FieldTypes.SET_SS.type,
            DocumentDetailsFragment.FieldTypes.SET_MS.type,
            DocumentDetailsFragment.FieldTypes.SET_SS_REF.type-> {
                setValue.toString().substring(1, setValue.toString().length-1)
            }
            else -> {
                inputField.text.toString()
            }
        }
    }

    fun <T: SearchDataModel> showSelectionDialog(
            interactionListener: CountryPickerDialog.CountryPickerDialogInteractionListener<T>
            ,pickerListener: OnCountryPickerListener<T>
            ,tag:String,title:String, showCheckbox: Boolean) {
        if (interactionListener.allCountries == null || interactionListener.allCountries!!.isEmpty()) {
            Toast.makeText(context,context.getString(R.string.no_results_found), Toast.LENGTH_SHORT).show()
        } else {
            val countryPickerDialog = CountryPickerDialog.newInstance(title, showCheckbox)
            countryPickerDialog.setCountryPickerListener(pickerListener)
            countryPickerDialog.setDialogInteractionListener(interactionListener)
            countryPickerDialog.show((context as AppCompatActivity).supportFragmentManager, tag)
        }
    }

    private fun setTextForSetValues(){
        if(setValue != null) {
            val str = StringBuilder()
            for (df in set!!.iterator()) {
                if(df.isSelected){
                    str.append(df.label).append(", ")
                }
            }
            var string = str.toString()
            if(!TextUtils.isEmpty(string)) {
                string = string.substring(0, string.length - 2)
            }
            inputField.setText(string)
        }
    }


}

class ServerRequestInputFields( @SerializedName("key") var key:String,
                                @SerializedName("value") var value:String?,
                                @SerializedName("set_value") var setValue:List<String>?)



