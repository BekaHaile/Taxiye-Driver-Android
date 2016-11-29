package product.clicklabs.jugnoo.driver.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.EarningsDetailResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class SwitchCustomerFragment extends Fragment {

	private RelativeLayout relativeLayoutRoot;
	private LinearLayout linearLayoutCard2, linearLayoutCard1;

	private TextView textViewCustome1Name, textViewCustome1Address, textViewCustome2Name, textViewCustome2Address;
	private Button backBtn, buttonSelect1, buttonCancel1, buttonSelect2, buttonCancel2;
	private ImageView imageViewDote1, imageViewDote2, imageViewExpended, imageViewExpendedSide;

	private View rootView;
	private FragmentActivity activity;

	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.init(activity, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(activity, Data.FLURRY_KEY);
		FlurryAgent.onEvent(SwitchCustomerFragment.class.getSimpleName() + " started");
	}

	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(activity);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_earnings_new, container, false);


		activity = getActivity();

		relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			if (relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		linearLayoutCard1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutCard1);
		linearLayoutCard2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutCard2);


		backBtn = (Button) rootView.findViewById(R.id.backBtn);
		buttonSelect1 = (Button) rootView.findViewById(R.id.buttonSelect1);
		buttonCancel1 = (Button) rootView.findViewById(R.id.buttonCancel1);
		buttonSelect2 = (Button) rootView.findViewById(R.id.buttonSelect2);
		buttonCancel2 = (Button) rootView.findViewById(R.id.buttonCancel2);

		imageViewDote1 = (ImageView) rootView.findViewById(R.id.imageViewDote1);
		imageViewDote2 = (ImageView) rootView.findViewById(R.id.imageViewDote2);
		imageViewExpended = (ImageView) rootView.findViewById(R.id.imageViewExpended);
		imageViewExpendedSide = (ImageView) rootView.findViewById(R.id.imageViewExpendedSide);

		textViewCustome1Name = (TextView) rootView.findViewById(R.id.textViewCustome1Name);
		textViewCustome1Name.setTypeface(Data.latoRegular(activity));
		textViewCustome1Address = (TextView) rootView.findViewById(R.id.textViewCustome1Address);
		textViewCustome1Address.setTypeface(Data.latoRegular(activity));

		textViewCustome2Name = (TextView) rootView.findViewById(R.id.textViewCustome2Name);
		textViewCustome2Name.setTypeface(Data.latoRegular(activity));
		textViewCustome2Address = (TextView) rootView.findViewById(R.id.textViewCustome2Address);
		textViewCustome2Address.setTypeface(Data.latoRegular(activity));



		try {
		} catch (Exception e) {
			e.printStackTrace();
		}


		return rootView;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(relativeLayoutRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}

}
