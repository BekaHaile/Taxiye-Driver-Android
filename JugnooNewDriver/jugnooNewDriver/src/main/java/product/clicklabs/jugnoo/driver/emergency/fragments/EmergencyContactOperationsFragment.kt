package product.clicklabs.jugnoo.driver.emergency.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.picker.Country
import com.picker.CountryPicker
import com.tokenautocomplete.FilteredArrayAdapter
import kotlinx.android.synthetic.main.fragment_emergency_contacts_operations.*
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.apis.ApiEmergencyContactsList
import product.clicklabs.jugnoo.driver.apis.ApiEmergencySendRideStatus
import product.clicklabs.jugnoo.driver.emergency.ContactsFetchAsync
import product.clicklabs.jugnoo.driver.emergency.EmergencyActivity
import product.clicklabs.jugnoo.driver.emergency.FragTransUtils
import product.clicklabs.jugnoo.driver.emergency.adapters.ContactsListAdapter
import product.clicklabs.jugnoo.driver.emergency.models.ContactBean
import product.clicklabs.jugnoo.driver.utils.*
import java.util.*


/**
 * For
 *
 *
 * Created by shankar on 2/22/16.
 */

@SuppressLint("ValidFragment")
class EmergencyContactOperationsFragment : Fragment() {

    private var relative: RelativeLayout? = null

    private var title: TextView? = null
    private var textViewSend: TextView? = null
    private var backBtn: ImageView? = null

    private var linearLayoutMain: LinearLayout? = null
    private var textViewScroll: TextView? = null

    private var linearLayoutEmergencyContacts: LinearLayout? = null
    private var recyclerViewEmergencyContacts: RecyclerView? = null
    private var relativeLayoutOr: RelativeLayout? = null
    private var textViewOr: TextView? = null
    private var buttonAddContact: Button? = null

    private var editTextPhoneContacts: AutoCompleteTextView? = null
    private var recyclerViewPhoneContacts: RecyclerView? = null

    private var emergencyContactsListAdapter: ContactsListAdapter? = null
    private var phoneContactsListAdapter: ContactsListAdapter? = null
    private var emergencyContactBeans: ArrayList<ContactBean>? = null
    private var phoneContactBeans: ArrayList<ContactBean>? = ArrayList()
    private var phoneContactsArrayAdapter: ArrayAdapter<ContactBean>? = null

    private var engagementId: String? = null
    private var listMode: ContactsListAdapter.ListMode? = null

    private var rootView: View? = null
    private var dialog: Dialog? = null
    private var isFromEdiText: Boolean = false
    private var permissionCommon: PermissionCommon? = null
    private val permissionListener = object : PermissionCommon.PermissionListener {
        override fun permissionGranted(requestCode: Int) {

            isGrantCalled = true
            rootView!!.findViewById<View>(R.id.llPermission).visibility = View.GONE
            rootView!!.findViewById<View>(R.id.layoutContacts).visibility = View.VISIBLE

            if (requestCode == REQUEST_CODE_ADD_CONTACT) {
                FragTransUtils().openAddEmergencyContactsFragment(requireActivity(),
                        (activity as EmergencyActivity).container)
            } else {
                async.execute()
            }

        }

        override fun permissionDenied(requestCode: Int, neverAsk: Boolean): Boolean {

            if (requestCode == REQUEST_CODE_VIEW_CONTACTS) {

                if (neverAsk) {
                    PermissionCommon.openSettingsScreen(activity!!)
                }

                return false

            } else if (requestCode == REQUEST_CODE_VIEW_CONTACTS_ON_CREATE) {
                return false
            }

            return true
        }

        override fun onRationalRequestIntercepted() {

        }

    }
    private var firstTime: Boolean = false
    private var isGrantCalled: Boolean = false

