package product.clicklabs.jugnoo.driver.home

import io.paperdb.Paper
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo
import product.clicklabs.jugnoo.driver.utils.Log
import java.util.*

object CustomerInfoPaperUtil {
    private var TAG = CustomerInfoPaperUtil::class.java.simpleName
    private const val KEY_CUSTOMER_INFOS = "customer_infos"

    fun writeCustomerInfos(customerInfos: ArrayList<CustomerInfo>){
        Paper.book().write(KEY_CUSTOMER_INFOS, customerInfos)
        Log.e(TAG, "writeCustomerInfos customerInfos=$customerInfos")
    }


    fun readCustomerInfos(): ArrayList<CustomerInfo>{
        val list = Paper.book().read(KEY_CUSTOMER_INFOS, ArrayList<CustomerInfo>())
        Log.e(TAG, "readCustomerInfos list=$list")
        return list
    }

}