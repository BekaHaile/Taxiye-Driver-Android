package product.clicklabs.jugnoo.driver.datastructure

enum class GenderValues(val type: Int){
    MALE(1),
    FEMALE(2),
    OTHER(3)
}

class Gender(val type:Int, val name:String){
    override fun toString(): String {
        return name
    }
}