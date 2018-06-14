package product.clicklabs.jugnoo.driver.fragments;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.NotificationCenterActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.NotificationAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.NotificationInboxResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by aneeshbansal on 08/06/16.
 */
public class NotificationMessagesFragment extends android.support.v4.app.Fragment {


	private RecyclerView recyclerViewNotification;
	private LinearLayout root;
	private NotificationAdapter myNotificationAdapter;
	private ArrayList<NotificationInboxResponse.NotificationData> notificationList;
	private LinearLayout linearLayoutNoNotifications;
	private SwipeRefreshLayout swipeRefreshLayout;


	private View rootView;
	private NotificationCenterActivity activity;

	public NotificationMessagesFragment(){

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_notification_messages, container, false);

		activity = (NotificationCenterActivity) getActivity();

		root = (LinearLayout) rootView.findViewById(R.id.root);
		new ASSL(activity, root, 1134, 720, false);



		swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setColorSchemeResources(R.color.themeColor);
		linearLayoutNoNotifications = (LinearLayout) rootView.findViewById(R.id.linearLayoutNoNotifications);
		linearLayoutNoNotifications.setVisibility(View.GONE);
		((TextView)rootView.findViewById(R.id.textViewNoNotifications)).setTypeface(Fonts.mavenRegular(activity));

		recyclerViewNotification = (RecyclerView) rootView.findViewById(R.id.my_request_recycler);
		recyclerViewNotification.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewNotification.setItemAnimator(new DefaultItemAnimator());
		recyclerViewNotification.setHasFixedSize(false);

		notificationList = new ArrayList<>();
		myNotificationAdapter = new NotificationAdapter(notificationList, activity,
				R.layout.list_item_notification, 0, new NotificationAdapter.Callback() {
			@Override
			public void onShowMoreClick() {
				getNotificationInboxApi(false);
			}
		});
		recyclerViewNotification.setAdapter(myNotificationAdapter);


		getNotificationInboxApi(true);

		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getNotificationInboxApi(true);
			}
		});



		return rootView;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(root);
		} catch (Exception e) {
		}
		System.gc();
	}

	public void update(){
		try{
			if(activity != null){
				getNotificationInboxApi(true);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private void getNotificationInboxApi(final boolean refresh) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				if(!swipeRefreshLayout.isRefreshing()) {
					DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
				}
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				if(refresh){
					params.put("offset", "0");
				} else{
					params.put("offset", String.valueOf(myNotificationAdapter.getListSize()));
				}
				HomeUtil.putDefaultParams(params);


				RestClient.getApiServices().notificationInbox(params, new Callback<NotificationInboxResponse>() {
					@Override
					public void success(final NotificationInboxResponse notificationInboxResponse, Response response) {
						DialogPopup.dismissLoadingDialog();

						try {
							swipeRefreshLayout.setRefreshing(false);
							if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
								Prefs.with(activity).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
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
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			DialogPopup.dismissLoadingDialog();
			e.printStackTrace();
		}
	}

}
