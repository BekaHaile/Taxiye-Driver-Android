package product.clicklabs.jugnoo.driver.emergency.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.emergency.EmergencyActivity
import product.clicklabs.jugnoo.driver.emergency.models.ContactBean
import product.clicklabs.jugnoo.driver.utils.ASSL
import product.clicklabs.jugnoo.driver.utils.Fonts
import product.clicklabs.jugnoo.driver.utils.Utils
import java.util.*


/**
 * Created by shankar on 7/27/15.
 */
class ContactsListAdapter(contactBeans: ArrayList<ContactBean>, private val activity: Activity, private val rowLayout: Int,
                          private val callback: Callback, var listMode: ListMode?) : RecyclerView.Adapter<ContactsListAdapter.ViewHolderC>() {

    private val TAG = ContactsListAdapter::class.java.simpleName
    var contactBeans: ArrayList<ContactBean>? = ArrayList()
    private var selectedCount: Int = 0

    init {
        this.contactBeans = contactBeans
        this.selectedCount = 0
    }

    @Synchronized
    fun setList(contactBeans: ArrayList<ContactBean>) {
        this.contactBeans = contactBeans
        notifyDataSetChanged()
    }

    @Synchronized
    fun setCountAndNotify() {
        selectedCount = 0
        for (contactBean in contactBeans!!) {
            if (contactBean.isSelected) {
                selectedCount++
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderC {
        val v = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)

        val layoutParams = RecyclerView.LayoutParams(640, 120)
        v.layoutParams = layoutParams

        ASSL.DoMagic(v)
        return ViewHolderC(v, activity)
    }


    override fun onBindViewHolder(holder: ContactsListAdapter.ViewHolderC, position: Int) {
        val contactBean = contactBeans!![position]

        holder.textViewContactName.text = contactBean.name
        holder.textViewContactNumberType.text = contactBean.phoneNo + " " + contactBean.type

        if (ListMode.ADD_CONTACTS == listMode || ListMode.SEND_RIDE_STATUS == listMode) {
            if (ListMode.ADD_CONTACTS == listMode) {
                holder.imageViewOption.visibility = View.GONE
            }
            if (contactBean.isSelected) {
                holder.imageViewOption.setImageResource(R.drawable.option_checked_orange)
            } else {
                holder.imageViewOption.setImageResource(R.drawable.option_unchecked)
            }
        } else if (ListMode.EMERGENCY_CONTACTS == listMode) {
            holder.imageViewOption.visibility = View.GONE
        } else if (ListMode.DELETE_CONTACTS == listMode) {
            holder.imageViewOption.visibility = View.VISIBLE
            holder.imageViewOption.setImageResource(R.drawable.ic_cross_grey)
        } else if (ListMode.CALL_CONTACTS == listMode) {
            holder.imageViewOption.visibility = View.VISIBLE
            holder.imageViewOption.setImageResource(R.drawable.ic_phone_green)
        }

        holder.relative.tag = position

        holder.relative.setOnClickListener { v ->
            val pos = v.tag as Int
            if (ListMode.ADD_CONTACTS == listMode) {
                if (contactBeans!![pos].isSelected) {
                    contactBeans!![pos].isSelected = false
                    selectedCount--
                    callback.contactClicked(pos, contactBeans!![pos])
                } else if (selectedCount < EmergencyActivity.EMERGENCY_CONTACTS_ALLOWED_TO_ADD) {
                    contactBeans!![pos].isSelected = true
                    selectedCount++
                    callback.contactClicked(pos, contactBeans!![pos])
                } else {
                    Utils.showToast(activity, activity.getString(R.string.you_can_add_only_three_contacts))
                }
                notifyDataSetChanged()
            } else if (ListMode.EMERGENCY_CONTACTS == listMode) {
                callback.contactClicked(pos, contactBeans!![pos])
            } else if (ListMode.DELETE_CONTACTS == listMode) {
                callback.contactClicked(pos, contactBeans!![pos])
            } else if (ListMode.CALL_CONTACTS == listMode) {
                callback.contactClicked(pos, contactBeans!![pos])
            } else if (ListMode.SEND_RIDE_STATUS == listMode) {
                if (contactBeans!![pos].isSelected) {
                    contactBeans!![pos].isSelected = false
                    callback.contactClicked(pos, contactBeans!![pos])
                } else {
                    contactBeans!![pos].isSelected = true
                    callback.contactClicked(pos, contactBeans!![pos])
                }
                notifyDataSetChanged()
            }
        }

    }

    override fun getItemCount(): Int {
        return if (contactBeans == null) 0 else contactBeans!!.size
    }

    class ViewHolderC(itemView: View, activity: Activity) : RecyclerView.ViewHolder(itemView) {
        var relative: RelativeLayout
        var imageViewOption: ImageView
        var textViewContactName: TextView
        var textViewContactNumberType: TextView

        init {
            relative = itemView.findViewById<View>(R.id.relative) as RelativeLayout
            imageViewOption = itemView.findViewById(R.id.imageViewOption) as ImageView
            textViewContactName = itemView.findViewById(R.id.textViewContactName) as TextView
            textViewContactName.typeface = Fonts.mavenLight(activity)
            textViewContactNumberType = itemView.findViewById(R.id.textViewContactNumberType) as TextView
            textViewContactNumberType.typeface = Fonts.mavenLight(activity)
        }
    }

    interface Callback {
        fun contactClicked(position: Int, contactBean: ContactBean)
    }


    enum class ListMode constructor(ordinal: Int) {
        ADD_CONTACTS(0),
        EMERGENCY_CONTACTS(1),
        DELETE_CONTACTS(2),
        CALL_CONTACTS(3),
        SEND_RIDE_STATUS(4)
    }
}