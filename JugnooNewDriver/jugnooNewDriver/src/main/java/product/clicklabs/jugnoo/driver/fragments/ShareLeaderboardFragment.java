package product.clicklabs.jugnoo.driver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.ShareActivity;
import product.clicklabs.jugnoo.driver.adapters.LeaderboardItemsAdapter;
import product.clicklabs.jugnoo.driver.retrofit.model.LeaderboardResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.Ranklist;
import rmn.androidscreenlibrary.ASSL;


public class ShareLeaderboardFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private Button buttonLocal, buttonGlobal;
	private TextView textViewDaily, textViewWeekly;
	private RecyclerView recyclerViewLb;
	private LeaderboardItemsAdapter leaderboardItemsAdapter;
	private ArrayList<Ranklist> leaderboardItems;

	private View rootView;
    private ShareActivity activity;

	private LBLocationType lbLocationType;
	private LBTimeType lbTimeType;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Data.FLURRY_KEY);
        FlurryAgent.onStartSession(activity, Data.FLURRY_KEY);
        FlurryAgent.onEvent(ShareLeaderboardFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_share_leaderboard, container, false);


        activity = (ShareActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		buttonLocal = (Button)rootView.findViewById(R.id.buttonLocal);
		buttonLocal.setTypeface(Data.latoRegular(activity));
		buttonGlobal = (Button)rootView.findViewById(R.id.buttonGlobal);
		buttonGlobal.setTypeface(Data.latoRegular(activity));

		textViewDaily = (TextView)rootView.findViewById(R.id.textViewDaily);
		textViewDaily.setTypeface(Data.latoRegular(activity));
		textViewWeekly = (TextView)rootView.findViewById(R.id.textViewWeekly);
		textViewWeekly.setTypeface(Data.latoRegular(activity));

		recyclerViewLb = (RecyclerView)rootView.findViewById(R.id.recyclerViewLb);
		recyclerViewLb.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewLb.setItemAnimator(new DefaultItemAnimator());
		recyclerViewLb.setHasFixedSize(false);

		leaderboardItems = new ArrayList<>();
		leaderboardItemsAdapter = new LeaderboardItemsAdapter(leaderboardItems, activity, R.layout.list_item_lb_entry);
		recyclerViewLb.setAdapter(leaderboardItemsAdapter);

		buttonLocal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateList(LBLocationType.LOCAL, lbTimeType);
			}
		});

		buttonGlobal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateList(LBLocationType.GLOBAL, lbTimeType);
			}
		});

		textViewDaily.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateList(lbLocationType, LBTimeType.DAILY);
			}
		});

		textViewWeekly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateList(lbLocationType, LBTimeType.WEEKLY);
			}
		});


		try {
		} catch(Exception e){
			e.printStackTrace();
		}

		updateList(LBLocationType.LOCAL, LBTimeType.DAILY);

		return rootView;
	}

	public void update(){
		updateList(lbLocationType, lbTimeType);
	}


	private void updateList(LBLocationType lbLocationType, LBTimeType lbTimeType){
		try {
			leaderboardItems.clear();

			if(LBTimeType.DAILY == lbTimeType){
				if(this.lbTimeType != lbTimeType) {
					textViewDaily.setBackgroundResource(R.drawable.background_blue);
					textViewDaily.setTextColor(getResources().getColor(R.color.white));
					textViewWeekly.setBackgroundResource(R.drawable.background_white_bordered_blue_selector);
					textViewWeekly.setTextColor(getResources().getColorStateList(R.color.grey_leader));
				}
			}
			else if(LBTimeType.WEEKLY == lbTimeType){
				if(this.lbTimeType != lbTimeType) {
					textViewDaily.setBackgroundResource(R.drawable.background_white_bordered_blue_selector);
					textViewDaily.setTextColor(getResources().getColorStateList(R.color.grey_leader));
					textViewWeekly.setBackgroundResource(R.drawable.background_blue);
					textViewWeekly.setTextColor(getResources().getColor(R.color.white));
				}
			}

			if(LBLocationType.LOCAL == lbLocationType){
				if(this.lbLocationType != lbLocationType){
					buttonLocal.setBackgroundResource(R.drawable.button_blue_normal);
					buttonLocal.setTextColor(getResources().getColor(R.color.white));
					buttonGlobal.setBackgroundResource(R.drawable.background_white_bordered_blue_rounded_selector);
					buttonGlobal.setTextColor(getResources().getColorStateList(R.color.grey_leader));
				}
				if(LBTimeType.DAILY == lbTimeType){
					leaderboardItems.addAll(activity.leaderboardResponse.getLocal().getDaily().getRanklist());
					LeaderboardResponse.Userinfo userInfo = activity.leaderboardResponse.getLocal().getDaily().getUserinfo();
					fillUserInfo(userInfo);
				}
				else if(LBTimeType.WEEKLY == lbTimeType){
					leaderboardItems.addAll(activity.leaderboardResponse.getLocal().getWeekly().getRanklist());
					LeaderboardResponse.Userinfo userInfo = activity.leaderboardResponse.getLocal().getWeekly().getUserinfo();
					fillUserInfo(userInfo);
				}
			}
			else if(LBLocationType.GLOBAL == lbLocationType){
				if(this.lbLocationType != lbLocationType) {
					buttonLocal.setBackgroundResource(R.drawable.background_white_bordered_blue_rounded_selector);
					buttonLocal.setTextColor(getResources().getColorStateList(R.color.grey_leader));
					buttonGlobal.setBackgroundResource(R.drawable.button_blue_normal);
					buttonGlobal.setTextColor(getResources().getColor(R.color.white));
				}
				if(LBTimeType.DAILY == lbTimeType){
					leaderboardItems.addAll(activity.leaderboardResponse.getGlobal().getDaily().getRanklist());
					LeaderboardResponse.Userinfo userInfo = activity.leaderboardResponse.getGlobal().getDaily().getUserinfo();
					fillUserInfo(userInfo);
				}
				else if(LBTimeType.WEEKLY == lbTimeType){
					leaderboardItems.addAll(activity.leaderboardResponse.getGlobal().getWeekly().getRanklist());
					LeaderboardResponse.Userinfo userInfo = activity.leaderboardResponse.getGlobal().getWeekly().getUserinfo();
					fillUserInfo(userInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			this.lbLocationType = lbLocationType;
			this.lbTimeType = lbTimeType;

			leaderboardItemsAdapter.notifyDataSetChanged();
		}
	}

	private void fillUserInfo(LeaderboardResponse.Userinfo userInfo){
		if(userInfo != null && userInfo.getRank() != null){
			if(userInfo.getRank() > 5){
				leaderboardItems.add(new Ranklist(userInfo.getRank(),
						userInfo.getDownloads(), Data.userData.userName, true));
			}
			else if(userInfo.getRank() < 0){
				leaderboardItems.add(new Ranklist(userInfo.getRank(),
						userInfo.getDownloads(), Data.userData.userName, true));
			}
			else {
				for (int i=0; i<leaderboardItems.size(); i++) {
					Ranklist ranklist = leaderboardItems.get(i);
					if (ranklist.getRank().equals(userInfo.getRank())) {
						leaderboardItems.get(i).setIsUser(true);
						break;
					}
				}
			}
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}

	enum LBLocationType{
		LOCAL, GLOBAL
	}

	enum LBTimeType{
		DAILY, WEEKLY
	}


}
