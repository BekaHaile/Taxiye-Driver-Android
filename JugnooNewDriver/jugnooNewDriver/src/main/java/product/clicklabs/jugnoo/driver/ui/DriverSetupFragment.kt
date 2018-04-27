package product.clicklabs.jugnoo.driver.ui

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.HomeUtil
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.retrofit.model.CityResponse
import product.clicklabs.jugnoo.driver.ui.adapters.VehicleTypeSelectionAdapter
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallback
import product.clicklabs.jugnoo.driver.ui.api.ApiCommon
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.utils.Fonts



class DriverSetupFragment : Fragment() {
    private lateinit var accessToken: String
    private lateinit var vehicleTypes: ArrayList<CityResponse.VehicleType>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accessToken = it.getString(Constants.KEY_ACCESS_TOKEN)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_driver_info_update, container, false)
    }

    private lateinit var editTextName: EditText
    private lateinit var rvVehicleTypes: RecyclerView
    private lateinit var bContinue: Button
    private lateinit var bCancel: Button
    private lateinit var adapter: VehicleTypeSelectionAdapter

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (view?.findViewById(R.id.tvEnterName) as TextView?)?.typeface = Fonts.mavenLight(activity)
        editTextName = view?.findViewById(R.id.editTextName) as EditText
        editTextName.typeface = Fonts.mavenRegular(activity)
        (view.findViewById(R.id.tvSelectVehicle) as TextView?)?.typeface = Fonts.mavenLight(activity)
        rvVehicleTypes = view.findViewById(R.id.rvVehicleTypes) as RecyclerView
        rvVehicleTypes.layoutManager = GridLayoutManager(activity, 3)
        bContinue = view.findViewById(R.id.bContinue) as Button
        bContinue.typeface = Fonts.mavenRegular(activity)
        bCancel = view.findViewById(R.id.bCancel) as Button
        bCancel.typeface = Fonts.mavenRegular(activity)

        adapter = VehicleTypeSelectionAdapter(activity, rvVehicleTypes, vehicleTypes);
        rvVehicleTypes.adapter = adapter;

        getCitiesAPI();

    }


    private fun getCitiesAPI(){
        val params = HashMap<String, String>()
        params.put(Constants.KEY_LATITUDE, Data.latitude.toString())
        params.put(Constants.KEY_LONGITUDE, Data.longitude.toString())
        HomeUtil.putDefaultParams(params)

        ApiCommon<CityResponse>(activity).execute(params, ApiName.GET_CITIES, object : APICommonCallback<CityResponse>(){
            override fun onSuccess(t: CityResponse?, message: String?, flag: Int) {
                vehicleTypes = t?.vehicleTypes as ArrayList<CityResponse.VehicleType>
                adapter.setList(vehicleTypes);
            }

            override fun onError(t: CityResponse?, message: String?, flag: Int): Boolean {
                return false;
            }
        })

    }


    companion object {
        @JvmStatic
        fun newInstance(accessToken: String) =
                DriverSetupFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                    }
                }
    }
}
