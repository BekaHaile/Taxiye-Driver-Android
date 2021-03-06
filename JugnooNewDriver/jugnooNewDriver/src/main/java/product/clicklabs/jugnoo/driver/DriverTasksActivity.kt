package product.clicklabs.jugnoo.driver


import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_driver_tasks.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.adapters.DriverTasksAdapter
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.DriverTaskTypes
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.drivertaks.DriverTasks
import product.clicklabs.jugnoo.driver.retrofit.model.drivertaks.Tasks
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Utils
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.*


class DriverTasksActivity : BaseFragmentActivity(), DriverTasksAdapter.DriverTasksListener {
    override fun onTaskClicked(tasks: Tasks) {
        openDriverDocumentActivity(tasks)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_tasks)
        setToolBar()
        driverTaskApi()
    }

    private fun setToolBar() {
        backBtn.setOnClickListener { onBackPressed() }
        findViewById<TextView>(R.id.title).text = getString(R.string.title_tasks)
    }

    private fun driverTaskApi() {
        try {
            val params = HashMap<String, String>()
            params["access_token"] = intent.getStringExtra("access_token")
            HomeUtil.putDefaultParams(params)
            DialogPopup.showLoadingDialog(this@DriverTasksActivity, getString(R.string.loading))
            RestClient.getApiServices().fetchDriverTask(params, object : Callback<DriverTasks> {
                override fun success(driverTasks: DriverTasks, response: Response) {
                    try {
                        val jsonString = String((response.body as TypedByteArray).bytes)
                        val jObj = JSONObject(jsonString)
                        val message = JSONParser.getServerMessage(jObj)
                        val flag = jObj.getInt("flag")

                        if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                            setAdapter(driverTasks.tasks)
                        } else {
                            DialogPopup.alertPopup(this@DriverTasksActivity, "", message)
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        DialogPopup.alertPopup(this@DriverTasksActivity, "", Data.SERVER_ERROR_MSG)
                    }

                    DialogPopup.dismissLoadingDialog()
                }

                override fun failure(error: RetrofitError) {
                    DialogPopup.dismissLoadingDialog()
                    DialogPopup.alertPopup(this@DriverTasksActivity, "", Data.SERVER_NOT_RESOPNDING_MSG)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setAdapter(tasks: List<Tasks>) {
        val layoutManager = LinearLayoutManager(this@DriverTasksActivity)
        rvTasks.layoutManager = layoutManager
        rvTasks.isNestedScrollingEnabled = false
        val driverTasksAdapter = DriverTasksAdapter()
        rvTasks.adapter = driverTasksAdapter
        driverTasksAdapter.setList(tasks as ArrayList<Tasks>, Data.userData.currency)
    }


    private fun openDriverDocumentActivity(tasks: Tasks) {
        val preIntent = intent
        val intent = Intent(this@DriverTasksActivity, DriverDocumentActivity::class.java)
        intent.putExtra("access_token", preIntent.getStringExtra("access_token"))
        intent.putExtra("in_side", preIntent.getBooleanExtra("in_side", true))
        intent.putExtra("doc_required", preIntent.getIntExtra("doc_required", 0))
        intent.putExtra(Constants.BRANDING_IMAGES_ONLY, 1)
        intent.putExtra(Constants.KEY_TASK_TYPE, tasks.taskType)

        if(tasks.taskType == DriverTaskTypes.HERE_MAPS_FEEDBACK.type) {
            if (HomeActivity.myLocation != null) {
                intent.putExtra(Constants.KEY_LATITUDE, HomeActivity.myLocation.latitude)
                        .putExtra(Constants.KEY_LONGITUDE, HomeActivity.myLocation.longitude)
            } else {
                Utils.showToast(this@DriverTasksActivity, getString(R.string.waiting_for_location))
                return
            }
        }

        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}
