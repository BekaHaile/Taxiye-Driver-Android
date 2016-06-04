package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.adapters.NotificationAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DisplayPushHandler;
import product.clicklabs.jugnoo.driver.datastructure.NotificationData;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.NotificationInboxResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.EventsHolder;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by socomo on 10/15/15.
 */
public class NotificationCenterActivity extends BaseActivity implements DisplayPushHandler {

    private RelativeLayout root;
    private TextView title;
    private Button backBtn;
    private RecyclerView recyclerViewNotification;
    private NotificationAdapter myNotificationAdapter;
    private ArrayList<NotificationInboxResponse.NotificationData> notificationList;
	private LinearLayout linearLayoutNoNotifications;
	private SwipeRefreshLayout swipeRefreshLayout;


	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
		FlurryAgent.onEvent("Notification opened");
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);

		EventsHolder.displayPushHandler = this;

        root = (RelativeLayout)findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(getApplicationContext()));
		backBtn = (Button)findViewById(R.id.backBtn);

		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setColorSchemeResources(R.color.new_orange);
		linearLayoutNoNotifications = (LinearLayout) findViewById(R.id.linearLayoutNoNotifications);
		linearLayoutNoNotifications.setVisibility(View.GONE);
		((TextView)findViewById(R.id.textViewNoNotifications)).setTypeface(Data.latoRegular(getApplicationContext()));

        recyclerViewNotification = (RecyclerView) findViewById(R.id.my_request_recycler);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(NotificationCenterActivity.this));
        recyclerViewNotification.setItemAnimator(new DefaultItemAnimator());
		recyclerViewNotification.setHasFixedSize(false);

		notificationList = new ArrayList<>();
		myNotificationAdapter = new NotificationAdapter(notificationList, NotificationCenterActivity.this,
				R.layout.list_item_notification, 0, new NotificationAdapter.Callback() {
			@Override
			public void onShowMoreClick() {
				getNotificationInboxApi(false);
			}
		});
		recyclerViewNotification.setAdapter(myNotificationAdapter);


		getNotificationInboxApi(true);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getNotificationInboxApi(true);
			}
		});


//		loadListFromDB();

    }


//	private void loadListFromDB(){
//		notificationList.clear();
//		//notificationList.addAll(Database2.getInstance(NotificationCenterActivity.this).getAllNotification());
//		Prefs.with(NotificationCenterActivity.this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
//		if(notificationList.size() > 0){
//			linearLayoutNoNotifications.setVisibility(View.GONE);
//		} else{
//			linearLayoutNoNotifications.setVisibility(View.VISIBLE);
//		}
//		myNotificationAdapter.notifyDataSetChanged();
//	}


    public void performBackPressed(){
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

	@Override
	public void onBackPressed() {
		performBackPressed();
	}

	@Override
	protected void onDestroy() {
		ASSL.closeActivity(root);
		super.onDestroy();
		System.gc();
	}

	@Override
	public void onDisplayMessagePushReceived() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				loadListFromDB();
				getNotificationInboxApi(true);
			}
		});
	}

	private void getNotificationInboxApi(final boolean refresh) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				if(!swipeRefreshLayout.isRefreshing()) {
					DialogPopup.showLoadingDialog(NotificationCenterActivity.this, getResources().getString(R.string.loading));
				}
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				if(refresh){
					params.put("offset", "0");
				} else{
					params.put("offset", String.valueOf(myNotificationAdapter.getListSize()));
				}


				RestClient.getApiServices().notificationInbox(params, new Callback<NotificationInboxResponse>() {
					@Override
					public void success(final NotificationInboxResponse notificationInboxResponse, Response response) {
						DialogPopup.dismissLoadingDialog();

						try {
							swipeRefreshLayout.setRefreshing(false);
							if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
								Prefs.with(NotificationCenterActivity.this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
								if(notificationInboxResponse.getPushes().size() > 0) {
									myNotificationAdapter.notifyList(notificationInboxResponse.getTotal(),
											(ArrayList<NotificationInboxResponse.NotificationData>) notificationInboxResponse.getPushes(), refresh);
								}
								if (myNotificationAdapter.getListSize() > 0) {
									linearLayoutNoNotifications.setVisibility(View.GONE);
									swipeRefreshLayout.setVisibility(View.VISIBLE);
								} else {
									linearLayoutNoNotifications.setVisibility(View.VISIBLE);
//									swipeRefreshLayout.setVisibility(View.GONE);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						swipeRefreshLayout.setRefreshing(false);
						DialogPopup.alertPopup(NotificationCenterActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
			} else {
				DialogPopup.alertPopup(NotificationCenterActivity.this, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			DialogPopup.dismissLoadingDialog();
			e.printStackTrace();
		}
	}

}
