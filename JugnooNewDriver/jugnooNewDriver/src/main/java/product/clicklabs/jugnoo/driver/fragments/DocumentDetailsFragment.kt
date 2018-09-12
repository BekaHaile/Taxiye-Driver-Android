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
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.document_details.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.DocInfo
import product.clicklabs.jugnoo.driver.utils.inflate
import product.clicklabs.jugnoo.driver.utils.pxValue
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Parminder Saini on 12/09/18.
 */
val DOB_DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

class DocumentDetailsFragment:Fragment(){


    private var TAG = DocumentDetailsFragment::class.qualifiedName

    private lateinit var docInfo: DocInfo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.document_details)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var list =  arrayListOf("Name","Age","Gender")/*,"Friend1","Friend2","Friend3","Friend 3","Friend 4","Friend 5")*/


        var lastEdtId: Int? = null

        val labelTopMargin = 20.pxValue(requireContext())
        val inputTopMargin = 10.pxValue(requireContext())
        val sideMargin = labelTopMargin

        val documentInputFields = arrayListOf<DocumentInputField>()
        for (item in list) {

            val label = layoutInflater.inflate(R.layout.list_item_document_detail_label, null) as TextView
            label.run(addViewToParentConstraint(lastEdtId, labelTopMargin, sideMargin))
            label.text = item

            val editText = layoutInflater.inflate(R.layout.list_item_document_edit_input, null) as EditText
            editText.run(addViewToParentConstraint(label.id, inputTopMargin, sideMargin))

            lastEdtId = editText.id

            documentInputFields.add(DocumentInputField(item,editText,InputType.TYPE_CLASS_DATETIME,requireContext()))


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
        var label: String,
        var inputField: EditText,
        var inputType: Int, var context: Context) {

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
        inputField.inputType = inputType
        inputField.hint = label

        if (inputType == InputType.TYPE_CLASS_DATETIME) {
            inputField.isFocusableInTouchMode = false
            inputField.setOnClickListener {
                datePickerDialog.show()
            }
        }


    }


}


