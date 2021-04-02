package product.clicklabs.jugnoo.driver.emergency

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
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
class ContactsFetchAsync(private val activity: Context, private val contactBeans: ArrayList<ContactBean>, private val callback: Callback) : AsyncTask<String?, Int?, String>() {
    private var stopInterrupt = false
    private val progressDialog: ProgressDialog
    override fun onPreExecute() {
        super.onPreExecute()
        showLoading()
        callback.onPreExecute()
    }

    override fun doInBackground(vararg params: String?): String {
        fetchContacts()
        return ""
    }

    override fun onPostExecute(s: String) {
        super.onPostExecute(s)
        progressDialog.dismiss()
        if (!stopInterrupt) {
            callback.onPostExecute(contactBeans)
        }
    }

    private fun fetchContacts() {
        val contactBeans = ArrayList<ContactBean>()
        try {
            val cr = activity.contentResolver
            val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
            if (cur!!.count > 0) {
                progressDialog.max = cur.count
                while (cur.moveToNext() && !stopInterrupt) {
                    val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val hasPhoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    val imageUri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                    var uri: Uri? = null
                    if (imageUri != null) {
                        try {
                            uri = Uri.parse(imageUri)
                        } catch (e: java.lang.Exception) {
                        }
                    }
                    if (hasPhoneNumber.toInt() > 0) {
                        val pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)
                        while (pCur!!.moveToNext() && !stopInterrupt) {
                            var phone = pCur
                                    .getString(pCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            var type =""
                            type = if(pCur.getString(
                                            pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))== null){
                                ""
                            }else
                                getContactTypeString(activity, pCur.getString(
                                        pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)))

                            phone = phone.replace(" ", "")
                            phone = phone.replace("-", "")
                            if (Utils.validPhoneNumber(phone)) {
                                contactBeans.add(ContactBean(name, phone, "", type, ContactBean.ContactBeanViewType.CONTACT, uri, null))
                            }
                        }
                        pCur.close()
                    }
                    progressDialog.incrementProgressBy(1)
                }
            }
            cur.close()
            loadList(contactBeans)
            return
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return
    }

    private fun loadList(list: ArrayList<ContactBean>) {
        if (!stopInterrupt) {
            val set: MutableSet<ContactBean> = object: TreeSet<ContactBean>(
                    object : Comparator<ContactBean?> {
                        override fun compare(o1: ContactBean?, o2: ContactBean?): Int {
                            return if (o1?.phoneNo.equals(o2?.phoneNo)) {
                                0
                            } else 1
                        }
                    }
            ){}
            set.addAll(list)
            val newList: ArrayList<ContactBean> = ArrayList<ContactBean>(set)
            Collections.sort(newList, Comparator { o1: ContactBean, o2: ContactBean -> o1.name?.compareTo(o2.name?:"", true) ?: 0 })
            contactBeans.addAll(newList)
        }
    }

    private fun getContactTypeString(context: Context, type: String): String {
        return try {
            val typeInt = type.toInt()
            if (typeInt == ContactsContract.CommonDataKinds.Phone.TYPE_HOME) {
                context.getString(R.string.home)
            } else if (typeInt == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                context.getString(R.string.mobile)
            } else if (typeInt == ContactsContract.CommonDataKinds.Phone.TYPE_WORK) {
                context.getString(R.string.work)
            } else {
                context.getString(R.string.other)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            context.getString(R.string.mobile)
        }
    }

    fun stop() {
        stopInterrupt = true
        cancel(true)
        callback.onCancel()
    }

    interface Callback {
        fun onPreExecute()
        fun onPostExecute(contactBeans: ArrayList<ContactBean>)
        fun onCancel()
    }

    private fun showLoading() {
        progressDialog.setMessage("Loading contacts...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { dialog: DialogInterface?, which: Int ->
            stop()
            progressDialog.dismiss()
        }
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    init {
        stopInterrupt = false
        progressDialog = ProgressDialog(activity, R.style.MyProgressDialog)
    }

}

