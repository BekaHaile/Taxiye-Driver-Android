package product.clicklabs.jugnoo.driver.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.chat.adapter.ChatAdapter;
import product.clicklabs.jugnoo.driver.chat.adapter.ChatSuggestionAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.FetchChatResponse;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Created by ankit on 10/11/16.
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener{

	private EditText input;
	private int engagementId = 0;
	private String userImage;
	LinearLayoutManager linearLayoutManager;
	RecyclerView recyclerViewChat, recyclerViewChatOptions;
	ChatAdapter chatAdapter;
	boolean appOpen = true;
	ChatSuggestionAdapter chatSuggestionAdapter;
	public static String CHAT_SCREEN_OPEN = null;
	private Handler handler = new Handler();
	ArrayList<FetchChatResponse.ChatHistory> chatResponse = new ArrayList<>();
	ArrayList<FetchChatResponse.Suggestion> chatSuggestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

		if(getIntent().hasExtra(Constants.KEY_ENGAGEMENT_ID)){
			engagementId = getIntent().getIntExtra(Constants.KEY_ENGAGEMENT_ID, Integer.parseInt(Data.getCurrentEngagementId()));
			userImage = getIntent().getStringExtra("user_image");
		} else{
			try {
				appOpen =false;
				engagementId = Integer.parseInt(Data.getCurrentEngagementId());
				userImage = Data.getCurrentCustomerInfo().image;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(Fonts.mavenRegular(this));
        title.setText(R.string.chat);
		ImageView backBtn = (ImageView) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);

		input = (EditText) findViewById(R.id.input); input.setTypeface(Fonts.mavenRegular(this));
		ImageView send = (ImageView) findViewById(R.id.action_send);

		recyclerViewChat = (RecyclerView) findViewById(R.id.recyclerViewChat);
		linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
		recyclerViewChat.setLayoutManager(linearLayoutManager);
		recyclerViewChat.setHasFixedSize(false);
		recyclerViewChat.setItemAnimator(new DefaultItemAnimator());
		chatResponse = new ArrayList<>();
		chatAdapter = new ChatAdapter(this, chatResponse, userImage);
		recyclerViewChat.setAdapter(chatAdapter);
		Prefs.with(ChatActivity.this).save(Constants.KEY_CHAT_COUNT, 0);
		recyclerViewChatOptions = (RecyclerView) findViewById(R.id.recyclerViewChatOptions);
		recyclerViewChatOptions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		recyclerViewChatOptions.setItemAnimator(new DefaultItemAnimator());
		recyclerViewChatOptions.setHasFixedSize(false);
		chatSuggestions = new ArrayList<>();
		chatSuggestionAdapter = new ChatSuggestionAdapter(this, chatSuggestions,  new ChatSuggestionAdapter.Callback() {
			@Override
			public void onSuggestionClick(int position, FetchChatResponse.Suggestion suggestion) {
				if(suggestion.getSuggestionId() != null) {
					sendChat(suggestion.getSuggestion(), suggestion.getSuggestionId());
				}else {
					sendChat(suggestion.getSuggestion(), -1);
				}
			}
		});
		recyclerViewChatOptions.setAdapter(chatSuggestionAdapter);

		// done action listener to send the chat
		input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if(input.getText().length() >0) {
						sendChat(input.getText().toString().trim(), -1);
					}
					return true;
				}
				return false;
			}
		});

		input.setOnClickListener(this);
		send.setOnClickListener(this);

		fetchChat(ChatActivity.this);

		if(Data.getCurrentCustomerInfo() != null) {
			title.setText(Data.getCurrentCustomerInfo().getName());
		}

		if(Data.getCurrentCustomerInfo() == null){
			if(HomeActivity.activity == null){
				Intent homeScreen = new Intent(this, DriverSplashActivity.class);
				homeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(homeScreen);
			}

			performBackPressed();
		}


		CHAT_SCREEN_OPEN = "yes";
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					Utils.hideSoftKeyboard(ChatActivity.this, input);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 100);

		registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_FINISH_ACTIVITY));
	}

	Runnable loadDiscussion=new Runnable() {
		@Override
		public void run() {
			loadDiscussions();
		}
	};


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.backBtn:
				performBackPressed();
				break;
			case R.id.action_send:
				if(input.getText().length() >0) {
					sendChat(input.getText().toString().trim(), -1);
				}
				break;

		}
	}

	@Override
	public void onResume(){
		super.onResume();
		CHAT_SCREEN_OPEN = "yes";
		Data.context = ChatActivity.this;
	}

	@Override
	public void onPause(){
		CHAT_SCREEN_OPEN = null;
		super.onPause();
	}

    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CHAT_SCREEN_OPEN = null;
		Data.context = null;
		unregisterReceiver(broadcastReceiver);
		if(handler != null && loadDiscussion != null){
			handler.removeCallbacks(loadDiscussion);
		}
	}


	// sends the message to server and display it
	private void sendChat(String inputText, int id) {
		try {
			if (!(inputText.isEmpty())) {
				FetchChatResponse.ChatHistory chatHistory = new FetchChatResponse.ChatHistory();
				chatHistory.setChatHistoryId(0);
				chatHistory.setCreatedAt(DateOperations.getCurrentTimeInUTC());
				chatHistory.setIsSender(1);
				chatHistory.setMessage(inputText);
				chatResponse.add(chatHistory);
				int position = chatAdapter.getItemCount() - 1;
				chatAdapter.notifyItemInserted(position);

				linearLayoutManager.scrollToPositionWithOffset(chatAdapter.getItemCount() - 1, 10);
				postChat(ChatActivity.this, inputText, id);
				input.setText("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void loadDiscussions(){
		if (!AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			DialogPopup.alertPopupTwoButtonsWithListeners(ChatActivity.this, "",
					getResources().getString(R.string.no_internet_tap_to_retry), getResources().getString(R.string.retry),
					getResources().getString(R.string.cancel), new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					loadDiscussions();
				}
			}, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					performBackPressed();
				}
			}, true, false);
			return;
		}

		fetchChat(ChatActivity.this);
	}


    private void fetchChat(final Activity activity) {

        try {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put("login_type", "1");
                params.put("engagement_id", String.valueOf(engagementId));


                RestClient.getChatAckApiServices().fetchChat(params, new Callback<FetchChatResponse>() {
					@Override
					public void success(FetchChatResponse fetchChat, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.e("Shared rides jsonString", "=" + jsonString);
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								Prefs.with(ChatActivity.this).save(Constants.KEY_CHAT_COUNT, 0);
								chatResponse.clear();
								chatSuggestions.clear();
								chatResponse.addAll(fetchChat.getChatHistory());
								Collections.reverse(chatResponse);
								if(fetchChat.getChatHistory().size() > Prefs.with(activity).getInt(SPLabels.CHAT_SIZE,0) && CHAT_SCREEN_OPEN != null){
									SoundMediaPlayer.startSound(activity, R.raw.whats_app_shat_sound, 1, false);
								}
								Prefs.with(activity).save(SPLabels.CHAT_SIZE, fetchChat.getChatHistory().size());
								chatSuggestions.addAll(fetchChat.getSuggestions());
								if(fetchChat.getSuggestions().size() > 0){
									recyclerViewChatOptions.setVisibility(View.VISIBLE);
								} else {
									recyclerViewChatOptions.setVisibility(View.GONE);
								}
								chatSuggestionAdapter.notifyDataSetChanged();

								chatAdapter.notifyDataSetChanged();
								linearLayoutManager.scrollToPositionWithOffset(chatAdapter.getItemCount() - 1, 10);
								//updateListData(getResources().getString(R.string.add_cash), false);
								if(handler != null && loadDiscussion != null) {
									handler.removeCallbacks(loadDiscussion);
									handler.postDelayed(loadDiscussion, 5000);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
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
        }
    }

    private void postChat(final Activity activity, final String message, final int id) {

        try {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put("login_type", "1");
                params.put("engagement_id", String.valueOf(engagementId));
                params.put("message", message);
				params.put("suggestion_id", ""+id);

                RestClient.getChatAckApiServices().postChat(params, new Callback<FetchChatResponse>() {
					@Override
					public void success(FetchChatResponse fetchChatResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.e("Shared rides jsonString", "=" + jsonString);
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								if (handler != null && loadDiscussion != null) {
									handler.removeCallbacks(loadDiscussion);
								}
								fetchChat(ChatActivity.this);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
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
        }
    }

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(Constants.ACTION_FINISH_ACTIVITY)) {
				performBackPressed();
			}
		}
	};
}
