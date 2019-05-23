package product.clicklabs.jugnoo.driver.emergency.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.picker.Country
import com.picker.CountryPicker
import com.tokenautocomplete.FilteredArrayAdapter
import com.tokenautocomplete.TokenCompleteTextView
import kotlinx.android.synthetic.main.fragment_add_emergency_contacts.*
import org.json.JSONArray
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.emergency.ContactsFetchAsync
import product.clicklabs.jugnoo.driver.emergency.EmergencyActivity
import product.clicklabs.jugnoo.driver.emergency.adapters.ContactsListAdapter
import product.clicklabs.jugnoo.driver.emergency.models.ContactBean
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt
import product.clicklabs.jugnoo.driver.utils.*
import product.clicklabs.jugnoo.driver.widgets.ContactsCompletionView
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.*


/**
 * For adding contacts to emergency contacts
 *
 *
 * Created by shankar on 2/22/16.
 */

@SuppressLint("ValidFragment")
class AddEmergencyContactsFragment : Fragment() {

    private val TAG = AddEmergencyContactsFragment::class.java.simpleName
    private var relative: RelativeLayout? = null

    private var title: TextView? = null
    private var textViewAdd: TextView? = null
    private var backBtn: ImageView? = null

    private var editTextContacts: ContactsCompletionView? = null
    private var recyclerViewContacts: RecyclerView? = null
    private var contactsListAdapter: ContactsListAdapter? = null
    private var contactBeans: ArrayList<ContactBean>? = ArrayList()
    private var contactsArrayAdapter: ArrayAdapter<ContactBean>? = null

