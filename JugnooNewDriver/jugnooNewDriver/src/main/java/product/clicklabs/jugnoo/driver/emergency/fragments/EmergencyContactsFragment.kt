package product.clicklabs.jugnoo.driver.emergency.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.apis.ApiEmergencyContactsList
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.emergency.EmergencyActivity
import product.clicklabs.jugnoo.driver.emergency.FragTransUtils
import product.clicklabs.jugnoo.driver.emergency.adapters.ContactsListAdapter
import product.clicklabs.jugnoo.driver.emergency.models.ContactBean
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt
import product.clicklabs.jugnoo.driver.utils.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.*


/**
 * For displaying emergency contacts
 * and options to add or delete them
 *
 * Created by shankar on 2/22/16.
 */

@SuppressLint("ValidFragment")
class EmergencyContactsFragment : Fragment() {
    private val TAG = EmergencyContactsFragment::class.java.simpleName
    private var relative: RelativeLayout? = null

    private var title: TextView? = null
    private var textViewEdit: TextView? = null
    private var backBtn: ImageView? = null

    private var linearLayoutContactsList: LinearLayout? = null
    private var recyclerViewContacts: RecyclerView? = null

    private var linearLayoutNoContacts: LinearLayout? = null

    private var buttonAddContact: Button? = null
    private var contactsListAdapter: ContactsListAdapter? = null
    private var contactBeans: ArrayList<ContactBean>? = null

