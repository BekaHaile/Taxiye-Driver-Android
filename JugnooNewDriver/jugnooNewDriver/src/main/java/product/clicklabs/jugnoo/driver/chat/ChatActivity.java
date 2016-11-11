package product.clicklabs.jugnoo.driver.chat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.ChatAdapter;
import product.clicklabs.jugnoo.driver.adapters.InfoTilesAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.dodo.MyViewPager;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.FetchChatResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Created by ankit on 10/11/16.
 */

public class ChatActivity extends Activity implements View.OnClickListener{

    private RelativeLayout relative;
    private TextView textViewTitle;
    private ImageView imageViewBack;

	RecyclerView recyclerViewChat;
	ChatAdapter chatAdapter;
	ArrayList<FetchChatResponse.ChatHistory> chatResponse = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack); imageViewBack.setOnClickListener(this);

		recyclerViewChat = (RecyclerView) findViewById(R.id.recyclerViewChat);
		recyclerViewChat.setHasFixedSize(true);
		recyclerViewChat.setItemAnimator(new DefaultItemAnimator());
		chatResponse = new ArrayList<>();
		chatAdapter = new ChatAdapter(this, chatResponse);
		recyclerViewChat.setAdapter(chatAdapter);
    }


	public void updateListData(String message, boolean errorOccurred) {
		if (errorOccurred) {
			chatAdapter.notifyDataSetChanged();
		} else {
			chatAdapter.notifyDataSetChanged();
		}
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewBack:
                performBackPressed();
            break;

        }
    }

    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    private void fetchChat(final Activity activity) {

        try {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                DialogPopup.showLoadingDialog(ChatActivity.this, getResources().getString(R.string.loading));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put("login_type", "1");
                params.put("engagement_id", Data.getCurrentEngagementId());

                RestClient.getApiServices().fetchChat(params, new Callback<FetchChatResponse>() {
					@Override
					public void success(FetchChatResponse fetchChat, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.e("Shared rides jsonString", "=" + jsonString);
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = jObj.getString("message");
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								chatResponse.addAll(fetchChat.getChatHistory());
								updateListData(getResources().getString(R.string.no_rides), false);

							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
					}
				});

            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogPopup.dismissLoadingDialog();
        }
    }

    private void postChat(final Activity activity, final String message) {

        try {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                DialogPopup.showLoadingDialog(ChatActivity.this, getResources().getString(R.string.loading));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put("login_type", "1");
                params.put("engagement_id", Data.getCurrentEngagementId());
                params.put("message", message);

                RestClient.getApiServices().postChat(params, new Callback<FetchChatResponse>() {
					@Override
					public void success(FetchChatResponse fetchChatResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.e("Shared rides jsonString", "=" + jsonString);
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = jObj.getString("message");
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
					}
				});

            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogPopup.dismissLoadingDialog();
        }
    }
}
