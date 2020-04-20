package product.clicklabs.jugnoo.driver.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_program_details.*
import product.clicklabs.jugnoo.driver.IncentiveActivity
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.ui.models.ProgramModel
import product.clicklabs.jugnoo.driver.utils.DateOperations
import product.clicklabs.jugnoo.driver.utils.inflate

class ProgramDetailFragment : Fragment() {
    var totalRides = 0
    val thresholdAdapter=ThresholdAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_program_details)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            setValues((activity as IncentiveActivity).programData)
    }

    fun setValues(programData: ProgramModel.Data) {
        tvDates.text =getString(R.string.program_dur)+(DateOperations.convertMonthDayViaFormat(DateOperations.utcToLocalTZ(programData.startDate))+"-"+ DateOperations.convertMonthDayViaFormat(DateOperations.utcToLocalTZ(programData.endDate)))
        tvTodayDate.text = DateOperations.getTodayDate()
        tvProgramName.text = programData.name
        tvDescription.text = programData.description
        tvCompletedRides.text = programData.completedRides.toString() + " "+getString(R.string.rides)
//        pbRides
        rvThresholds.layoutManager = LinearLayoutManager(activity)
        rvThresholds.adapter =thresholdAdapter
        thresholdAdapter.setList(programData.threshold)
        setExpiredVisibility(programData.programType==0)
        if (programData.threshold.size > 0) {

            for (i in 0 until programData.threshold.size) {
                if (totalRides < programData.threshold[i].value) {
                    totalRides=programData.threshold[i].value
                }
            }
            pbRides.totalProgress = totalRides
            pbRides.markers = programData.threshold.map { it -> it.value }.toMutableList()
        }
        pbRides.currentProgress = programData.completedRides
//        val displayMetrics = DisplayMetrics()
//        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
//        var i=displayMetrics?.widthPixels
//        if (i != null) {
//            pbRides.progressBarWidth= (i-100).toFloat()
//        }



    }
    fun setExpiredVisibility(isExpired : Boolean){
        if(isExpired){
            tvTodayDate.visibility=View.GONE
            tvCompleted.visibility=View.GONE
            tvCompletedRides.visibility=View.GONE
            pbRides.visibility=View.GONE
            vwBottom.visibility=View.GONE
        }
        else{
            tvTodayDate.visibility=View.VISIBLE
            tvCompleted.visibility=View.VISIBLE
            tvCompletedRides.visibility=View.VISIBLE
            pbRides.visibility=View.VISIBLE
            vwBottom.visibility=View.VISIBLE
        }
    }

    class ThresholdAdapter() : RecyclerView.Adapter<ThresholdAdapter.ProgramViewHolder>() {
        private var details: ArrayList<ProgramModel.Thresholds>? = null
        public fun setList(details: ArrayList<ProgramModel.Thresholds>?) {
            this.details = details
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThresholdAdapter.ProgramViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_threshold, parent, false)
            return ProgramViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return if (details == null) 0 else details!!.size;
        }

        override fun onBindViewHolder(holder: ThresholdAdapter.ProgramViewHolder, position: Int) {
            holder.tvThresholdValue.text = details!![position].value.toString()
            holder.tvThresholdPrice.text = details!![position].prize.toString()
        }


        inner class ProgramViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvThresholdValue: TextView = view.findViewById(R.id.tvThresholdValue) as TextView
            val tvThresholdPrice: TextView = view.findViewById(R.id.tvThresholdPrice) as TextView
        }
    }

    //ic_program_info
    //params compile line

}