package product.clicklabs.jugnoo.driver.dodo.fragments;

import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.MyViewPager;
import product.clicklabs.jugnoo.driver.dodo.adapters.DeliveryListAdapter;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class DeliveryInfoTabs implements ViewPager.OnPageChangeListener {

	private final String TAG = "DeliveryInfoTabs";

	private DeliveryListAdapter adapter;
	private MyViewPager deliveryListHorizontal;

	private HomeActivity activity;

	private ArrayList<DeliveryInfo> tasksList;

	public DeliveryInfoTabs(HomeActivity activity, View rootView) {
		this.activity = activity;
		init(rootView);
	}

	/**
	 * Method to initialize the views
	 *
	 * @param rootView
	 */
	private void init(View rootView) {
		deliveryListHorizontal = (MyViewPager) rootView.findViewById(R.id.deliveryListHorizontal);
		deliveryListHorizontal.addOnPageChangeListener(this);
	}


	/**
	 * Method to render the TasksList on to the Map
	 *
	 * @param tasksList
	 */
	public void render(int engagementId, ArrayList<DeliveryInfo> tasksList) {
		this.tasksList = tasksList;
		if (tasksList == null) {
			deliveryListHorizontal.setVisibility(View.GONE);
			return;
		}
		deliveryListHorizontal.setVisibility(View.VISIBLE);
		populateTasksList(engagementId, tasksList);
	}



	private void populateTasksList(int engagementId, ArrayList<DeliveryInfo> tasksList) {
		adapter = new DeliveryListAdapter(activity, tasksList, engagementId);
		deliveryListHorizontal.setPageMargin(Utils.dpToPx(activity, 8));
		deliveryListHorizontal.setAdapter(adapter);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		Log.e(TAG, "onPageSelected: " + position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	public void notifyDatasetchange(){
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}


}



