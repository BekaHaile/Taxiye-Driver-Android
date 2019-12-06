package product.clicklabs.jugnoo.driver.emergency

import android.content.Context
import android.os.AsyncTask
import android.provider.ContactsContract
import androidx.fragment.app.FragmentActivity
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.emergency.models.ContactBean
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Utils
import java.util.*

/**
 * Created by shankar on 2/26/16.
 */
class ContactsFetchAsync(private val activity: FragmentActivity, private val contactBeans: ArrayList<ContactBean>, private val showLoadingDialog: Boolean, private val callback: Callback) : AsyncTask<String, Int, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
        if (showLoadingDialog) {
            DialogPopup.showLoadingDialog(activity, activity.resources.getString(R.string.loading))
        }
        callback.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String {
        fetchContacts()
        return ""
    }

    override fun onPostExecute(s: String) {
        super.onPostExecute(s)
        if (showLoadingDialog) {
            DialogPopup.dismissLoadingDialog()
        }
        callback.onPostExecute(contactBeans)
    }

    private fun fetchContacts() {
        val contactBeans = ArrayList<ContactBean>()
        try {

            val cr = activity.contentResolver
            val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

            if (cur!!.count > 0) {
                while (cur.moveToNext()) {
                    val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val hasPhoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    if (Integer.parseInt(hasPhoneNumber) > 0) {
                        val pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(id), null)

                        while (pCur!!.moveToNext()) {
                            var phone: String? = pCur
                                    .getString(pCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            val type = getContactTypeString(activity, pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)))

                            phone = Utils.retrievePhoneNumberTenChars("",phone!!)
                            if (phone != null && Utils.validPhoneNumber(phone)) {
                                contactBeans.add(ContactBean(name, phone, "", type))
                            }
                        }
                        pCur.close()
                    }
                }
            }
            cur.close()

            loadList(contactBeans)


            return

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return
    }

    private fun loadList(list: ArrayList<ContactBean>) {

        val set = TreeSet(Comparator<ContactBean> { o1, o2 ->
            if (o1.phoneNo.toString().equals(o2.phoneNo.toString(), ignoreCase = true)) {
                0
            } else 1
        })

        set.addAll(list)

        val newList = ArrayList(set)
        newList.sortWith(Comparator { o1, o2 ->
            o1.name.compareTo(o2.name, true)
        })
        contactBeans.addAll(newList)
    }


    private fun getContactTypeString(context: Context, type: String): String {
        try {
            val typeInt = Integer.parseInt(type)
            return if (typeInt == ContactsContract.CommonDataKinds.Phone.TYPE_HOME) {
                context.getString(R.string.home)
            } else if (typeInt == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                context.getString(R.string.mobile)
            } else if (typeInt == ContactsContract.CommonDataKinds.Phone.TYPE_WORK) {
                context.getString(R.string.work)
            } else {
                context.getString(R.string.other)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return context.getString(R.string.mobile)
        }

    }


    interface Callback {
        fun onPreExecute()
        fun onPostExecute(contactBeans: ArrayList<ContactBean>)
    }

}
