package product.clicklabs.jugnoo.driver.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.VehicleDetails
import product.clicklabs.jugnoo.driver.utils.inflate

/**
 * Created by Parminder Saini on 09/07/18.
 */
class VehicleDetailsFragment : Fragment() {

    private lateinit var toolbarChangeListener: ToolbarChangeListener

    companion object {
        @JvmStatic
        fun newInstance(accessToken: String) =
                VehicleDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                    }
                }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        toolbarChangeListener  = context as ToolbarChangeListener
        toolbarChangeListener.setToolbarText(getString(R.string.register_as_driver))
        toolbarChangeListener.setToolbarVisibility(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return container?.inflate(R.layout.fragment_vehicle_model)
    }



    fun getVehicleDetails(){

        val params = hashMapOf(
                Constants.KEY_ACCESS_TOKEN to "access",
                "city_id" to "1",
                "vehicle_type" to  ""+"vehi$$")

        ApiCommonKt<VehicleDetails>(requireActivity()).execute(params, ApiName.VEHICLE_MAKE_DATA,
                object: APICommonCallbackKotlin<VehicleDetails>(){
                    override fun onSuccess(t: VehicleDetails?, message: String?, flag: Int) {


                    }

                    override fun onError(t: VehicleDetails?, message: String?, flag: Int): Boolean {
                        return false;
                    }

                })


    }



}