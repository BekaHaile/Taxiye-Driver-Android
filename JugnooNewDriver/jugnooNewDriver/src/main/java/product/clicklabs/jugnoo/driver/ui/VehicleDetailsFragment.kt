package product.clicklabs.jugnoo.driver.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.picker.CountryPickerDialog
import com.picker.OnCountryPickerListener
import kotlinx.android.synthetic.main.fragment_vehicle_model.*
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.adapters.VehicleDetailsLogin
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.*
import product.clicklabs.jugnoo.driver.utils.*
import retrofit.RetrofitError
import java.lang.Exception
import java.util.*

/**
 * Created by Parminder Saini on 09/07/18.
 */
class VehicleDetailsFragment : Fragment() {

    private val  ARGS_CITY_ID = "city_id"
    private val  ARGS_VEHICLE_TYPE = "vehicle_type"
    private val  ARGS_USER_NAME = "user_name"
    private val  ARGS_VEHICLE_DETAIL = "vehicle_detail"
    private val  ARGS_EDIT_MODE = "edit_mode"

    private var isEditMode = false
    private lateinit var toolbarChangeListener: ToolbarChangeListener
    private  var vehicleDetailsInteractor: VehicleDetailsInteractor?=null
    private lateinit var cityId:String
    private lateinit var userName:String
    private lateinit var vehicleType:String
    private lateinit var accessToken:String
    private val  VEHICLE_MAKE_DIALOG_FRAGMENT_TAG = "vehicle_make_dialog"
    private val  VEHICLE_MODEL_DIALOG_FRAGMENT_TAG = "vehicle_model_dialog"
    private val  VEHICLE_COLOR_DIALOG_FRAGMENT_TAG = "vehicle_color_dialog"

    private lateinit var vehiceMakeModelData: Map<String,List<VehicleModelDetails>>
    private var vehicleMakeList: MutableList<VehicleMakeInfo>?=null

    private var colorCustomisationList: List<VehicleModelCustomisationDetails>?=null
    private var doorsCustomisationList: List<VehicleModelCustomisationDetails>?=null
    private var seatBeltCustomisationList: List<VehicleModelCustomisationDetails>?=null

    private var currentMakeSelected:VehicleMakeInfo? = null
    private var currentModelSelected:VehicleModelDetails? = null

    private var currentColorSelected:VehicleModelCustomisationDetails? = null
    private var currentSeatBeltSelected:VehicleModelCustomisationDetails? = null
    private var currentDoorSelected:VehicleModelCustomisationDetails? = null

    private  var year:String?=null
    private  var vehicleNumber:String?=null

    private val calendar = Calendar.getInstance()
    private val minYear = 1885


    interface VehicleDetailsInteractor{

        fun onDetailsUpdated(vehicleDetails: VehicleDetailsLogin)
    }

