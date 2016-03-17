package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.adapters.NotificationAdapter;
import product.clicklabs.jugnoo.driver.datastructure.DisplayPushHandler;
import product.clicklabs.jugnoo.driver.datastructure.NotificationData;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.EventsHolder;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Prefs;


/**
 * Created by socomo on 10/15/15.
 */
public class NotificationCenterActivity extends BaseActivity implements DisplayPushHandler {

    private LinearLayout root;
    private TextView title;
    private Button backBtn;
    private RecyclerView recyclerViewNotification;
    private NotificationAdapter myNotificationAdapter;
    private ArrayList<NotificationData> notificationList;
	private LinearLayout linearLayoutNoNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);

		EventsHolder.displayPushHandler = this;

        root = (LinearLayout)findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

		title = (TextView) findViewById(R.id.title);title.setTypeface(Data.latoRegular(getApplicationContext()));
		backBtn = (Button)findViewById(R.id.backBtn);

		linearLayoutNoNotifications = (LinearLayout) findViewById(R.id.linearLayoutNoNotifications);
		linearLayoutNoNotifications.setVisibility(View.GONE);
		((TextView)findViewById(R.id.textViewNoNotifications)).setTypeface(Data.latoRegular(getApplicationContext()));

        recyclerViewNotification = (RecyclerView) findViewById(R.id.my_request_recycler);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(NotificationCenterActivity.this));
        recyclerViewNotification.setItemAnimator(new DefaultItemAnimator());
		recyclerViewNotification.setHasFixedSize(false);

		notificationList = new ArrayList<>();
		myNotificationAdapter = new NotificationAdapter(notificationList, NotificationCenterActivity.this,
				R.layout.notification_list_item);
		recyclerViewNotification.setAdapter(myNotificationAdapter);


		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		loadListFromDB();

    }



	class GetNotificationsAsync extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DialogPopup.showLoadingDialog(NotificationCenterActivity.this, "Loading...");
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				notificationList.clear();
				notificationList.addAll(Database2.getInstance(NotificationCenterActivity.this).getAllNotification());
				Prefs.with(NotificationCenterActivity.this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			DialogPopup.dismissLoadingDialog();
			if(notificationList.size() > 0){
				linearLayoutNoNotifications.setVisibility(View.GONE);
			} else{
				linearLayoutNoNotifications.setVisibility(View.VISIBLE);
			}
			myNotificationAdapter.notifyDataSetChanged();
		}
	}

	private void loadListFromDB(){
		notificationList.clear();
		notificationList.addAll(Database2.getInstance(NotificationCenterActivity.this).getAllNotification());
		Prefs.with(NotificationCenterActivity.this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
		if(notificationList.size() > 0){
			linearLayoutNoNotifications.setVisibility(View.GONE);
		} else{
			linearLayoutNoNotifications.setVisibility(View.VISIBLE);
		}
		myNotificationAdapter.notifyDataSetChanged();
	}


    public void performBackPressed(){
        Intent intent=new Intent();
        setResult(RESULT_CANCELED, intent);
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
				loadListFromDB();
			}
		});
	}

}
