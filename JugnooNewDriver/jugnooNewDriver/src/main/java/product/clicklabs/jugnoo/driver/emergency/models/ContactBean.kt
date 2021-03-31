package product.clicklabs.jugnoo.driver.emergency.models

import android.net.Uri

/**
 * Created by shankar on 2/23/16.
 */
class ContactBean {
    var id = 0
    var name: String? = null
    var phoneNo: String
    var countryCode: String? = null
    var type: String? = null
    var isSelected = false
    private var contactBeanViewType: ContactBean.ContactBeanViewType? = null
    var imageUri: Uri? = null
    var imageUrl: String? = null

    constructor(name: String?, phoneNo: String, countryCode: String?, type: String?, contactBeanViewType: ContactBean.ContactBeanViewType?, imageUri: Uri?, imageUrl: String?) {
        this.name = name
        this.phoneNo = phoneNo
        this.countryCode = countryCode
        this.type = type
        this.contactBeanViewType = contactBeanViewType
        this.imageUri = imageUri
        this.imageUrl = imageUrl
        isSelected = false
    }

    constructor(phoneNo: String) {
        this.phoneNo = phoneNo
    }

    override fun toString(): String {
        return name!!
    }

    override fun equals(o: Any?): Boolean {
        try {
            return o is ContactBean && (o.phoneNo.contains(phoneNo)
                    || phoneNo.contains(o.phoneNo))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getContactBeanViewType(): ContactBean.ContactBeanViewType? {
        return contactBeanViewType
    }

    fun setContactBeanViewType(contactBeanViewType: ContactBean.ContactBeanViewType?) {
        this.contactBeanViewType = contactBeanViewType
    }

    enum class ContactBeanViewType(var type: Int) {
        CONTACT(0), EMERGENCY_CONTACTS(1), PHONE_CONTACTS(2);

    }
}
