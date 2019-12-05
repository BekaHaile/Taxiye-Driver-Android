package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverCreditResponse;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by aneesh on 10/4/15.
 */
public class DriverCreditsHistoryAdapter extends RecyclerView.Adapter<DriverCreditsHistoryAdapter.CreditHistoryViewHolder> implements ItemListener{

	private List<DriverCreditResponse.CreditHistory> deliveryDetails;
	private Context context;
	private RecyclerView recyclerView;


	public DriverCreditsHistoryAdapter(Context context, List<DriverCreditResponse.CreditHistory> deliveryDetails, RecyclerView recyclerView) {
		this.deliveryDetails = deliveryDetails;
		this.context = context;
		this.recyclerView = recyclerView;

	}

	@Override
	public int getItemCount() {
		return deliveryDetails==null?0:deliveryDetails.size();
	}

	@Override
	public void onBindViewHolder(CreditHistoryViewHolder rideInfoViewHolder, int i) {
		rideInfoViewHolder.tvDesc.setText(deliveryDetails.get(i).getDesc());
		rideInfoViewHolder.tvDate.setText(deliveryDetails.get(i).getTxnDate());
		rideInfoViewHolder.tvId.setText(context.getString(R.string.hash_format, String.valueOf(deliveryDetails.get(i).getTxnId())));
		rideInfoViewHolder.tvAmount.setText(Utils.formatCurrencyValue(deliveryDetails.get(i).getCurrencyUnit(),deliveryDetails.get(i).getAmount()));

	}

	@Override
	public CreditHistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.
				from(viewGroup.getContext()).
				inflate(R.layout.layout_item_driver_credit_history, viewGroup, false);

		return new CreditHistoryViewHolder(itemView,this);
	}

	@Override
	public void onClickItem(View parentView, View childView) {
		int position = recyclerView.getChildAdapterPosition(childView);
		CreditHistoryViewHolder rideInfoViewHolder = (CreditHistoryViewHolder) recyclerView.getChildViewHolder(parentView);
		if(position!=RecyclerView.NO_POSITION){
			switch (childView.getId()){
				case R.id.relativeLayout:


					break;
				default:
						break;
			}


		}
	}

	public class CreditHistoryViewHolder extends RecyclerView.ViewHolder {
		protected RelativeLayout relativeLayout;
		protected TextView tvDesc, tvDate,tvAmount,tvId;

		public CreditHistoryViewHolder(View v, final ItemListener itemListener) {
			super(v);
			relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
			tvId = (TextView) v.findViewById(R.id.tv_credit_history_id);
			tvAmount = (TextView) v.findViewById(R.id.tv_amount);
			tvDesc = (TextView) v.findViewById(R.id.tv_credit_history_desc);
			tvDate = (TextView) v.findViewById(R.id.tv_credit_history_date);
			relativeLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(v,relativeLayout);
				}
			});
		}
	}



}