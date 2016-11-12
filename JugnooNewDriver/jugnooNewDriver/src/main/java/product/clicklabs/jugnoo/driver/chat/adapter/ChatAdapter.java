package product.clicklabs.jugnoo.driver.chat.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.retrofit.model.FetchChatResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;


/**
 * Created by aneesh on 10/4/15.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private ArrayList<FetchChatResponse.ChatHistory> chatHistories;
	private Context context;

	public ChatAdapter(Context context, ArrayList<FetchChatResponse.ChatHistory> chatHistories) {
		this.chatHistories = chatHistories;
		this.context = context;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item, viewGroup, false);

		RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
		//RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT);
		itemView.setLayoutParams(layoutParams);
		ASSL.DoMagic(itemView);


		return new ChatViewHolder(itemView, context);
	}

	@Override
	public int getItemCount() {
		return chatHistories == null ? 0 : chatHistories.size();
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if(holder instanceof ChatViewHolder) {
			final ChatViewHolder chatViewHolder = ((ChatViewHolder)holder);
			FetchChatResponse.ChatHistory chatHistory = chatHistories.get(position);

			if (chatHistory.getIsSender() == 1) {
				chatViewHolder.layoutYou.setVisibility(View.GONE);
				chatViewHolder.layoutMe.setVisibility(View.VISIBLE);

				chatViewHolder.chatTextMe.setText(chatHistory.getMessage());
				chatViewHolder.chatTimeMe.setText(chatHistory.getCreatedAt());
			/*Picasso.with(context).
					load(chat.getImage()).
					transform(new CircleTransform()).
					fit().into(holder.userIconMe);*/
			} else {
				chatViewHolder.layoutYou.setVisibility(View.VISIBLE);
				chatViewHolder.layoutMe.setVisibility(View.GONE);

				chatViewHolder.chatTextYou.setText(chatHistory.getMessage());
				chatViewHolder.chatTimeYou.setText(chatHistory.getCreatedAt());
				if(!"".equalsIgnoreCase(Data.userData.userImage)) {
					float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
					Picasso.with(context).
							load(Data.userData.userImage)
							.placeholder(R.drawable.ring)
							.transform(new CircleTransform())
							.resize((int) (130f * minRatio), (int) (130f * minRatio)).centerCrop()
							.into(chatViewHolder.userIconYou);
				}
			}
		}
	}

	static class ChatViewHolder extends RecyclerView.ViewHolder {
		public TextView chatTextYou, chatTimeYou, chatTextMe, chatTimeMe;
		public ImageView userIconMe, userIconYou;
		public RelativeLayout layoutMe, layoutYou, relative;

		public ChatViewHolder(View itemView, Context context) {
			super(itemView);

			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			chatTextYou = (TextView) itemView.findViewById(R.id.chat_txt_you);chatTextYou.setTypeface(Data.latoRegular(context));
			chatTimeYou = (TextView) itemView.findViewById(R.id.chat_time_you);chatTimeYou.setTypeface(Data.latoRegular(context));
			userIconYou = (ImageView) itemView.findViewById(R.id.usr_icon_you);
			layoutYou = (RelativeLayout) itemView.findViewById(R.id.layout_you);

			chatTextMe = (TextView) itemView.findViewById(R.id.chat_txt_me);chatTextMe.setTypeface(Data.latoRegular(context));
			chatTimeMe = (TextView) itemView.findViewById(R.id.chat_time_me);chatTimeMe.setTypeface(Data.latoRegular(context));
			userIconMe = (ImageView) itemView.findViewById(R.id.usr_icon_me);
			layoutMe = (RelativeLayout) itemView.findViewById(R.id.layout_me);

			//ASSL.DoMagic(relative);
		}
	}

}