package product.clicklabs.jugnoo.driver

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_incentive.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import kotlinx.android.synthetic.main.layout_top_bar.view.*
import product.clicklabs.jugnoo.driver.adapters.ProgramsAdapter
import product.clicklabs.jugnoo.driver.fragments.ProgramDetailFragment
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.ProgramModel
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import java.util.*

class IncentiveActivity : BaseFragmentActivity() {

    val programsAdapter = ProgramsAdapter()
    var data = ArrayList<Any>()
    lateinit var programData: ProgramModel.Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incentive)
        backBtn.setOnClickListener { onBackPressed() }
        topRl.title.text=getString(R.string.incentive)
        fetchPrograms()
        //setProgramsAdapter()
    }
    fun fetchPrograms() {
        ApiCommonKt<ProgramModel>(this, putAccessToken = true).execute(params = null, apiName = ApiName.FETCH_PROGRAMS, apiCommonCallback = object : APICommonCallbackKotlin<ProgramModel>() {
            override fun onSuccess(t: ProgramModel, message: String?, flag: Int) {
                data = t.programs as ArrayList<Any>
                setProgramsAdapter()

            }

            override fun onError(t: ProgramModel?, message: String?, flag: Int): Boolean {
                return false;
            }

            override fun onDialogClick() {
                onBackPressed();
            }
        })
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            topRl.title.text =getString(R.string.incentive)
            supportFragmentManager.popBackStackImmediate()
            llProgramDetails.visibility= View.GONE
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
        } else {
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }
    }
//    fun setDummyPrograms(){
//        val thresholds = ArrayList<ProgramModel.Thresholds>()
//        for (i in 0..5) {
//            val threshold = ProgramModel.Thresholds(i.toString() + "", i + 20, (i + 4000).toDouble())
//            thresholds.add(threshold)
//        }
//
//
//        for (i in 0..5) {
//            val programs = ProgramModel.Data(i.toString(), "5 may", "10 june", if (i == 0||i == 2||i == 3) "Diwali program" else "Dushehra program", "Description: Sau 04 tuần đầu tiên hoạt động, từ tuần lễ thứ 05 trở đi, đối tá sẽ nhận được thêm một khoản thưởng vào thứ hai hàng tuần dựa trên thành tích hoạt động 04 tuần gần nhất",i + 478, thresholds, 900,if (i == 0) 1 else 0)
//            data.add(programs)
//        }
//    }
    fun setProgramsAdapter(){
       // setDummyPrograms()
        rvPrograms.layoutManager = LinearLayoutManager(this)
        rvPrograms.adapter =programsAdapter
        programsAdapter.setList(data,this)
    }
    fun openProgramDetailsFragment(programData: ProgramModel.Data){
        llProgramDetails.visibility= View.VISIBLE
        topRl.title.text =getString(R.string.details)
        val programDetailsFragment = ProgramDetailFragment()
        this.programData=programData
        loadFragment(R.id.llProgramDetails, programDetailsFragment, ProgramDetailFragment::class.java.name, true, true)
    }

}