    private val async by lazy{ ContactsFetchAsync(requireActivity(), phoneContactBeans!!, object : ContactsFetchAsync.Callback {
        override fun onPreExecute() {
            progressWheelLoadContacts.visibility = View.VISIBLE
        }

        override fun onPostExecute(contactBeans: ArrayList<ContactBean>) {
            progressWheelLoadContacts.visibility = View.GONE
            phoneContactsListAdapter!!.notifyDataSetChanged()
        }

        override fun onCancel() {
            performBackPressed()
        }
    })}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (permissionCommon != null) {
            if (PermissionCommon.isGranted(Manifest.permission.READ_CONTACTS, activity) && !isGrantCalled) {
                permissionListener.permissionGranted(REQUEST_CODE_VIEW_CONTACTS)
            } else if (firstTime) {
                firstTime = false
                permissionCommon!!.getPermission(REQUEST_CODE_VIEW_CONTACTS_ON_CREATE, PermissionCommon.SKIP_RATIONAL_MESSAGE, Manifest.permission.READ_CONTACTS)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_emergency_contacts_operations, container, false)

        this.engagementId = arguments!!.getString(Constants.KEY_ENGAGEMENT_ID, "")
        val listModeInt = arguments!!.getInt(LIST_MODE, ContactsListAdapter.ListMode.ADD_CONTACTS.ordinal)
        if (listModeInt == ContactsListAdapter.ListMode.ADD_CONTACTS.ordinal) {
            listMode = ContactsListAdapter.ListMode.ADD_CONTACTS
        } else if (listModeInt == ContactsListAdapter.ListMode.EMERGENCY_CONTACTS.ordinal) {
            listMode = ContactsListAdapter.ListMode.EMERGENCY_CONTACTS
        } else if (listModeInt == ContactsListAdapter.ListMode.DELETE_CONTACTS.ordinal) {
            listMode = ContactsListAdapter.ListMode.DELETE_CONTACTS
        } else if (listModeInt == ContactsListAdapter.ListMode.CALL_CONTACTS.ordinal) {
            listMode = ContactsListAdapter.ListMode.CALL_CONTACTS
        } else if (listModeInt == ContactsListAdapter.ListMode.SEND_RIDE_STATUS.ordinal) {
            listMode = ContactsListAdapter.ListMode.SEND_RIDE_STATUS
        }

        permissionCommon = PermissionCommon(this).setCallback(permissionListener)
        relative = rootView!!.findViewById<View>(R.id.relative) as RelativeLayout
        try {
            ASSL(activity, relative, 1134, 720, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        title = rootView!!.findViewById<View>(R.id.title) as TextView
        title!!.typeface = Fonts.mavenMedium(activity!!)
        backBtn = rootView!!.findViewById<View>(R.id.backBtn) as ImageView
        textViewSend = rootView!!.findViewById<View>(R.id.textViewSend) as TextView
        textViewSend!!.typeface = Fonts.mavenRegular(activity!!)

        linearLayoutMain = rootView!!.findViewById<View>(R.id.linearLayoutMain) as LinearLayout
        textViewScroll = rootView!!.findViewById<View>(R.id.textViewScroll) as TextView

        linearLayoutEmergencyContacts = rootView!!.findViewById<View>(R.id.linearLayoutEmergencyContacts) as LinearLayout
        (rootView!!.findViewById<View>(R.id.textViewEmergencyContacts) as TextView).typeface = Fonts.mavenLight(activity!!)
        relativeLayoutOr = rootView!!.findViewById<View>(R.id.relativeLayoutOr) as RelativeLayout
        textViewOr = rootView!!.findViewById<View>(R.id.textViewOr) as TextView
        textViewOr!!.typeface = Fonts.mavenLight(activity!!)
        buttonAddContact = rootView!!.findViewById<View>(R.id.buttonAddContact) as Button
        buttonAddContact!!.typeface = Fonts.mavenRegular(activity!!)
        (rootView!!.findViewById<View>(R.id.buttonGrantPermission) as Button).typeface = Fonts.mavenRegular(activity!!)
        (rootView!!.findViewById<View>(R.id.text_permission) as TextView).typeface = Fonts.mavenRegular(activity!!)
        buttonAddContact!!.visibility = View.GONE
        relativeLayoutOr!!.visibility = View.GONE

        recyclerViewEmergencyContacts = rootView!!.findViewById<View>(R.id.recyclerViewEmergencyContacts) as RecyclerView
        recyclerViewEmergencyContacts!!.layoutManager = LinearLayoutManager(activity)
        recyclerViewEmergencyContacts!!.itemAnimator = DefaultItemAnimator()
        recyclerViewEmergencyContacts!!.setHasFixedSize(false)

        emergencyContactBeans = ArrayList()
        emergencyContactsListAdapter = ContactsListAdapter(emergencyContactBeans!!, activity!!, R.layout.list_item_contact,
                object : ContactsListAdapter.Callback {
                    override fun contactClicked(position: Int, contactBean: ContactBean) {
                        contactCalledAccToListMode(contactBean)
                    }
                }, listMode)
        recyclerViewEmergencyContacts!!.adapter = emergencyContactsListAdapter


        (rootView!!.findViewById<View>(R.id.textViewPhoneContacts) as TextView).typeface = Fonts.mavenLight(activity!!)
        editTextPhoneContacts = rootView!!.findViewById<View>(R.id.editTextPhoneContacts) as AutoCompleteTextView
        editTextPhoneContacts!!.typeface = Fonts.mavenLight(activity!!)
        recyclerViewPhoneContacts = rootView!!.findViewById<View>(R.id.recyclerViewPhoneContacts) as RecyclerView
        recyclerViewPhoneContacts!!.layoutManager = LinearLayoutManager(activity)
        recyclerViewPhoneContacts!!.itemAnimator = DefaultItemAnimator()
        recyclerViewPhoneContacts!!.setHasFixedSize(false)

        phoneContactsListAdapter = ContactsListAdapter(phoneContactBeans!!, activity!!, R.layout.list_item_contact,
                object : ContactsListAdapter.Callback {
                    override fun contactClicked(position: Int, contactBean: ContactBean) {
                        isFromEdiText = false
                        dialogConfirmEmergencyContact(activity, activity!!.getString(R.string.confirm) + " " + activity!!.getString(R.string.emergency_contacts), "",
                                false, contactBean)
                        //contactCalledAccToListMode(contactBean);
                    }
                }, listMode)
        recyclerViewPhoneContacts!!.adapter = phoneContactsListAdapter

        phoneContactsArrayAdapter = object : FilteredArrayAdapter<ContactBean>(this.context, R.layout.list_item_contact,
                phoneContactBeans as List<ContactBean>?) {
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

                convertView.findViewById<View>(R.id.relative).tag = position
                convertView.findViewById<View>(R.id.relative).setOnClickListener { v ->
                    try {
                        val position = v.tag as Int
                        val p = getItem(position)
                        editTextPhoneContacts!!.dismissDropDown()
                        isFromEdiText = true
                        dialogConfirmEmergencyContact(activity, activity!!.getString(R.string.confirm) + " " + activity!!.getString(R.string.emergency_contacts), "",
                                false, p)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


                return convertView
            }

            override fun keepObject(person: ContactBean, mask: String): Boolean {
                var mask = mask
                //                mask = mask.toLowerCase();
                //                boolean matched = person.getName().toLowerCase().startsWith(mask)
                //                        || person.getPhoneNo().toLowerCase().startsWith(mask);
                //                return matched;

                if (mask.length > 2) {
                    mask = mask.toLowerCase()

                    return person.name!!.toLowerCase().contains(mask) || person.phoneNo.toLowerCase().contains(mask)
                } else {
                    return false
                }


            }

        }
        editTextPhoneContacts!!.setAdapter<ArrayAdapter<ContactBean>>(phoneContactsArrayAdapter)


        val onClickListener = View.OnClickListener { v ->
            when (v.id) {

                R.id.backBtn -> performBackPressed()

                R.id.textViewSend -> if (ContactsListAdapter.ListMode.SEND_RIDE_STATUS === listMode) {
                    clickOnSend()
                }

                R.id.linearLayoutMain -> Utils.hideSoftKeyboard(activity, editTextPhoneContacts)

                R.id.buttonAddContact -> permissionCommon!!.getPermission(REQUEST_CODE_ADD_CONTACT, Manifest.permission.READ_CONTACTS)
                R.id.buttonGrantPermission -> permissionCommon!!.getPermission(REQUEST_CODE_VIEW_CONTACTS, PermissionCommon.SKIP_RATIONAL_MESSAGE, Manifest.permission.READ_CONTACTS)
            }
        }


        backBtn!!.setOnClickListener(onClickListener)
        textViewSend!!.setOnClickListener(onClickListener)
        linearLayoutMain!!.setOnClickListener(onClickListener)
        buttonAddContact!!.setOnClickListener(onClickListener)
        rootView!!.findViewById<View>(R.id.buttonGrantPermission).setOnClickListener(onClickListener)

        setEmergencyContactsToList()


//        val keyboardLayoutListener = KeyboardLayoutListener(linearLayoutMain, textViewScroll,
//                object : KeyboardLayoutListener.KeyBoardStateHandler {
//                    override fun keyboardOpened() {
//                        linearLayoutEmergencyContacts!!.visibility = View.GONE
//                    }
//
//                    override fun keyBoardClosed() {
//                        linearLayoutEmergencyContacts!!.visibility = View.VISIBLE
//                    }
//                })
//        keyboardLayoutListener.setResizeTextView(false)
//        linearLayoutMain!!.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)


        if (ContactsListAdapter.ListMode.SEND_RIDE_STATUS === listMode) {
            textViewSend!!.visibility = View.VISIBLE
            title!!.text = activity!!.resources.getString(R.string.send_ride_status)
        } else if (ContactsListAdapter.ListMode.CALL_CONTACTS === listMode) {
            textViewSend!!.visibility = View.GONE
            title!!.text = activity!!.resources.getString(R.string.call_your_contacts)
        }

        if (!PermissionCommon.isGranted(Manifest.permission.READ_CONTACTS, getActivity())) {
            rootView!!.findViewById<View>(R.id.layoutContacts).visibility = View.GONE
            rootView!!.findViewById<View>(R.id.llPermission).visibility = View.VISIBLE

        }
        firstTime = true

        getAllEmergencyContacts()

        return rootView
    }


    private fun performBackPressed() {
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            getAllEmergencyContacts()
        }
    }


    private fun setEmergencyContactsToList() {
        if (Data.userData.emergencyContactsList != null) {
            emergencyContactBeans!!.clear()
            for (emergencyContact in Data.userData.emergencyContactsList) {
                val contactBean = ContactBean(emergencyContact.name, emergencyContact.phoneNo, emergencyContact.countryCode, "", ContactBean.ContactBeanViewType.CONTACT, null, null)
                contactBean.id = emergencyContact.id
                emergencyContactBeans!!.add(contactBean)
            }
            emergencyContactsListAdapter!!.notifyDataSetChanged()
            if (emergencyContactBeans!!.size > 0) {
                relativeLayoutOr!!.visibility = View.GONE
                buttonAddContact!!.visibility = View.GONE
            } else {
                relativeLayoutOr!!.visibility = View.VISIBLE
                buttonAddContact!!.visibility = View.VISIBLE
                if (listMode === ContactsListAdapter.ListMode.SEND_RIDE_STATUS) {
                    textViewOr!!.text = activity!!.resources.getString(R.string.or_send_directly)
                } else if (listMode === ContactsListAdapter.ListMode.CALL_CONTACTS) {
                    textViewOr!!.text = activity!!.resources.getString(R.string.or_call_directly)
                }
            }
        } else {
            relativeLayoutOr!!.visibility = View.VISIBLE
            buttonAddContact!!.visibility = View.VISIBLE
            if (listMode === ContactsListAdapter.ListMode.SEND_RIDE_STATUS) {
                textViewOr!!.text = activity!!.resources.getString(R.string.or_send_directly)
            } else if (listMode === ContactsListAdapter.ListMode.CALL_CONTACTS) {
                textViewOr!!.text = activity!!.resources.getString(R.string.or_call_directly)
            }
        }
    }

    private fun setSelectedObject(contactBean: ContactBean?) {
        try {
            val index = phoneContactBeans!!.indexOf(ContactBean(contactBean!!.name,
                    contactBean.phoneNo, contactBean.countryCode, contactBean.type, ContactBean.ContactBeanViewType.CONTACT, null, null))
            phoneContactBeans!![index].isSelected = true
            phoneContactsListAdapter!!.notifyDataSetChanged()
            (recyclerViewPhoneContacts!!.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(index, 20)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun getAllEmergencyContacts() {
        ApiEmergencyContactsList(activity!!, object : ApiEmergencyContactsList.Callback {
            override fun onSuccess() {
                setEmergencyContactsToList()
            }

            override fun onFailure() {

            }

            override fun onRetry(view: View) {
                getAllEmergencyContacts()
            }

            override fun onNoRetry(view: View) {

            }
        }).emergencyContactsList()
    }

    private fun clickOnSend() {
        val contacts = ArrayList<ContactBean>()
        for (contactBean in emergencyContactBeans!!) {
            if (contactBean.isSelected) {
                contacts.add(contactBean)
            }
        }
        for (contactBean in phoneContactBeans!!) {
            if (contactBean.isSelected) {
                contacts.add(contactBean)
            }
        }

        if (contacts.size == 0) {
            DialogPopup.alertPopup(activity, "",
                    activity!!.resources.getString(R.string.send_ride_status_no_contacts_message))
        } else if (contacts.size > 5) {
            DialogPopup.alertPopupTwoButtonsWithListeners(activity,
                    "",
                    String.format(activity!!.resources
                            .getString(R.string.send_ride_status_more_contacts_message_format),
                            "" + EmergencyActivity.MAX_EMERGENCY_CONTACTS_TO_SEND_RIDE_STATUS,
                            "" + EmergencyActivity.MAX_EMERGENCY_CONTACTS_TO_SEND_RIDE_STATUS),
                    activity!!.resources.getString(R.string.ok),
                    activity!!.resources.getString(R.string.cancel),
                    { sendRideStatusApi(engagementId, contacts) },
                    { }, true, false)
        } else {
            sendRideStatusApi(engagementId, contacts)
        }
    }

    private fun sendRideStatusApi(engagementId: String?, contacts: ArrayList<ContactBean>) {
        ApiEmergencySendRideStatus(activity!!, object : ApiEmergencySendRideStatus.Callback {
            override fun onSuccess(message: String) {
                DialogPopup.alertPopupWithListener(activity, "", message) { performBackPressed() }
            }

            override fun onFailure() {

            }

            override fun onRetry(view: View) {
                sendRideStatusApi(engagementId, contacts)
            }

            override fun onNoRetry(view: View) {

            }
        }).emergencySendRideStatusMessage(engagementId!!, contacts)
    }


    private fun contactCalledAccToListMode(contactBean: ContactBean?): Boolean {
        if (ContactsListAdapter.ListMode.CALL_CONTACTS === listMode) {
            Utils.openCallIntent(activity!!, contactBean!!.phoneNo)
            return true
        } else {
            return false
        }
    }

    fun dialogConfirmEmergencyContact(activity: FragmentActivity?, title: String, message: String,
                                      cancellable: Boolean, contactBean: ContactBean?) {
        try {
            if(contactBean == null){
                return
            }

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

            val phoneNo: String = contactBean.phoneNo
            val ccpn = UtilsKt.splitCountryCodeAndPhoneNumber(requireContext(), phoneNo)

            tvCountryCode.setText(ccpn.countryCode)
            editTextPhoneNumber.setText(ccpn.phoneNo)

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
                contactBean.countryCode = tvCountryCode.text.toString()
                if (editTextPhoneNumber.text.toString().substring(0, tvCountryCode.text.toString().length).equals(tvCountryCode.text.toString(), ignoreCase = true)) {
                    contactBean.phoneNo = editTextPhoneNumber.text.toString()
                } else {
                    contactBean.phoneNo = tvCountryCode.text.toString() + editTextPhoneNumber.text.toString()
                }
                if (isFromEdiText) {
                    if (!contactCalledAccToListMode(contactBean)) {
                        setSelectedObject(contactBean)
                    }
                } else {
                    contactCalledAccToListMode(contactBean)
                }
                contactBean.isSelected = true
                phoneContactsListAdapter!!.notifyDataSetChanged()
                dialog!!.dismiss()
            }

            btnClose.setOnClickListener {
                dialog!!.dismiss()
                contactBean.isSelected = false
                phoneContactsListAdapter!!.setCountAndNotify()
                //                    phoneContactsListAdapter.notifyDataSetChanged();
                //	try { editTextContacts.removeObject(contactBean); } catch (Exception ignored) {}
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
        } catch (ignored: Exception) {
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionCommon != null) permissionCommon!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private val LIST_MODE = "listMode"
        private val REQUEST_CODE_ADD_CONTACT = 50
        private val REQUEST_CODE_VIEW_CONTACTS = 51
        private val REQUEST_CODE_VIEW_CONTACTS_ON_CREATE = 52

        fun newInstance(engagementId: String, listMode: ContactsListAdapter.ListMode): EmergencyContactOperationsFragment {
            val emergencyContactOperationsFragment = EmergencyContactOperationsFragment()

            val bundle = Bundle()
            bundle.putString(Constants.KEY_ENGAGEMENT_ID, engagementId)
            bundle.putInt(LIST_MODE, listMode.ordinal)
            emergencyContactOperationsFragment.arguments = bundle

            return emergencyContactOperationsFragment
        }
    }


}
