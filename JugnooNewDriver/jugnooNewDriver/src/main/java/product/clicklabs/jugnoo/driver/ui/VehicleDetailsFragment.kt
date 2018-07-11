package product.clicklabs.jugnoo.driver.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.picker.CountryPickerDialog
import com.picker.OnCountryPickerListener
import kotlinx.android.synthetic.main.fragment_driver_info_update.*
import kotlinx.android.synthetic.main.fragment_vehicle_model.*
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.DriverDocumentActivity
import product.clicklabs.jugnoo.driver.IncomingSmsReceiver
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.*
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Utils
import product.clicklabs.jugnoo.driver.utils.gone
import product.clicklabs.jugnoo.driver.utils.inflate
import retrofit.RetrofitError
import java.lang.Exception
import java.util.*

/**
 * Created by Parminder Saini on 09/07/18.
 */
class VehicleDetailsFragment : Fragment() {

    private val  ARGS_CITY_ID = "city_id"
    private val  ARGS_VEHICLE_TYPE = "vehicle_type"

    private lateinit var toolbarChangeListener: ToolbarChangeListener
    private lateinit var cityId:String
    private lateinit var vehicleType:String
    private lateinit var accessToken:String
    private val  VEHICLE_MAKE_DIALOG_FRAGMENT_TAG = "vehicle_make_dialog"
    private val  VEHICLE_MODEL_DIALOG_FRAGMENT_TAG = "vehicle_model_dialog"
    private val  VEHICLE_COLOR_DIALOG_FRAGMENT_TAG = "vehicle_color_dialog"

    private lateinit var vehiceMakeModelData: Map<String,List<VehicleModelDetails>>
    private var vehicleMakeList: MutableList<VehicleMakeInfo>?=null
    private var vehicleColorList: List<VehicleModelCustomisationDetails>?=null
    private var currentMakeSelected:VehicleMakeInfo? = null;
    private var currentModelSelected:VehicleModelDetails? = null;
    private var currentCustomisationSelected:VehicleModelCustomisationDetails? = null;
    private val calendar = Calendar.getInstance()
    private val minYear = 1885