    private var rootView: View? = null
    private var dialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_add_emergency_contacts, container, false)


        relative = rootView!!.findViewById<View>(R.id.relative) as RelativeLayout
        try {
            ASSL(activity, relative, 1134, 720, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        title = rootView!!.findViewById<View>(R.id.title) as TextView
        title!!.setText(R.string.emergency_contacts)
        backBtn = rootView!!.findViewById<View>(R.id.backBtn) as ImageView
        textViewAdd = rootView!!.findViewById<View>(R.id.textViewAdd) as TextView
        textViewAdd!!.typeface = Fonts.mavenRegular(activity!!)

        (rootView!!.findViewById<View>(R.id.textViewAddContacts) as TextView).typeface = Fonts.mavenMedium(activity!!)

        editTextContacts = rootView!!.findViewById<View>(R.id.editTextContacts) as ContactsCompletionView
        editTextContacts!!.typeface = Fonts.mavenLight(activity!!)

        recyclerViewContacts = rootView!!.findViewById<View>(R.id.recyclerViewContacts) as RecyclerView
        recyclerViewContacts!!.layoutManager = LinearLayoutManager(activity)
        recyclerViewContacts!!.itemAnimator = DefaultItemAnimator()
        recyclerViewContacts!!.setHasFixedSize(false)

        contactsListAdapter = ContactsListAdapter(contactBeans!!, activity!!, R.layout.list_item_contact,
                object : ContactsListAdapter.Callback {
                    override fun contactClicked(position: Int, contactBean: ContactBean) {
                        if (contactBean.isSelected) {
                            dialogConfirmEmergencyContact(activity, activity!!.getString(R.string.confirm) + " " + activity!!.getString(R.string.emergency_contacts), "",
                                    false, contactBean)
                        } else {
                            editTextContacts!!.removeObject(contactBean)
                        }
                    }
                }, ContactsListAdapter.ListMode.ADD_CONTACTS)
        recyclerViewContacts!!.adapter = contactsListAdapter

        contactsArrayAdapter = object : FilteredArrayAdapter<ContactBean>(this.context, R.layout.list_item_contact,
                contactBeans as List<ContactBean>?) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var convertView = convertView
                if (convertView == null) {
                    val l = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    convertView = l.inflate(R.layout.list_item_contact, parent, false)
                    val layoutParams = RelativeLayout.LayoutParams(640, 128)
                    convertView!!.layoutParams = layoutParams

                    ASSL.DoMagic(convertView)
                }


                val p = getItem(position)
                (convertView.findViewById<View>(R.id.textViewContactName) as TextView).typeface = Fonts.mavenLight(activity!!)
                (convertView.findViewById<View>(R.id.textViewContactNumberType) as TextView).typeface = Fonts.mavenLight(activity!!)
                (convertView.findViewById<View>(R.id.textViewContactName) as TextView).text = p!!.name
                (convertView.findViewById<View>(R.id.textViewContactNumberType) as TextView).text = p.phoneNo + " " + p.type
                convertView.findViewById<View>(R.id.imageViewOption).visibility = View.GONE

                return convertView
            }

            override fun keepObject(person: ContactBean, mask: String): Boolean {
                var mask = mask
                if (mask.length > 2) {
                    mask = mask.toLowerCase()

                    return person.name!!.toLowerCase().contains(mask) || person.phoneNo!!.toLowerCase().contains(mask)
                } else {
                    return false
                }
            }
        }

        editTextContacts!!.setAdapter<ArrayAdapter<ContactBean>>(contactsArrayAdapter)
        editTextContacts!!.allowDuplicates(false)
        editTextContacts!!.setTokenLimit(EmergencyActivity.EMERGENCY_CONTACTS_ALLOWED_TO_ADD)
        editTextContacts!!.setTokenListener(object : TokenCompleteTextView.TokenListener<ContactBean> {
            override fun onTokenAdded(token: ContactBean) {
                token.isSelected = true
                dialogConfirmEmergencyContact(activity, activity!!.getString(R.string.confirm) + " " + activity!!.getString(R.string.emergency_contacts), "",
                        false, token)

            }

            override fun onTokenRemoved(token: ContactBean) {
                setSelectedObject(false, token)
            }
        })


        val onClickListener = View.OnClickListener { v ->
            when (v.id) {

                R.id.backBtn -> performBackPressed()

                R.id.textViewAdd -> try {
                    val jsonArray = JSONArray()
                    for (contactBean in contactBeans!!) {
                        if (contactBean.isSelected) {
                            val jsonObject = JSONObject()
                            jsonObject.put(Constants.KEY_NAME, contactBean.name)
                            jsonObject.put(Constants.KEY_PHONE_NO, contactBean.phoneNo)
                            jsonArray.put(jsonObject)
                        }
                    }
                    if (jsonArray.length() > 0) {
                        addEmergencyContactsAPI(activity, jsonArray.toString())
                    } else {
                        Utils.showToast(activity, activity!!.resources.getString(R.string.please_select_some_contacts_first))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }


        backBtn!!.setOnClickListener(onClickListener)
        textViewAdd!!.setOnClickListener(onClickListener)


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backBtn?.run { postDelayed({ async.execute() }, 200) }

    }

    private val async by lazy {
        ContactsFetchAsync(requireActivity(), contactBeans!!, false, object : ContactsFetchAsync.Callback {
            override fun onPreExecute() {
                progressWheelLoadContacts.visibility = View.VISIBLE
            }

            override fun onPostExecute(contactBeans: ArrayList<ContactBean>) {
                progressWheelLoadContacts.visibility = View.GONE
                contactsListAdapter!!.setCountAndNotify()
                contactsArrayAdapter!!.notifyDataSetChanged()
            }
        })
    }

    fun addEmergencyContact(contactBean: ContactBean, countryCode:String, phoneNo:String) {
        try {
            val jsonArray = JSONArray()
            if (contactBean.isSelected) {
                val jsonObject = JSONObject()
                jsonObject.put(Constants.KEY_NAME, contactBean.name)
                jsonObject.put(Constants.KEY_PHONE_NO, phoneNo)
                jsonObject.put(Constants.KEY_COUNTRY_CODE, countryCode)
                jsonArray.put(jsonObject)
            }
            if (jsonArray.length() > 0) {
                addEmergencyContactsAPI(activity, jsonArray.toString())
            } else {
                Utils.showToast(activity, activity!!.resources.getString(R.string.please_select_some_contacts_first))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun performBackPressed() {
        Utils.hideSoftKeyboard(activity, editTextContacts)
        if (activity is EmergencyActivity) {
            (activity as EmergencyActivity).performBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        async.cancel(true)
        ASSL.closeActivity(rootView)
        System.gc()
    }

    private fun setSelectedObject(selected: Boolean, contactBean: ContactBean) {
        try {
            contactBeans!![contactBeans!!.indexOf(ContactBean(contactBean.name,
                    contactBean.phoneNo, contactBean.countryCode, contactBean.type))].isSelected = selected
            contactsListAdapter!!.setCountAndNotify()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun addEmergencyContactsAPI(activity: Activity?, jsonArray: String) {
        try {
            if (MyApplication.getInstance().isOnline) {

                DialogPopup.showLoadingDialog(activity, getString(R.string.loading))

                val params = HashMap<String, String>()
                params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
                params[Constants.KEY_CLIENT_ID] = Data.CLIENT_ID
                params[Constants.KEY_EMERGENCY_CONTACTS] = jsonArray

                Log.e("params", "=" + params.toString())

                HomeUtil.putDefaultParams(params)
                RestClient.getApiServices().emergencyContactsAddMultiple(params, object : Callback<SettleUserDebt> {
                    override fun success(settleUserDebt: SettleUserDebt, response: Response) {
                        val responseStr = String((response.body as TypedByteArray).bytes)
                        Log.i(TAG, "response = $responseStr")
                        DialogPopup.dismissLoadingDialog()
                        try {
                            val jObj = JSONObject(responseStr)
                            val message = JSONParser.getServerMessage(jObj)
                            val flag = jObj.getInt(Constants.KEY_FLAG)
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
                                if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                    DialogPopup.dialogBanner(activity, message)
                                } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    DialogPopup.dialogBanner(activity, message)
                                    performBackPressed()
                                } else {
                                    DialogPopup.dialogBanner(activity, message)
                                }
                            }
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                            DialogPopup.alertPopup(activity, "", activity!!.getString(R.string.connection_lost))
                        }

                        DialogPopup.dismissLoadingDialog()
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e(TAG, "error=" + error.toString())
                        DialogPopup.dismissLoadingDialog()
                        DialogPopup.alertPopup(activity, "", activity!!.getString(R.string.connection_lost))
                    }
                })
            } else {
                DialogPopup.alertPopup(activity, "", activity!!.getString(R.string.connection_lost))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun dialogConfirmEmergencyContact(activity: FragmentActivity?, title: String, message: String,
                                      cancellable: Boolean, contactBean: ContactBean) {
        try {
            dismissAlertPopup()

            dialog = Dialog(activity!!, android.R.style.Theme_Translucent_NoTitleBar)
            dialog!!.window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            dialog!!.setContentView(R.layout.dialog_confirm_emergency_contact)

            val frameLayout = dialog!!.findViewById<View>(R.id.rv) as RelativeLayout
            ASSL(activity, frameLayout, 1134, 720, false)

            val layoutParams = dialog!!.window!!.attributes
            layoutParams.dimAmount = 0.6f
            dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog!!.setCancelable(cancellable)
            dialog!!.setCanceledOnTouchOutside(cancellable)


            val textHead = dialog!!.findViewById<View>(R.id.textHead) as TextView
            textHead.typeface = Fonts.mavenRegular(activity)
            val textMessage = dialog!!.findViewById<View>(R.id.textMessage) as TextView
            textMessage.typeface = Fonts.mavenLight(activity)


            val rlPhone = dialog!!.findViewById<View>(R.id.rlPhone) as RelativeLayout
            val llCountryCode = dialog!!.findViewById<View>(R.id.llCountryCode) as LinearLayout
            val tvCountryCode = dialog!!.findViewById<View>(R.id.tvCountryCode) as TextView
            tvCountryCode.typeface = Fonts.mavenRegular(activity)

            val editTextPhoneNumber = dialog!!.findViewById<View>(R.id.editTextPhoneNumber) as EditText
            val countryPicker = CountryPicker.Builder().with(activity)
                    .listener { country -> tvCountryCode.text = (country as Country).dialCode }
                    .build()
            tvCountryCode.text = Utils.getCountryCode(activity)
            if (countryPicker.allCountries.size > 1) {
                llCountryCode.isEnabled = true
                tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_vector, 0)
            } else {
                llCountryCode.isEnabled = false
                tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            }
            llCountryCode.setOnClickListener { countryPicker.showDialog(activity.supportFragmentManager) }
            editTextPhoneNumber.setText(contactBean.phoneNo!!.replaceFirst("^0+(?!$)".toRegex(), ""))
            editTextPhoneNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable) {
                    if (s.toString().startsWith("0")) {
                        if (s.length > 1) {
                            editTextPhoneNumber.setText(s.toString().substring(1))
                        } else {
                            editTextPhoneNumber.setText("")
                        }
                        Toast.makeText(activity, "Phone number should not start with 0", Toast.LENGTH_SHORT).show()
                    }
                }
            })

            textMessage.movementMethod = LinkMovementMethod.getInstance()
            textMessage.maxHeight = (800.0f * ASSL.Yscale()).toInt()

            textHead.text = title
            textMessage.text = message

            val btnOk = dialog!!.findViewById<View>(R.id.btnOk) as Button
            btnOk.typeface = Fonts.mavenRegular(activity)
            val btnClose = dialog!!.findViewById<View>(R.id.close) as ImageView
            btnOk.setOnClickListener {
//                contactBean.countryCode = tvCountryCode.text.toString()
//                contactBean.phoneNo = tvCountryCode.text.toString() + editTextPhoneNumber.text.toString()
                addEmergencyContact(contactBean, tvCountryCode.text.toString(), tvCountryCode.text.toString() + editTextPhoneNumber.text.toString())
                dialog!!.dismiss()
            }

            btnClose.setOnClickListener {
                dialog!!.dismiss()
                contactBean.isSelected = false
                contactsListAdapter!!.setCountAndNotify()
                try {
                    editTextContacts!!.removeObject(contactBean)
                } catch (ignored: Exception) {
                }
            }

            frameLayout.setOnClickListener {
                if (cancellable) {
                    dialog!!.dismiss()
                }
            }

            dialog!!.findViewById<View>(R.id.linearLayoutInner).setOnClickListener { }

            dialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun dismissAlertPopup() {
        try {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        } catch (e: Exception) {
        }

    }

    companion object {

        fun newInstance(): AddEmergencyContactsFragment {
            val fragment = AddEmergencyContactsFragment()
            val bundle = Bundle()

            fragment.arguments = bundle
            return fragment
        }
    }


}
