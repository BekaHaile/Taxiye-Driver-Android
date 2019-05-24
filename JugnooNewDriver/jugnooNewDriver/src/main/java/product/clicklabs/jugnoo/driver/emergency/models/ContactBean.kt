package product.clicklabs.jugnoo.driver.emergency.models

/**
 * Created by shankar on 2/23/16.
 */
class ContactBean(var name: String, var phoneNo: String, var countryCode: String, var type: String) {

    var id: Int = 0
    var isSelected: Boolean = false


    init {
        this.isSelected = false
    }

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        try {
            return other is ContactBean && other.phoneNo.equals(this.phoneNo, ignoreCase = true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

}
