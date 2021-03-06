package product.clicklabs.jugnoo.driver.chat.adapter;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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
	private String image;

	public ChatAdapter(Context context, ArrayList<FetchChatResponse.ChatHistory> chatHistories, String image) {
		this.chatHistories = chatHistories;
		this.context = context;
		this.image = image;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item, viewGroup, false);
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
				chatViewHolder.chatTimeMe.setText(DateOperations.utcToLocalTZAMPM(chatHistory.getCreatedAt()));
			} else {
				chatViewHolder.layoutYou.setVisibility(View.VISIBLE);
				chatViewHolder.layoutMe.setVisibility(View.GONE);

				chatViewHolder.chatTextYou.setText(chatHistory.getMessage());
				chatViewHolder.chatTimeYou.setText(DateOperations.utcToLocalTZAMPM(chatHistory.getCreatedAt()));
				if(!"".equalsIgnoreCase(Data.userData.userImage)) {
					try {
						float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
						Picasso.with(context).
								load(image)
								.placeholder(R.drawable.ring)
								.transform(new CircleTransform())
								.resize((int) (130f * minRatio), (int) (130f * minRatio)).centerCrop()
								.into(chatViewHolder.userIconYou);
					} catch (Exception e) {
						e.printStackTrace();
					}
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
			chatTextYou = (TextView) itemView.findViewById(R.id.chat_txt_you);
			chatTimeYou = (TextView) itemView.findViewById(R.id.chat_time_you);
			userIconYou = (ImageView) itemView.findViewById(R.id.usr_icon_you);
			layoutYou = (RelativeLayout) itemView.findViewById(R.id.layout_you);

			chatTextMe = (TextView) itemView.findViewById(R.id.chat_txt_me);
			chatTimeMe = (TextView) itemView.findViewById(R.id.chat_time_me);
			userIconMe = (ImageView) itemView.findViewById(R.id.usr_icon_me);
			layoutMe = (RelativeLayout) itemView.findViewById(R.id.layout_me);
		}
	}

}