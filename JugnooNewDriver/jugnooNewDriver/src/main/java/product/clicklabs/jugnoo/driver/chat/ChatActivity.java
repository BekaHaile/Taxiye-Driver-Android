package product.clicklabs.jugnoo.driver.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.RideDetailsNewActivity;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.adapters.DriverRideHistoryAdapter;
import product.clicklabs.jugnoo.driver.chat.adapter.ChatAdapter;
import product.clicklabs.jugnoo.driver.chat.adapter.ChatSuggestionAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.FetchChatResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
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

    private RelativeLayout relative;
    private TextView textViewTitle;
    private ImageView imageViewBack;
	private EditText input;
	private int position = 0, engagementId = 0;
	private ImageView send;
	private String userImage;
	RecyclerView recyclerViewChat, recyclerViewChatOptions;
	ChatAdapter chatAdapter;
	boolean appOpen = true;
	ChatSuggestionAdapter chatSuggestionAdapter;
	private FetchChatResponse fetchChatResponse;
	private SimpleDateFormat sdf;
	private final String LOGIN_TYPE = "1";
	public static String CHAT_SCREEN_OPEN = null;
	private Handler handler = new Handler();
	private Handler myHandler=new Handler();
	ArrayList<FetchChatResponse.ChatHistory> chatResponse = new ArrayList<>();
	ArrayList<FetchChatResponse.Suggestion> chatSuggestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

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

		sdf = new SimpleDateFormat("hh:mm a");
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Data.latoRegular(this));
//        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack); imageViewBack.setOnClickListener(this);

		input = (EditText) findViewById(R.id.input);
		send = (ImageView) findViewById(R.id.action_send);

		recyclerViewChat = (RecyclerView) findViewById(R.id.recyclerViewChat);
		recyclerViewChat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		recyclerViewChat.setHasFixedSize(true);
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
			textViewTitle.setText(Data.getCurrentCustomerInfo().getName());
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
					hideSoftKeyboard();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 100);
	}

	Runnable loadDiscussion=new Runnable() {
		@Override
		public void run() {
			loadDiscussions();
			//myHandler.postAtTime(loadDiscussion, 5000);
		}
	};

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			loadDiscussions();
		}
	};


	public void updateListData(String message, boolean errorOccurred) {
		if (errorOccurred) {
			chatAdapter.notifyDataSetChanged();
		} else {
			chatAdapter.notifyDataSetChanged();
		}
	}

	public void hideSoftKeyboard() {
		if(getCurrentFocus()!=null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.imageViewBack:
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
		try {
			if(handler != null){
				handler.removeCallbacks(loadDiscussion);
			}
			if(myHandler != null){
				myHandler.removeCallbacks(loadDiscussion);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// sends the message to server and display it
	private void sendChat(String inputText, int id) {
		//hideKeyboard(input);
		Calendar time = Calendar.getInstance();
		try {
			if (!(inputText.isEmpty())) {
				// add message to list
				FetchChatResponse.ChatHistory chatHistory = fetchChatResponse.new ChatHistory();
				chatHistory.setChatHistoryId(0);
				chatHistory.setCreatedAt(sdf.format(time.getTime()));
				chatHistory.setIsSender(1);
				chatHistory.setMessage(inputText);
				chatResponse.add(chatHistory);
				position = chatAdapter.getItemCount() - 1;
				chatAdapter.notifyItemInserted(position);

				// scroll to the last message
				recyclerViewChat.scrollToPosition(position);
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
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put("login_type", "1");
                params.put("engagement_id", String.valueOf(engagementId));


                RestClient.getChatAckApiServices().fetchChat(params, new Callback<FetchChatResponse>() {
					@Override
					public void success(FetchChatResponse fetchChat, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.e("Shared rides jsonString", "=" + jsonString);
							fetchChatResponse = fetchChat;
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = jObj.getString("message");
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								Prefs.with(ChatActivity.this).save(Constants.KEY_CHAT_COUNT, 0);
								chatResponse.clear();
								chatSuggestions.clear();
								chatResponse.addAll(fetchChat.getChatHistory());
								Collections.reverse(chatResponse);
								if(fetchChat.getChatHistory().size() > Prefs.with(activity).getInt(SPLabels.CHAT_SIZE,0) && CHAT_SCREEN_OPEN != null){
									SoundMediaPlayer.startSound(activity, R.raw.whats_app_shat_sound, 1, true);
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
								recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
								//updateListData(getResources().getString(R.string.add_cash), false);
								if(handler != null && loadDiscussion != null) {
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
                HashMap<String, String> params = new HashMap<String, String>();
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
							String message = jObj.getString("message");
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
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
}