    private var rootView: View? = null
    private var mPermissionCommon: PermissionCommon? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_emergency_contacts, container, false)

        mPermissionCommon = PermissionCommon(this).setCallback(object : PermissionCommon.PermissionListener {
            override fun permissionGranted(requestCode: Int) {
                openAddEmergencyContactsFragments()
            }

            override fun permissionDenied(requestCode: Int, neverAsk: Boolean): Boolean {
                return true
            }

            override fun onRationalRequestIntercepted() {

            }

        })

        relative = rootView!!.findViewById<View>(R.id.relative) as RelativeLayout
        try {
            ASSL(activity, relative, 1134, 720, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        title = rootView!!.findViewById<View>(R.id.title) as TextView
        title!!.setText(R.string.emergency_contacts_only)
        backBtn = rootView!!.findViewById<View>(R.id.backBtn) as ImageView
        textViewEdit = rootView!!.findViewById<View>(R.id.textViewEdit) as TextView
        textViewEdit!!.typeface = Fonts.mavenRegular(activity!!)

        linearLayoutContactsList = rootView!!.findViewById<View>(R.id.linearLayoutContactsList) as LinearLayout
        (rootView!!.findViewById<View>(R.id.textViewContacts) as TextView).typeface = Fonts.mavenMedium(activity!!)
        recyclerViewContacts = rootView!!.findViewById<View>(R.id.recyclerViewContacts) as RecyclerView
        recyclerViewContacts!!.layoutManager = LinearLayoutManagerForResizableRecyclerView(activity)
        recyclerViewContacts!!.itemAnimator = DefaultItemAnimator()
        recyclerViewContacts!!.setHasFixedSize(false)

        linearLayoutNoContacts = rootView!!.findViewById<View>(R.id.linearLayoutNoContacts) as LinearLayout
        (rootView!!.findViewById<View>(R.id.textViewConfigureContacts) as TextView).setTypeface(Fonts.mavenRegular(activity!!), Typeface.BOLD)
        (rootView!!.findViewById<View>(R.id.textViewInformYourFriends) as TextView).typeface = Fonts.mavenLight(activity!!)
        (rootView!!.findViewById<View>(R.id.textViewInformYourFriends) as TextView).text = getString(R.string.inform_your_friends, getString(R.string.appname))

        textViewEdit!!.visibility = View.VISIBLE
        linearLayoutContactsList!!.visibility = View.VISIBLE
        linearLayoutNoContacts!!.visibility = View.GONE



        buttonAddContact = rootView!!.findViewById<View>(R.id.buttonAddContact) as Button
        buttonAddContact!!.typeface = Fonts.mavenRegular(activity!!)

        contactBeans = ArrayList()
        contactsListAdapter = ContactsListAdapter(contactBeans!!, activity!!, R.layout.list_item_contact,
                object : ContactsListAdapter.Callback {
                    override fun contactClicked(position: Int, contactBean: ContactBean) {
                        if (ContactsListAdapter.ListMode.DELETE_CONTACTS === contactsListAdapter!!.listMode) {
                            DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
                                    activity!!.resources.getString(R.string.delete_emergency_contact_message),
                                    activity!!.resources.getString(R.string.delete),
                                    activity!!.resources.getString(R.string.cancel),
                                    { deleteEmergencyContactAPI(activity, contactBean.id) },
                                    { }, true, false)
                        }
                    }
                }, ContactsListAdapter.ListMode.EMERGENCY_CONTACTS)
        recyclerViewContacts!!.adapter = contactsListAdapter


        val onClickListener = View.OnClickListener { v ->
            when (v.id) {

                R.id.backBtn -> performBackPressed()

                R.id.textViewEdit -> if (ContactsListAdapter.ListMode.EMERGENCY_CONTACTS === contactsListAdapter!!.listMode) {
                    contactsListAdapter!!.listMode = ContactsListAdapter.ListMode.DELETE_CONTACTS
                    contactsListAdapter!!.setCountAndNotify()
                    textViewEdit!!.text = activity!!.resources.getString(R.string.done)
                } else if (ContactsListAdapter.ListMode.DELETE_CONTACTS === contactsListAdapter!!.listMode) {
                    contactsListAdapter!!.listMode = ContactsListAdapter.ListMode.EMERGENCY_CONTACTS
                    contactsListAdapter!!.setCountAndNotify()
                    textViewEdit!!.text = activity!!.resources.getString(R.string.edit)
                }

                R.id.buttonAddContact -> mPermissionCommon!!.getPermission(REQUEST_CODE_CONTACT, Manifest.permission.READ_CONTACTS)
            }
        }


        backBtn!!.setOnClickListener(onClickListener)
        textViewEdit!!.setOnClickListener(onClickListener)
        textViewEdit!!.text = activity!!.resources.getString(R.string.edit)
        buttonAddContact!!.setOnClickListener(onClickListener)


        getAllEmergencyContacts(activity)


        return rootView
    }

    private fun openAddEmergencyContactsFragments() {
        FragTransUtils().openAddEmergencyContactsFragment(requireActivity(),
                (activity as EmergencyActivity).container)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionCommon!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun performBackPressed() {
        if (activity is EmergencyActivity) {
            (activity as EmergencyActivity).performBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ASSL.closeActivity(rootView)
        System.gc()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            getAllEmergencyContacts(activity)
            if (ContactsListAdapter.ListMode.DELETE_CONTACTS === contactsListAdapter!!.listMode) {
                textViewEdit!!.performClick()
            }
        }
    }

    fun getAllEmergencyContacts(activity: Activity?) {
        ApiEmergencyContactsList(activity!!, object : ApiEmergencyContactsList.Callback {
            override fun onSuccess() {
                setEmergencyContactsToList()
            }

            override fun onFailure() {

            }

            override fun onRetry(view: View) {

            }

            override fun onNoRetry(view: View) {

            }
        }).emergencyContactsList()
    }


    private fun setEmergencyContactsToList() {
        if (Data.userData.emergencyContactsList != null) {
            contactBeans!!.clear()
            for (emergencyContact in Data.userData.emergencyContactsList) {
                val contactBean = ContactBean(emergencyContact.name, emergencyContact.phoneNo, emergencyContact.countryCode, "")
                contactBean.id = emergencyContact.id
                contactBeans!!.add(contactBean)
            }
            notifyListAndShowAddContactsButton()
            EmergencyActivity.setEmergencyContactsAllowedToAdd()

            if (contactBeans!!.size > 0) {
                textViewEdit!!.visibility = View.VISIBLE
                linearLayoutContactsList!!.visibility = View.VISIBLE
                linearLayoutNoContacts!!.visibility = View.GONE
            } else {
                textViewEdit!!.visibility = View.GONE
                linearLayoutContactsList!!.visibility = View.GONE
                linearLayoutNoContacts!!.visibility = View.VISIBLE
            }
        }
    }

    private fun notifyListAndShowAddContactsButton() {
        contactsListAdapter!!.notifyDataSetChanged()
        if (contactBeans!!.size < 5) {
            buttonAddContact!!.visibility = View.VISIBLE
        } else {
            buttonAddContact!!.visibility = View.GONE
        }
    }


    fun deleteEmergencyContactAPI(activity: Activity?, id: Int) {
        try {
            if (MyApplication.getInstance().isOnline) {

                DialogPopup.showLoadingDialog(activity, activity!!.resources.getString(R.string.loading))

                val params = HashMap<String, String>()

                params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
                params[Constants.KEY_ID] = id.toString()

                Log.i("params", "=" + params.toString())

                HomeUtil.putDefaultParams(params)
                RestClient.getApiServices().emergencyContactsDelete(params, object : Callback<SettleUserDebt> {
                    override fun success(settleUserDebt: SettleUserDebt, response: Response) {
                        val responseStr = String((response.body as TypedByteArray).bytes)
                        Log.i(TAG, "emergencyContactsDelete response = $responseStr")
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
                                    getAllEmergencyContacts(activity)
                                } else {
                                    DialogPopup.dialogBanner(activity, message)
                                }
                            }
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                            DialogPopup.alertPopup(activity, "", activity.getString(R.string.server_error))
                        }

                        DialogPopup.dismissLoadingDialog()
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e(TAG, "error=" + error.toString())
                        DialogPopup.dismissLoadingDialog()
                        DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost))
                    }
                })
            } else {
                DialogPopup.alertPopup(activity, "", activity!!.getString(R.string.no_internet))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        val REQUEST_CODE_CONTACT = 1000
    }


}
