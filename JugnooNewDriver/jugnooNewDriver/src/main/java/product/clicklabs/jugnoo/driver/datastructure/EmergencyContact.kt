package product.clicklabs.jugnoo.driver.datastructure

/**
 * Created by socomo20 on 6/30/15.
 */
class EmergencyContact {

    var id: Int = 0
    var name: String = ""
    var phoneNo: String = ""
    var countryCode: String = ""

    constructor(id: Int, name: String, phoneNo: String, countryCode: String) {
        this.id = id
        this.name = name
        this.phoneNo = phoneNo
        this.countryCode = countryCode
    }

    constructor(id: Int) {
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        try {
            return (other as EmergencyContact).id == this.id
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }
}
