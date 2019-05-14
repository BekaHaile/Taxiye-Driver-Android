package product.clicklabs.jugnoo.driver.heremaps.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_here_maps_feedback.*
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.heremaps.adapter.HereMapsCategoryAdapter
import product.clicklabs.jugnoo.driver.heremaps.model.HMCategory
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt
import product.clicklabs.jugnoo.driver.utils.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import retrofit.mime.TypedFile
import java.io.File
import java.util.*


class HereMapsFeedbackActivity  : BaseFragmentActivity(), HereMapsCategoryAdapter.Callback {

    lateinit var categoryAdapter: HereMapsCategoryAdapter
    var categorySelected: HMCategory? = null
    var fileSelected:File? = null
    var latitude:Double? = 0.0
    var longitude:Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_here_maps_feedback)

        val tvTitle: TextView = findViewById(R.id.title)
        tvTitle.text = getString(R.string.add_a_place)
        tvTitle.typeface = Fonts.mavenRegular(this)
        val backBtn = findViewById<View>(R.id.backBtn)
        backBtn.setOnClickListener { onBackPressed() }


        fileSelected = File(intent.getStringExtra(Constants.KEY_FILE_SELECTED))
        latitude = intent.getDoubleExtra(Constants.KEY_LATITUDE, 0.0)
        longitude = intent.getDoubleExtra(Constants.KEY_LONGITUDE, 0.0)

        Picasso.with(this).load(fileSelected).into(ivReviewImage)

        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        categoryAdapter = HereMapsCategoryAdapter(this, rvCategories, this)
        rvCategories.adapter = categoryAdapter

        bSubmit.setOnClickListener{
            if(fileSelected == null){
                Utils.showToast(this, getString(R.string.image_not_captured))
                return@setOnClickListener
            }
            if(categorySelected == null){
                Utils.showToast(this, getString(R.string.please_select_a_category))
                return@setOnClickListener
            }
            uploadPicToServer(this, fileSelected!!, categorySelected!!)
        }


        setCategoriesAdapter()


    }

    private fun setCategoriesAdapter() {
        val categoriesList = mutableListOf<HMCategory>()
        categoriesList.add(HMCategory(4581, "Airport"))
        categoriesList.add(HMCategory(7996, "Amusement Park"))
        categoriesList.add(HMCategory(3578, "ATM"))
        categoriesList.add(HMCategory(5512, "Auto Dealership Used Cars"))
        categoriesList.add(HMCategory(5511, "Automobile Dealerships"))
        categoriesList.add(HMCategory(7538, "Auto Service & Maintenance"))
        categoriesList.add(HMCategory(8699, "Automobile Club"))
        categoriesList.add(HMCategory(6000, "Bank"))
        categoriesList.add(HMCategory(9532, "Bar or Pub"))
        categoriesList.add(HMCategory(9995, "Book Store"))
        categoriesList.add(HMCategory(9999, "Border Crossing"))
        categoriesList.add(HMCategory(7933, "Bowling Centre"))
        categoriesList.add(HMCategory(4170, "Bus Station"))
        categoriesList.add(HMCategory(5000, "Business Facility"))
        categoriesList.add(HMCategory(9517, "Campground"))
        categoriesList.add(HMCategory(9714, "Cargo Centre"))
        categoriesList.add(HMCategory(7985, "Casino"))
        categoriesList.add(HMCategory(9591, "Cemetry"))
        categoriesList.add(HMCategory(7832, "Cinema"))
        categoriesList.add(HMCategory(9121, "City Hall"))
        categoriesList.add(HMCategory(7994, "Civic/Community Centre"))
        categoriesList.add(HMCategory(9537, "Clothing Store"))
        categoriesList.add(HMCategory(9996, "Coffee Shop"))
        categoriesList.add(HMCategory(4100, "Commuter Rail Station"))
        categoriesList.add(HMCategory(9987, "Consumer Electronic Store"))
        categoriesList.add(HMCategory(9535, "Convenience Store"))
        categoriesList.add(HMCategory(7990, "Convention/Exhibition Centre"))
        categoriesList.add(HMCategory(9994, "County Council"))
        categoriesList.add(HMCategory(9211, "Court House"))
        categoriesList.add(HMCategory(9545, "Department Store"))
        categoriesList.add(HMCategory(9993, "Embassy"))
        categoriesList.add(HMCategory(1111, "Extended Listing"))
        categoriesList.add(HMCategory(4482, "Ferry Terminal"))
        categoriesList.add(HMCategory(7992, "Golf Course"))
        categoriesList.add(HMCategory(9525, "Government Office"))
        categoriesList.add(HMCategory(5400, "Grocery Store"))
        categoriesList.add(HMCategory(8200, "Higher Education"))
        categoriesList.add(HMCategory(9592, "Highway Exit"))
        categoriesList.add(HMCategory(5999, "Historical Monument"))
        categoriesList.add(HMCategory(9986, "Home Improvement and Hardware"))
        categoriesList.add(HMCategory(9560, "Home Specialty Store"))
        categoriesList.add(HMCategory(8060, "Hospital"))
        categoriesList.add(HMCategory(7011, "Hotel"))
        categoriesList.add(HMCategory(7998, "Ice Skating Rink"))
        categoriesList.add(HMCategory(9991, "Industrial Zone"))
        categoriesList.add(HMCategory(8231, "Library"))
        categoriesList.add(HMCategory(4493, "Marina"))
        categoriesList.add(HMCategory(9583, "Medical Service"))
        categoriesList.add(HMCategory(9715, "Military Base"))
        categoriesList.add(HMCategory(5571, "Motorcycle Dealership"))
        categoriesList.add(HMCategory(8410, "Museum"))
        categoriesList.add(HMCategory(4444, "City Centre / Named Place"))
        categoriesList.add(HMCategory(9709, "Neighbourhood"))
        categoriesList.add(HMCategory(5813, "Nightlife"))
        categoriesList.add(HMCategory(9988, "Office Supply Service Store"))
        categoriesList.add(HMCategory(7013, "Other Accomodation"))
        categoriesList.add(HMCategory(7947, "Park/Recreation Area"))
        categoriesList.add(HMCategory(7522, "Park & Ride"))
        categoriesList.add(HMCategory(7521, "Parking Garage/House"))
        categoriesList.add(HMCategory(7520, "Parking Lot"))
        categoriesList.add(HMCategory(7929, "Performing Arts"))
        categoriesList.add(HMCategory(5540, "Petrol/Gasoline Station"))
        categoriesList.add(HMCategory(9565, "Pharmacy"))
        categoriesList.add(HMCategory(9992, "Place of Worship"))
        categoriesList.add(HMCategory(9221, "Police Station"))
        categoriesList.add(HMCategory(9530, "Post Office"))
        categoriesList.add(HMCategory(9589, "Public Restroom"))
        categoriesList.add(HMCategory(4580, "Public Sports Airport"))
        categoriesList.add(HMCategory(9708, "Public Transit Access"))
        categoriesList.add(HMCategory(9707, "Public Transit Stop"))
        categoriesList.add(HMCategory(7510, "Rental Car Agency"))
        categoriesList.add(HMCategory(9590, "Residential Area Building"))
        categoriesList.add(HMCategory(7897, "Rest Area"))
        categoriesList.add(HMCategory(5800, "Restaurant"))
        categoriesList.add(HMCategory(8211, "School"))
        categoriesList.add(HMCategory(6512, "Shopping"))
        categoriesList.add(HMCategory(7012, "Ski Resort"))
        categoriesList.add(HMCategory(9567, "Specialty Store"))
        categoriesList.add(HMCategory(9568, "Sporting Goods Store"))
        categoriesList.add(HMCategory(7997, "Sports Centre"))
        categoriesList.add(HMCategory(7940, "Sports Complex"))
        categoriesList.add(HMCategory(9989, "Taxi Stand"))
        categoriesList.add(HMCategory(7999, "Tourist Attraction"))
        categoriesList.add(HMCategory(7389, "Tourist Information"))
        categoriesList.add(HMCategory(4013, "Train Station"))
        categoriesList.add(HMCategory(9593, "Transportation Service"))
        categoriesList.add(HMCategory(2084, "Winery"))
        categoriesList.add(HMCategory(9719, "Truck Dealership"))
        categoriesList.add(HMCategory(9710, "Weigh Station"))
        categoriesList.add(HMCategory(9522, "Truck Stop/Plaza"))
        categoriesList.add(HMCategory(9718, "Animal Park"))
        categoriesList.add(HMCategory(9500, "Business Service"))
        categoriesList.add(HMCategory(9501, "Other Communication"))
        categoriesList.add(HMCategory(9502, "Telephone Service"))
        categoriesList.add(HMCategory(9503, "Cleaning Laundry"))
        categoriesList.add(HMCategory(9504, "Hair Beauty"))
        categoriesList.add(HMCategory(9506, "Mover"))
        categoriesList.add(HMCategory(9507, "Photography Shopping"))
        categoriesList.add(HMCategory(9508, "Video Game Rental"))
        categoriesList.add(HMCategory(9509, "Storage"))
        categoriesList.add(HMCategory(9510, "Tailor Alteration"))
        categoriesList.add(HMCategory(9511, "Tax Service"))
        categoriesList.add(HMCategory(9512, "Repair Service"))
        categoriesList.add(HMCategory(9513, "Retirement Nursing Home"))
        categoriesList.add(HMCategory(9514, "Social Service"))
        categoriesList.add(HMCategory(9515, "Utilities"))
        categoriesList.add(HMCategory(9516, "Waste Sanitary"))
        categoriesList.add(HMCategory(9518, "Auto Parts"))
        categoriesList.add(HMCategory(9519, "Car Wash Detailing"))
        categoriesList.add(HMCategory(9520, "Local Transit"))
        categoriesList.add(HMCategory(9521, "Travel Agent Ticketing"))
        categoriesList.add(HMCategory(9523, "Church"))
        categoriesList.add(HMCategory(9524, "Synagogue"))
        categoriesList.add(HMCategory(9527, "Fire Department"))
        categoriesList.add(HMCategory(9528, "Road Assistance"))
        categoriesList.add(HMCategory(9529, "Funeral Director"))
        categoriesList.add(HMCategory(9531, "Banquet Hall"))
        categoriesList.add(HMCategory(9533, "Cocktail Lounge"))
        categoriesList.add(HMCategory(9534, "Night Club"))
        categoriesList.add(HMCategory(9536, "Specialty Food Store"))
        categoriesList.add(HMCategory(9538, "Mens Apparel"))
        categoriesList.add(HMCategory(9539, "Shoe Store"))
        categoriesList.add(HMCategory(9540, "Specialty Clothing Store"))
        categoriesList.add(HMCategory(9541, "Women Apparel"))
        categoriesList.add(HMCategory(9542, "Check Cashing Service"))
        categoriesList.add(HMCategory(9544, "Money Transferring Service"))
        categoriesList.add(HMCategory(9546, "Discount Store"))
        categoriesList.add(HMCategory(9547, "Other General Merchandise"))
        categoriesList.add(HMCategory(9548, "Variety Store"))
        categoriesList.add(HMCategory(9549, "Garden Center"))
        categoriesList.add(HMCategory(9550, "Glass/Window Store"))
        categoriesList.add(HMCategory(9551, "Hardware Store"))
        categoriesList.add(HMCategory(9552, "Home Centre"))
        categoriesList.add(HMCategory(9553, "Lumber"))
        categoriesList.add(HMCategory(9554, "Other House/Garden Store"))
        categoriesList.add(HMCategory(9555, "Paint Store"))
        categoriesList.add(HMCategory(9556, "Entertainment Electronics"))
        categoriesList.add(HMCategory(9557, "Floor/Carpet Store"))
        categoriesList.add(HMCategory(9558, "Furniture Store"))
        categoriesList.add(HMCategory(9559, "Major Appliance Store"))
        categoriesList.add(HMCategory(9561, "Computer Software"))
        categoriesList.add(HMCategory(9562, "Flowers Jewelry"))
        categoriesList.add(HMCategory(9563, "Gift/Antique/Art"))
        categoriesList.add(HMCategory(9564, "Optical"))
        categoriesList.add(HMCategory(9566, "Record/CD/Video"))
        categoriesList.add(HMCategory(9569, "Wine Liquor"))
        categoriesList.add(HMCategory(9570, "Boating"))
        categoriesList.add(HMCategory(9572, "Race Track"))
        categoriesList.add(HMCategory(9573, "Golf Practice Range"))
        categoriesList.add(HMCategory(9574, "Health Club"))
        categoriesList.add(HMCategory(9575, "Bowling Alley"))
        categoriesList.add(HMCategory(9576, "Sports Activities"))
        categoriesList.add(HMCategory(9577, "Recreation Center"))
        categoriesList.add(HMCategory(9578, "Attorney"))
        categoriesList.add(HMCategory(9579, "Dentist"))
        categoriesList.add(HMCategory(9580, "Physican"))
        categoriesList.add(HMCategory(9581, "Realtor"))
        categoriesList.add(HMCategory(9582, "Caravan Park"))
        categoriesList.add(HMCategory(9584, "Police Service"))
        categoriesList.add(HMCategory(9585, "Veterinarian"))
        categoriesList.add(HMCategory(9586, "Sporting Instructional Camps"))
        categoriesList.add(HMCategory(9587, "Agricultural Product Market"))
        categoriesList.add(HMCategory(-1001, "Mosque"))
        categoriesList.add(HMCategory(-1002, "Temple"))
        categoriesList.add(HMCategory(-1003, "Ashram"))
        categoriesList.add(HMCategory(-2001, "Truck Loading Dock"))

        categoryAdapter.setList(categoriesList)
    }

    override fun onCategoryClick(pos: Int, category: HMCategory) {
        categorySelected = category
    }

    private fun uploadPicToServer(activity: Activity?, photoFile: File, category: HMCategory) {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                DialogPopup.showLoadingDialog(activity, resources.getString(R.string.loading))
                val params = HashMap<String, String>()
                val placeType = category.placeType
                params["feedback"] = "{\"coordinates\":[$longitude, $latitude, 0],\"type\":\"Point\"" +
                        ",\"properties\":{\"appId\":\"here-mapcreator\",\"error\": 30,\"v\":\"2.7\",\"type\":\"$placeType\"}}"

                val typedFile = TypedFile(Constants.MIME_TYPE, photoFile)

                RestClient.getHereMapsApiService().feedback(
                        "here_app", "Y8pzxL8Y52FBp0qtaYVO", "Am5XvFppRInSeFpAtijKeg",
                        typedFile, params, object : Callback<SettleUserDebt> {
                    override fun success(docRequirementResponse: SettleUserDebt, response: Response) {
                        try {
                            val jsonString = String((response.body as TypedByteArray).bytes)
                            val jObj: JSONObject
                            jObj = JSONObject(jsonString)
                            Log.e("uploadPicToServer", "resp = $jObj")

                        } catch (exception: Exception) {
                            exception.printStackTrace()
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG)
                            DialogPopup.dismissLoadingDialog()
                        }

                        DialogPopup.dismissLoadingDialog()

                    }

                    override fun failure(error: RetrofitError) {
                        DialogPopup.dismissLoadingDialog()
                    }
                })
            } else {
                DialogPopup.alertPopup(activity, "", resources.getString(R.string.check_internet_message))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}