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
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.squareup.picasso.RoundBorderTransform
import kotlinx.android.synthetic.main.document_details.*
import product.clicklabs.jugnoo.driver.DocumentListFragment
import product.clicklabs.jugnoo.driver.DriverDocumentActivity
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.DocInfo
import product.clicklabs.jugnoo.driver.utils.inflate
import product.clicklabs.jugnoo.driver.utils.pxValue
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Parminder Saini on 12/09/18.
 */
val DOB_DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

class DocumentDetailsFragment( var docInfo: DocInfo):Fragment(){


    private var TAG = DocumentDetailsFragment::class.qualifiedName


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

        addImageLayout.run(setImagesDataAsPerDocInfo(deleteImage1,deleteImage2,docInfo.file,docInfo.url[0]))
        addImageLayout2.run(setImagesDataAsPerDocInfo(deleteImage2,deleteImage2,docInfo.file1,docInfo.url[1]))








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
//                setImageResource(R.drawable.transparent)
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