    companion object {
        @JvmStatic @JvmOverloads
        fun newInstance(accessToken: String, cityId:String, vehicleType:String, userName:String,
                        vehicleDetails: VehicleDetailsLogin?=null,editMode:Boolean = false)=
                VehicleDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                        putString(ARGS_CITY_ID, cityId)
                        putString(ARGS_VEHICLE_TYPE, vehicleType)
                        putString(ARGS_USER_NAME, userName)
                        putBoolean(ARGS_EDIT_MODE, editMode)
                        if(vehicleDetails!=null){
                            putParcelable(ARGS_VEHICLE_DETAIL, vehicleDetails)
                        }

                    }
                }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is ToolbarChangeListener){
            toolbarChangeListener  = context
            toolbarChangeListener.setToolbarText(getString(R.string.title_vehicle_details))
            toolbarChangeListener.setToolbarVisibility(true)
        }
        if(context is VehicleDetailsInteractor){
            vehicleDetailsInteractor = context
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accessToken = it.getString(Constants.KEY_ACCESS_TOKEN)
            cityId = it.getString(ARGS_CITY_ID)
            vehicleType = it.getString(ARGS_VEHICLE_TYPE)
            userName = it.getString(ARGS_USER_NAME)
            isEditMode = it.getBoolean(ARGS_EDIT_MODE)
            if(it.containsKey(ARGS_VEHICLE_DETAIL)){
                val vehicleDetails = it.getParcelable(ARGS_VEHICLE_DETAIL) as VehicleDetailsLogin
                vehicleDetails.run {
                    if(modelId!=null && !vehicleMake.isNullOrEmpty()  && !vehicleModel.isNullOrEmpty() ){
                        currentModelSelected = VehicleModelDetails(vehicleDetails.vehicleMake!!,vehicleDetails.vehicleModel!!,vehicleDetails.modelId!!)
                    }
                    if(!color.isNullOrEmpty() && colorID!=null){
                        currentColorSelected = VehicleModelCustomisationDetails(color!!,colorID!!)

                    }
                    if(!doors.isNullOrEmpty() && doorId!=null){
                        currentDoorSelected = VehicleModelCustomisationDetails(doors!!,doorId!!)

                    }
                    if(!seatbelts.isNullOrEmpty() && seatBeltId!=null){
                        currentSeatBeltSelected = VehicleModelCustomisationDetails(seatbelts!!,seatBeltId!!)

                    }

                }


                year = vehicleDetails.year
                vehicleNumber = vehicleDetails.vehicleNumber

            }
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
        edtModel.setOnClickListener{ showModelDialogIfPossible() }
        edtColor.setOnClickListener{if (currentModelSelected!=null){
            showSelectionDialog(vehicleColorInteractionListener,colorSelectionListener,VEHICLE_COLOR_DIALOG_FRAGMENT_TAG,getString(R.string.select_color))
        }else{
            Toast.makeText(requireContext(),getString(R.string.invalid_model),Toast.LENGTH_SHORT).show();
        }}
        edtDoor.setOnClickListener{if (currentModelSelected!=null){
            showSelectionDialog(doorsInteractionListener,doorSelectionListener,VEHICLE_COLOR_DIALOG_FRAGMENT_TAG,getString(R.string.select_doors))
        }else{
            Toast.makeText(requireContext(),getString(R.string.invalid_model),Toast.LENGTH_SHORT).show();
        }}
        edtSeatBelt.setOnClickListener{if (currentModelSelected!=null){
            showSelectionDialog(seatBeltInteractionListener,seatBeltSelectionListener,VEHICLE_COLOR_DIALOG_FRAGMENT_TAG,getString(R.string.select_seatbelts))
        }else{
            Toast.makeText(requireContext(),getString(R.string.invalid_model),Toast.LENGTH_SHORT).show();
        }}
        edtYear.addTextChangedListener(yearWatcher);
        btn_continue.setOnClickListener{
            submitVehicleDetails()
        }

        //if prefilled details this will set data
        edtYear.setText(year)
        edtVehicleNumber.setText(vehicleNumber)

        getVehicleDetails();
    }

    private fun showModelDialogIfPossible() {
        if (currentMakeSelected != null) {
            showSelectionDialog(vehicleModelInteractionListener, modelSelectionListener, VEHICLE_MODEL_DIALOG_FRAGMENT_TAG, getString(R.string.select_model))
        } else {
            Toast.makeText(requireContext(), getString(R.string.invalid_make), Toast.LENGTH_SHORT).show();
        }
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
                        currentModelSelected?.run {
                            currentMakeSelected = VehicleMakeInfo(make)
                            edtMake.setText(make)
                            getModelDetails(this)
                        }
                    }

                    override fun onError(t: VehicleDetailsResponse?, message: String?, flag: Int): Boolean {
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

                        colorCustomisationList = t!!.customisationList.colorCustomisationList
                        doorsCustomisationList =  t.customisationList.doorCustomisationList
                        seatBeltCustomisationList =  t.customisationList.seatBeltsCustomisationList

                        vehicleColorInteractionListener.list = colorCustomisationList
                        doorsInteractionListener.list = doorsCustomisationList
                        seatBeltInteractionListener.list = seatBeltCustomisationList


                        if(currentColorSelected==null){
                            colorCustomisationList?.run {
                                if(size>0){
                                    currentColorSelected = colorCustomisationList!![0]
                                }
                            }
                        }

                        currentColorSelected?.run {
                            edtColor.setText(value)
                        }


                        if(currentDoorSelected==null){
                            doorsCustomisationList?.run {
                                if(size>0){
                                    currentDoorSelected = doorsCustomisationList!![0]
                                }
                            }

                        }
                        currentDoorSelected?.run {
                            edtDoor.setText(value)
                        }

                        if(currentSeatBeltSelected==null){
                            seatBeltCustomisationList?.run {
                                if(size>0){
                                    currentSeatBeltSelected = seatBeltCustomisationList!![0]
                                }
                            }
                        }
                        currentSeatBeltSelected?.run {
                            edtSeatBelt.setText(value)
                        }



                        vehicleDetailsGroup.visible()
                        btn_continue.isEnabled=true

                        edtYear.requestFocus();



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
                if(currentModelSelected!=null){
                    currentModelSelected=null;
                    edtModel.setText(null)
                    currentColorSelected=null
                    currentDoorSelected=null
                    currentSeatBeltSelected=null
                    edtColor.setText(null)
                    edtDoor.setText(null)
                    edtSeatBelt.setText(null)
                    edtYear.setText(null)
                    vehicleDetailsGroup.gone()
                    btn_continue.isEnabled=true
                }
                 showModelDialogIfPossible();
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

    val vehicleColorInteractionListener  = CustomisationInteractorListener(colorCustomisationList)
    val seatBeltInteractionListener  = CustomisationInteractorListener(seatBeltCustomisationList)
    val doorsInteractionListener  = CustomisationInteractorListener(doorsCustomisationList)

    val colorSelectionListener = object : OnCountryPickerListener<VehicleModelCustomisationDetails>{
        override fun onSelectCountry(country: VehicleModelCustomisationDetails) {
            this@VehicleDetailsFragment.currentColorSelected = country
            edtColor.setText(country.value)
        }
    }

    val doorSelectionListener = object : OnCountryPickerListener<VehicleModelCustomisationDetails>{
        override fun onSelectCountry(country: VehicleModelCustomisationDetails) {
            this@VehicleDetailsFragment.currentDoorSelected = country
            edtDoor.setText(country.value)
        }
    }

    val seatBeltSelectionListener = object : OnCountryPickerListener<VehicleModelCustomisationDetails>{
        override fun onSelectCountry(country: VehicleModelCustomisationDetails) {
            this@VehicleDetailsFragment.currentSeatBeltSelected = country
            edtSeatBelt.setText(country.value)
        }
    }


    fun submitVehicleDetails(){



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

        if(currentColorSelected==null)
        {
            Toast.makeText(requireContext(),getString(R.string.invalid_color),Toast.LENGTH_SHORT).show();
            return;
        }


        if(currentDoorSelected==null)
        {
            Toast.makeText(requireContext(),getString(R.string.invalid_doors),Toast.LENGTH_SHORT).show();
            return;
        }

        if(currentSeatBeltSelected==null)
        {
            Toast.makeText(requireContext(),getString(R.string.invalid_seat_belts),Toast.LENGTH_SHORT).show();
            return;
        }

        val year = edtYear.text.toString().trim();
        if(!isYearValid(year))
        {
            Toast.makeText(requireContext(),getString(R.string.invalid_year_error,minYear,calendar.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            return;
        }

        val vehicleNumber = edtVehicleNumber.text.toString().trim()
        if(vehicleNumber.isEmpty()){

            Toast.makeText(requireContext(),getString(R.string.invalid_vehicle_number),Toast.LENGTH_SHORT).show();
            return;
        }


        val customisationData = JSONObject();
        customisationData.put("door_id",currentDoorSelected!!.id)
        customisationData.put("seat_belt_id",currentSeatBeltSelected!!.id)
        customisationData.put("color_id",currentColorSelected!!.id)
        customisationData.put("model_id",currentModelSelected!!.id)

        val params = hashMapOf(
                Constants.KEY_ACCESS_TOKEN to accessToken,
                "user_name" to userName,
                "latitude" to "" + Data.latitude,
                "longitude" to "" + Data.longitude,
                "city" to ""+cityId,
                "offering_type" to "" + 1,
                "vehicle_status" to getString(R.string.owned),
                "device_type" to Data.DEVICE_TYPE,
                "device_name" to Data.deviceName,
                "app_version" to "" + Data.appVersion,
                "os_version" to Data.osVersion,
                "country" to Data.country,
                "client_id" to Data.CLIENT_ID,
                "login_type" to Data.LOGIN_TYPE,
                "referral_code" to "",
                "device_token" to Data.deviceToken,
                "unique_device_id" to Data.uniqueDeviceId,
                "device_rooted" to if (Utils.isDeviceRooted()) "1" else "0",
                //vehicle model specific details
                "vehicle_no" to vehicleNumber,
                "vehicle_details" to customisationData.toString(),
                "vehicle_type" to vehicleType,
                "vehicle_year" to ""+year)



        ApiCommonKt<FeedCommonResponseKotlin>(requireActivity()).execute(params,ApiName.REGISTER_DRIVER,object : APICommonCallbackKotlin<FeedCommonResponseKotlin>(){
            override fun onSuccess(t: FeedCommonResponseKotlin?, message: String?, flag: Int) {

                if(t!=null){
                    when (t.flag) {
                        ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal(), ApiResponseFlags.ACTION_COMPLETE.getOrdinal() -> {
                            if(isEditMode){
                                val  vehicleDetailsLogin = VehicleDetailsLogin(vehicleNumber,year,
                                        currentModelSelected!!.make ,currentModelSelected!!.modelName,currentModelSelected!!.id,
                                        currentColorSelected!!.value,currentColorSelected!!.id,
                                        currentDoorSelected!!.value,currentDoorSelected!!.id,
                                        currentSeatBeltSelected!!.value,currentSeatBeltSelected!!.id)

                                vehicleDetailsInteractor?.onDetailsUpdated(vehicleDetailsLogin)

                            }else{
                                openDocumentUploadActivity()
                            }
                        }


                       ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal(), ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() -> {
                            DialogPopup.alertPopupWithListener(activity, "", message) {

                                if(requireActivity() is DriverSplashActivity){
                                    (requireActivity() as DriverSplashActivity).openPhoneLoginScreen()
                                    (requireActivity() as DriverSplashActivity).setToolbarVisibility(false)
                                }

                            }

                        }
                        else -> DialogPopup.alertPopup(requireActivity(), "", message)
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

    class CustomisationInteractorListener(var list:  List<VehicleModelCustomisationDetails>?):
            CountryPickerDialog.CountryPickerDialogInteractionListener<VehicleModelCustomisationDetails>{

       ;
        override fun getAllCountries(): List<VehicleModelCustomisationDetails>? {
            return  list;
        }

        override fun sortCountries(searchResults: List<VehicleModelCustomisationDetails>?) {

        }

        override fun canSearch(): Boolean {
           return  list!=null && list!!.size>7
        }

    }

}