    companion object {
        @JvmStatic
        fun newInstance(accessToken: String,cityId:String,vehicleType:String) =
                VehicleDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                        putString(ARGS_CITY_ID, cityId)
                        putString(ARGS_VEHICLE_TYPE, vehicleType)
                    }
                }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        toolbarChangeListener  = context as ToolbarChangeListener
        toolbarChangeListener.setToolbarText(getString(R.string.title_vehicle_details))
        toolbarChangeListener.setToolbarVisibility(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accessToken = it.getString(Constants.KEY_ACCESS_TOKEN)
            cityId = it.getString(ARGS_CITY_ID)
            vehicleType = it.getString(ARGS_VEHICLE_TYPE)
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return container?.inflate(R.layout.fragment_vehicle_model);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtMake.setOnClickListener{if (::vehiceMakeModelData.isInitialized){
            showSelectionDialog(vehicleMakeInteractionListener,makeSelectionListener,VEHICLE_MAKE_DIALOG_FRAGMENT_TAG,getString(R.string.select_make))
        }}
        edtModel.setOnClickListener{if (currentMakeSelected!=null){
            showSelectionDialog(vehicleModelInteractionListener,modelSelectionListener,VEHICLE_MODEL_DIALOG_FRAGMENT_TAG,getString(R.string.select_model))
        }}
        edtColor.setOnClickListener{if (currentModelSelected!=null){
            showSelectionDialog(vehicleColorInteractionListener,colorSelectionListener,VEHICLE_COLOR_DIALOG_FRAGMENT_TAG,getString(R.string.select_color))
        }else{
            Toast.makeText(requireContext(),getString(R.string.error_select_car_make),Toast.LENGTH_SHORT).show();
        }}
        edtYear.addTextChangedListener(yearWatcher);
        btn_continue.setOnClickListener{
            submitVehicleDetails()
        }

        getVehicleDetails();
    }

    fun getVehicleDetails(){

        val params = hashMapOf(
                Constants.KEY_ACCESS_TOKEN to accessToken,
                "city_id" to cityId,
                "vehicle_type" to vehicleType)

        ApiCommonKt<VehicleDetailsResponse>(requireActivity()).execute(params, ApiName.VEHICLE_MAKE_DATA,
                object: APICommonCallbackKotlin<VehicleDetailsResponse>(){
                    override fun onSuccess(t: VehicleDetailsResponse?, message: String?, flag: Int) {
                        vehiceMakeModelData = t?.models!!;
                        prepareMakeList()
                    }

                    override fun onError(t: VehicleDetailsResponse?, message: String?, flag: Int): Boolean {
                        groupView.gone()
                        DialogPopup.alertPopupWithListener(requireActivity(), "", message, { requireActivity().onBackPressed() })
                        return true;
                    }

                    override fun onNotConnected(): Boolean {
                        onError(null,requireActivity().getString(R.string.check_internet_message),0)
                        return true
                    }

                    override fun onFailure(error: RetrofitError?): Boolean {
                        onError(null,requireActivity().getString(R.string.some_error_occured),0)
                        return true
                    }

                    override fun onException(e: Exception?): Boolean {
                        onError(null,requireActivity().getString(R.string.some_error_occured),0)
                        return true
                    }


                })


    }

    fun getModelDetails(modelRequested: VehicleModelDetails) {

        val params = hashMapOf(
                Constants.KEY_ACCESS_TOKEN to accessToken,
                "city_id" to cityId,
                "vehicle_type" to vehicleType,
                "model_id" to "" +modelRequested.id)

        ApiCommonKt<VehicleModelCustomisationsResponse>(requireActivity()).execute(params, ApiName.VEHICLE_MODEL_DATA,
                object: APICommonCallbackKotlin<VehicleModelCustomisationsResponse>(){

                    override fun onSuccess(t: VehicleModelCustomisationsResponse?, message: String?, flag: Int) {



                        currentModelSelected = modelRequested
                        edtModel.setText(modelRequested.modelName)
                        edtSeatBelt.setText(""+modelRequested.noOfSeatBelts)
                        edtDoor.setText(""+modelRequested.noOfDoors)
                        vehicleColorList = t!!.customisationList;
                        if(vehicleColorList!=null  && vehicleColorList!!.size>0){
                            currentCustomisationSelected = vehicleColorList!![0]
                            edtColor.setText(vehicleColorList!![0].color)
                        }



                    }

                    override fun onError(t: VehicleModelCustomisationsResponse?, message: String?, flag: Int): Boolean {
                        return false
                    }

                })
    }

    private fun prepareMakeList(){
       vehicleMakeList = mutableListOf()
        for(make in vehiceMakeModelData.keys){
            vehicleMakeList?.add(VehicleMakeInfo(make));
        }
    }

    fun <T:SearchDataModel> showSelectionDialog(
            interactionListener:CountryPickerDialog.CountryPickerDialogInteractionListener<T>
            ,pickerListener: OnCountryPickerListener<T>
            ,tag:String,title:String) {
        if (interactionListener.allCountries == null || interactionListener.allCountries!!.isEmpty()) {
            Toast.makeText(requireActivity(),getString(R.string.no_results_found),Toast.LENGTH_SHORT).show()
        } else {
            val countryPickerDialog = CountryPickerDialog.newInstance(title)
            countryPickerDialog.setCountryPickerListener(pickerListener)
            countryPickerDialog.setDialogInteractionListener(interactionListener)
            countryPickerDialog.show(requireActivity().supportFragmentManager, tag)
        }
    }

    val vehicleMakeInteractionListener  = object : CountryPickerDialog.CountryPickerDialogInteractionListener<VehicleMakeInfo>{
        override fun getAllCountries(): MutableList<VehicleMakeInfo>? {
                return vehicleMakeList
        }

        override fun sortCountries(searchResults: MutableList<VehicleMakeInfo>?) {

        }

        override fun canSearch(): Boolean {
            return  vehicleMakeList!=null && vehicleMakeList!!.size>7;
        }

    }
    val makeSelectionListener = object : OnCountryPickerListener<VehicleMakeInfo>{
        override fun onSelectCountry(country: VehicleMakeInfo) {
            if(currentMakeSelected==null || !currentMakeSelected!!.makeName.equals(country.makeName)){
                currentMakeSelected = country
                edtMake.setText(country.makeName)
            }

        }

    }

    val vehicleModelInteractionListener  = object : CountryPickerDialog.CountryPickerDialogInteractionListener<VehicleModelDetails>{
        override fun getAllCountries(): List<VehicleModelDetails>? {
                return vehiceMakeModelData.get(currentMakeSelected?.makeName)
        }

        override fun sortCountries(searchResults: MutableList<VehicleModelDetails>?) {

        }

        override fun canSearch(): Boolean {
            return  vehiceMakeModelData.get(currentMakeSelected?.makeName)!!.size>7;
        }

    }
    val modelSelectionListener = object : OnCountryPickerListener<VehicleModelDetails>{
        override fun onSelectCountry(country: VehicleModelDetails) {
            if (currentModelSelected==null || country.id!=currentModelSelected!!.id) {

                getModelDetails(country)
            }

        }

    }

    val vehicleColorInteractionListener  = object : CountryPickerDialog.CountryPickerDialogInteractionListener<VehicleModelCustomisationDetails>{
        override fun getAllCountries(): List<VehicleModelCustomisationDetails>? {
                return vehicleColorList
        }

        override fun sortCountries(searchResults: MutableList<VehicleModelCustomisationDetails>?) {

        }

        override fun canSearch(): Boolean {
            return  vehicleColorList!=null && vehicleColorList!!.size>7;
        }

    }
    val colorSelectionListener = object : OnCountryPickerListener<VehicleModelCustomisationDetails>{
        override fun onSelectCountry(country: VehicleModelCustomisationDetails) {
            this@VehicleDetailsFragment.currentCustomisationSelected = country
            edtColor.setText(country.color)


        }

    }


    fun submitVehicleDetails(){

        val year = edtYear.text.toString().trim();
        if(!isYearValid(year))
        {
            Toast.makeText(requireContext(),getString(R.string.invalid_year_error,minYear,calendar.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            return;
        }

        if(currentMakeSelected==null)
        {
            Toast.makeText(requireContext(),getString(R.string.invalid_make),Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentModelSelected==null)
        {
            Toast.makeText(requireContext(),getString(R.string.invalid_model),Toast.LENGTH_SHORT).show();
            return;
        }

        if(currentCustomisationSelected==null)
        {
            Toast.makeText(requireContext(),getString(R.string.invalid_color),Toast.LENGTH_SHORT).show();
            return;
        }



        val params = hashMapOf(
                Constants.KEY_ACCESS_TOKEN to accessToken,
                "city" to ""+cityId,
                "vehicle_make_id" to ""+currentCustomisationSelected!!.id,
                 "vehicle_type" to vehicleType,
                "vehicle_year" to ""+year)



        ApiCommonKt<FeedCommonResponseKotlin>(requireActivity()).execute(params,ApiName.REGISTER_DRIVER,object : APICommonCallbackKotlin<FeedCommonResponseKotlin>(){
            override fun onSuccess(t: FeedCommonResponseKotlin?, message: String?, flag: Int) {

                if(t!=null){
                    when (t.flag) {
                        ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal(), ApiResponseFlags.ACTION_COMPLETE.getOrdinal() -> {
                            openDocumentUploadActivity()
                        }


                        ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal(), ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() -> {
                            DialogPopup.alertPopupWithListener(activity, "", message, {
                                (requireActivity() as DriverSplashActivity).openPhoneLoginScreen()
                                (requireActivity() as DriverSplashActivity).setToolbarVisibility(false)
                            })

                        }
                        else ->onError(t,message,flag)
                    }
                }

            }

            override fun onError(t: FeedCommonResponseKotlin?, message: String?, flag: Int): Boolean {
                return false;
            }

        })
    }

    private fun isYearValid(yearString: String): Boolean {
        try {
            val year = Integer.parseInt(yearString)
            return  year>minYear&&year<=calendar.get(Calendar.YEAR);
        } catch (e: Exception) {
            return false
        }
    }

    private fun openDocumentUploadActivity() {
        val intent = Intent(activity, DriverDocumentActivity::class.java).apply {
            putExtra("access_token", accessToken)
            putExtra("in_side", false)
            putExtra("doc_required", 3)
        }
        Utils.enableReceiver(activity, IncomingSmsReceiver::class.java, false)
        startActivity(intent)
    }

    private val yearWatcher = object:TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            if(s.length==4 && isYearValid(s.toString())){
                edtYear.setTextColor(ContextCompat.getColor(requireContext(),R.color.textColor))
            }else{
                edtYear.setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
            }

        }

    }

}