package product.clicklabs.jugnoo.driver.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import params.com.stepprogressview.StepProgressView
import product.clicklabs.jugnoo.driver.IncentiveActivity
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.ui.models.ProgramModel
import product.clicklabs.jugnoo.driver.utils.DateOperations
import java.util.*

class ProgramsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var details: ArrayList<Any>? = null
    lateinit var activity: IncentiveActivity

    val TYPE_HEADER = 1
    val TYPE_ONGOING = 2

    public fun setList(details: ArrayList<Any>?, activity: IncentiveActivity) {
        if (this.details == null) {
            this.details = arrayListOf()
        }
        this.details!!.clear()
        if (details != null && details.size > 0) {
            var currentAdded = false
            var expiredAdded = false
            for (any: Any in details.iterator()) {
                if (any is ProgramModel.Data) {
                    if (!currentAdded && any.programType == 1) {
                        this.details!!.add(HeaderData(activity.getString(R.string.current_program)))
                        currentAdded = true
                    }
                    if (!expiredAdded && any.programType == 0) {
                        this.details!!.add(HeaderData(activity.getString(R.string.expired_programs)))
                        expiredAdded = true
                    }
                    this.details!!.add(any)
                }
            }
            this.activity = activity
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_program_header, parent, false)
            return HeaderViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_program, parent, false)
            return ProgramViewHolder(itemView)
        }
    }

    override fun getItemCount(): Int {
        return if (details == null) 0 else details!!.size;
    }

    override fun getItemViewType(position: Int): Int {
        if (details!![position] is HeaderData) {
            return TYPE_HEADER
        } else {
            return TYPE_ONGOING
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProgramViewHolder) {
            val data: ProgramModel.Data = details!![position] as ProgramModel.Data
            holder.tvProgramName.text = (data.name)
            holder.tvCompletedRides.text = (data.completedRides).toString()
            if (details!!.size > 0 && position == (details!!.size) - 1) {
                holder.vwBottom.visibility = View.INVISIBLE
            } else {
                holder.vwBottom.visibility = View.VISIBLE
            }
            holder.ivProgramDetails.tag=position
            holder.ivProgramDetails.setOnClickListener {
                activity.openProgramDetailsFragment(data)
            }
            if (data.programType == 1) {
                holder.tvDates.text = DateOperations.getTodayDate()
                holder.pbRides.visibility = View.VISIBLE
            } else {
                holder.tvDates.text = activity.getString(R.string.program_dur)+(DateOperations.convertMonthDayViaFormat(DateOperations.utcToLocalTZ(data.startDate))+"-"+DateOperations.convertMonthDayViaFormat(DateOperations.utcToLocalTZ(data.endDate)))
                holder.pbRides.visibility = View.GONE
            }
            holder.pbRides.currentProgress = data.completedRides
//            holder.pbRides.currentProgress = if (position == 1) 25 else data.completedRides
            if (data.threshold.size > 0) {
                var totalRides = 0
                for (i in 0 until data.threshold.size) {
                    if (totalRides < data.threshold[i].value) {
                        totalRides=data.threshold[i].value
                    }
                }
                holder.pbRides.totalProgress = totalRides
                Collections.sort(data.threshold, object : Comparator<ProgramModel.Thresholds> {
                    override fun compare(lhs: ProgramModel.Thresholds, rhs: ProgramModel.Thresholds): Int {

                        return (lhs.value).compareTo(rhs.value)
                    }
                })

                holder.pbRides.markers = data.threshold.map { it -> it.value }.toMutableList()
            }

//            val displayMetrics = DisplayMetrics()
//            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
//            var i=displayMetrics?.widthPixels
//            holder.pbRides.progressBarWidth=(i-150).toFloat()
        } else if (holder is HeaderViewHolder) {
            val data: HeaderData = details!![position] as HeaderData
            holder.tvProgramType.setText(data.text)
        }
    }


    inner class ProgramViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDates: TextView = view.findViewById(R.id.tvDates) as TextView
        val tvProgramName: TextView = view.findViewById(R.id.tvProgramName) as TextView
        val tvCompletedRides: TextView = view.findViewById(R.id.tvCompletedRides) as TextView
        val vwBottom: View = view.findViewById(R.id.vwBottom) as View
        val pbRides: StepProgressView = view.findViewById(R.id.pbRides) as StepProgressView
        val ivProgramDetails: ImageView = view.findViewById(R.id.ivProgramDetails) as ImageView
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProgramType: TextView = view.findViewById(R.id.tvProgramType)
    }

    inner class HeaderData(val text: String) {}
}