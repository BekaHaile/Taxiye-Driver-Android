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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.retrofit.model.CityResponse
import product.clicklabs.jugnoo.driver.ui.adapters.VehicleTypeSelectionAdapter
import product.clicklabs.jugnoo.driver.utils.Fonts



class DriverSetupFragment : Fragment() {
    private lateinit var accessToken: String
    private lateinit var vehicleTypes: ArrayList<CityResponse.VehicleType>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accessToken = it.getString(Constants.KEY_ACCESS_TOKEN)
            val vehicleTypesStr: String? = it.getString(Constants.KEY_VEHICLE_TYPES);
            vehicleTypes = Gson().fromJson<java.util.ArrayList<CityResponse.VehicleType>>(vehicleTypesStr, object : TypeToken<List<CityResponse.VehicleType>>() {}.type)
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

    }


    companion object {
        @JvmStatic
        fun newInstance(accessToken: String, vehicleTypes: ArrayList<CityResponse.VehicleType>) =
                DriverSetupFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                        val element = Gson().toJsonTree(vehicleTypes, object : TypeToken<List<CityResponse.VehicleType>>() {}.type)
                        putString(Constants.KEY_VEHICLE_TYPES, element.asJsonArray.toString())
                    }
                }
    }
}
