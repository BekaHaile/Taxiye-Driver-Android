package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.retrofit.model.FetchChatResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;

/**
 * Created by aneesh on 10/4/15.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.infoTileViewHolder> {

	private ArrayList<FetchChatResponse.ChatHistory> chatHistories;
	private Context context;
	private FetchChatResponse fetchChatResponse;

	public ChatAdapter(Context context, ArrayList<FetchChatResponse.ChatHistory> chatHistories) {
		this.chatHistories = chatHistories;
		this.context = context;
	}

	@Override
	public int getItemCount() {
		return chatHistories.size();
	}

	@Override
	public void onBindViewHolder(infoTileViewHolder infoTileViewHolder, int i) {
		final FetchChatResponse.ChatHistory itr = chatHistories.get(i);

		infoTileViewHolder.name.setTypeface(Data.latoRegular(context));
		infoTileViewHolder.name.setText(itr.getMessage());

	}

	@Override
	public infoTileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.
				from(viewGroup.getContext()).
				inflate(R.layout.list_item_help, viewGroup, false);

		return new infoTileViewHolder(itemView);
	}


	public class infoTileViewHolder extends RecyclerView.ViewHolder {
		protected LinearLayout relative;
		protected TextView name;
		protected int id;
		public infoTileViewHolder(View v) {
			super(v);
			relative = (LinearLayout)v.findViewById(R.id.relative);
			name = (TextView) v.findViewById(R.id.name);
			name.setTypeface(Data.latoRegular(context));
			relative.setLayoutParams(new RecyclerView.LayoutParams(690, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(relative);
		}
	}

}