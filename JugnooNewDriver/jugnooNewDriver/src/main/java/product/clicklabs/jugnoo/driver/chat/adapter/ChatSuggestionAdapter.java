package product.clicklabs.jugnoo.driver.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
public class ChatSuggestionAdapter extends RecyclerView.Adapter<ChatSuggestionAdapter.infoTileViewHolder> {

	private ArrayList<FetchChatResponse.Suggestion> chatHistories;
	private Context context;
	private Callback callback;
	private FetchChatResponse fetchChatResponse;

	public ChatSuggestionAdapter(Context context, ArrayList<FetchChatResponse.Suggestion> chatHistories, Callback callback) {
		this.chatHistories = chatHistories;
		this.context = context;
		this.callback = callback;
	}

	@Override
	public int getItemCount() {
		return chatHistories.size();
	}

	@Override
	public void onBindViewHolder(infoTileViewHolder infoTileViewHolder, int i) {
		final FetchChatResponse.Suggestion itr = chatHistories.get(i);

		infoTileViewHolder.name.setTypeface(Data.latoRegular(context));
		infoTileViewHolder.name.setText(itr.getSuggestion());

		infoTileViewHolder.relative.setTag(i);
		infoTileViewHolder.relative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				infoTileResponses.get((int) v.getTag()).completed = 1;

				try {
					int pos = (int) v.getTag();
					callback.onSuggestionClick(pos, chatHistories.get(pos));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public infoTileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.
				from(viewGroup.getContext()).
				inflate(R.layout.list_item_chat_suggestion, viewGroup, false);

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
			relative.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(relative);
		}
	}

	public interface Callback{
		void onSuggestionClick(int position, FetchChatResponse.Suggestion suggestion);
	}

}