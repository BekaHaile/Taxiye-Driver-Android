package product.clicklabs.jugnoo.driver.dodo.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.TransactionUtils;
import product.clicklabs.jugnoo.driver.dodo.adapters.DeliveryInfoAdapter;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.utils.ASSL;



@SuppressLint("ValidFragment")
public class DeliveryInfoListFragment extends Fragment {

	private LinearLayout linearLayoutRoot;
	private RecyclerView recyclerViewDeliveryInfo;
	private DeliveryInfoAdapter deliveryInfoAdapter;
	private TransactionUtils transactionUtils;

	private View rootView;
	private HomeActivity activity;

	public DeliveryInfoListFragment() {}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_delivery_info_list, container, false);
		activity = (HomeActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			linearLayoutRoot.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			ASSL.DoMagic(linearLayoutRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}

		recyclerViewDeliveryInfo = (RecyclerView) rootView.findViewById(R.id.recyclerViewDeliveryInfo);
		recyclerViewDeliveryInfo.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewDeliveryInfo.setItemAnimator(new DefaultItemAnimator());
		recyclerViewDeliveryInfo.setHasFixedSize(false);

		deliveryInfoAdapter = new DeliveryInfoAdapter(activity, new ArrayList<DeliveryInfo>(), new DeliveryInfoAdapter.Callback() {
			@Override
			public void onClick(int position, DeliveryInfo deliveryInfo) {
				getTransactionUtils().openMarkDeliveryFragment(activity, activity.getRelativeLayoutContainer());
			}
		});

		recyclerViewDeliveryInfo.setAdapter(deliveryInfoAdapter);

		return rootView;
	}

	public TransactionUtils getTransactionUtils(){
		if(transactionUtils == null){
			transactionUtils = new TransactionUtils();
		}
		return transactionUtils;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(linearLayoutRoot);
		System.gc();
	}

}
