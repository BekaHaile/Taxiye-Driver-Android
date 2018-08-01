package product.clicklabs.jugnoo.driver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.adapters.LeaderboardItemsAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.Item;
import product.clicklabs.jugnoo.driver.retrofit.model.NewLeaderBoard;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class ShareLeaderboardFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private Button buttonLocal, buttonGlobal;
	private TextView textViewDaily, textViewWeekly, textViewNoOfDownloads;
	private RecyclerView recyclerViewLb;
	private LeaderboardItemsAdapter leaderboardItemsAdapter;
	private ArrayList<Item> leaderboardItems;

	private View rootView;
	private FragmentActivity activity;

	private LBLocationType lbLocationType;
	private LBTimeType lbTimeType;
	private NewLeaderBoard newLeaderBoard;

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
		rootView = inflater.inflate(R.layout.fragment_share_leaderboard, container, false);


		activity = getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if (linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		buttonLocal = (Button) rootView.findViewById(R.id.buttonLocal);
		buttonLocal.setTypeface(Fonts.mavenRegular(activity));
		buttonGlobal = (Button) rootView.findViewById(R.id.buttonGlobal);
		buttonGlobal.setTypeface(Fonts.mavenRegular(activity));

		textViewDaily = (TextView) rootView.findViewById(R.id.textViewDaily);
		textViewDaily.setTypeface(Fonts.mavenRegular(activity));
		textViewWeekly = (TextView) rootView.findViewById(R.id.textViewWeekly);
		textViewWeekly.setTypeface(Fonts.mavenRegular(activity));

		textViewNoOfDownloads = (TextView) rootView.findViewById(R.id.textViewNoOfDownloads);

		recyclerViewLb = (RecyclerView) rootView.findViewById(R.id.recyclerViewLb);
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

		getLeaderboardCall();

		try {
		} catch (Exception e) {
			e.printStackTrace();
		}

		lbLocationType = LBLocationType.LOCAL;
		lbTimeType = LBTimeType.DAILY;

		//updateList(LBLocationType.LOCAL, LBTimeType.DAILY);

		return rootView;
	}

	public void update() {
		updateList(lbLocationType, lbTimeType);
	}


	private void updateList(LBLocationType lbLocationType, LBTimeType lbTimeType) {
		try {
			leaderboardItems.clear();
			textViewNoOfDownloads.setText(newLeaderBoard.getDynamicColumnName());

			if (LBTimeType.DAILY == lbTimeType) {
				if (this.lbTimeType != lbTimeType) {
					textViewDaily.setBackgroundResource(R.color.themeColor);
					textViewDaily.setTextColor(getResources().getColor(R.color.white));
					textViewWeekly.setBackgroundResource(R.drawable.background_white_corner_orange_bordered);
					textViewWeekly.setTextColor(getResources().getColorStateList(R.color.menu_black));
				}
			} else if (LBTimeType.WEEKLY == lbTimeType) {
				if (this.lbTimeType != lbTimeType) {
					textViewDaily.setBackgroundResource(R.drawable.background_white_corner_orange_bordered);
					textViewDaily.setTextColor(getResources().getColorStateList(R.color.menu_black));
					textViewWeekly.setBackgroundResource(R.color.themeColor);
					textViewWeekly.setTextColor(getResources().getColor(R.color.white));
				}
			}

			if (LBLocationType.LOCAL == lbLocationType) {
				if (this.lbLocationType != lbLocationType) {
					buttonLocal.setBackgroundResource(R.drawable.new_orange_btn_round_corner_normal);
					buttonLocal.setTextColor(getResources().getColor(R.color.textColorButton));
					buttonGlobal.setBackgroundResource(R.drawable.background_white_rounded_orange_bordered);
					buttonGlobal.setTextColor(getResources().getColorStateList(R.color.textColor));
				}
				if (LBTimeType.DAILY == lbTimeType) {
					leaderboardItems.addAll(newLeaderBoard.getDriverLeaderBoard().getCityLeaderBoard().getDay());
					int cityDriverRankDaily = newLeaderBoard.getDriverLeaderBoard().getCityDriverRank().getDay();
					int cityDriverCustomValueDaily = newLeaderBoard.getDriverLeaderBoard().getCityDriverRank().getDayScore();
					int totalDriverCityDaily = newLeaderBoard.getDriverLeaderBoard().getTotalDriversCity().getDay();
					fillUserInfo(cityDriverRankDaily, cityDriverCustomValueDaily, totalDriverCityDaily);
				} else if (LBTimeType.WEEKLY == lbTimeType) {
					leaderboardItems.addAll(newLeaderBoard.getDriverLeaderBoard().getCityLeaderBoard().getWeek());
					int cityDriverRankWeekly = newLeaderBoard.getDriverLeaderBoard().getCityDriverRank().getWeek();
					int cityDriverCustomValueWeekly = newLeaderBoard.getDriverLeaderBoard().getCityDriverRank().getWeekScore();
					int totalDriverCityWeek = newLeaderBoard.getDriverLeaderBoard().getTotalDriversCity().getWeek();
					fillUserInfo(cityDriverRankWeekly, cityDriverCustomValueWeekly, totalDriverCityWeek);
				}
			} else if (LBLocationType.GLOBAL == lbLocationType) {
				if (this.lbLocationType != lbLocationType) {
					buttonLocal.setBackgroundResource(R.drawable.background_white_rounded_orange_bordered);
					buttonLocal.setTextColor(getResources().getColorStateList(R.color.textColor));
					buttonGlobal.setBackgroundResource(R.drawable.new_orange_btn_round_corner_normal);
					buttonGlobal.setTextColor(getResources().getColor(R.color.textColorButton));
				}
				if (LBTimeType.DAILY == lbTimeType) {
					leaderboardItems.addAll(newLeaderBoard.getDriverLeaderBoard().getOverallLeaderBoard().getDay());
					int overallDriverRankDaily = newLeaderBoard.getDriverLeaderBoard().getOverallDriverRank().getDay();
					int overallDriverCustomValueDaily = newLeaderBoard.getDriverLeaderBoard().getOverallDriverRank().getDayScore();
					int totalDriverOverallDaily = newLeaderBoard.getDriverLeaderBoard().getTotalDriversOverall().getDay();
					fillUserInfo(overallDriverRankDaily, overallDriverCustomValueDaily, totalDriverOverallDaily);
				} else if (LBTimeType.WEEKLY == lbTimeType) {
					leaderboardItems.addAll(newLeaderBoard.getDriverLeaderBoard().getOverallLeaderBoard().getWeek());
					int overallDriverRankDaily = newLeaderBoard.getDriverLeaderBoard().getOverallDriverRank().getWeek();
					int overallDriverCustomValueDaily = newLeaderBoard.getDriverLeaderBoard().getOverallDriverRank().getWeekScore();
					int totalDriverOverallWeek = newLeaderBoard.getDriverLeaderBoard().getTotalDriversOverall().getWeek();
					fillUserInfo(overallDriverRankDaily, overallDriverCustomValueDaily, totalDriverOverallWeek);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.lbLocationType = lbLocationType;
			this.lbTimeType = lbTimeType;

			leaderboardItemsAdapter.notifyDataSetChanged();
		}
	}

	private void fillUserInfo(int rank, int customColumnValue, int totalRank) {

		if (rank > 5) {
			leaderboardItems.add(new Item(0, Data.userData.userName, "", "", rank,
					customColumnValue, true));
		} else if (rank < 0) {
			leaderboardItems.add(new Item(0, Data.userData.userName, "", "", totalRank,
					customColumnValue, true));
		} else {
			boolean userFound = false;
			for (int i = 0; i < leaderboardItems.size(); i++) {
				Item item = leaderboardItems.get(i);
				if (item.getCityRank() == rank) {
					leaderboardItems.get(i).setIsUser(true);
					userFound = true;
					break;
				}
			}
			if(!userFound){
				leaderboardItems.add(new Item(0, Data.userData.userName, "", "", rank,
						customColumnValue, true));
			}
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(linearLayoutRoot);
		System.gc();
	}

	enum LBLocationType {
		LOCAL, GLOBAL
	}

	enum LBTimeType {
		DAILY, WEEKLY
	}

	public void getLeaderboardCall() {
		try {
			if (!((BaseFragmentActivity)activity).checkIfUserDataNull() && AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put("client_id", "");
				HomeUtil.putDefaultParams(params);
				RestClient.getApiServices().leaderboardServerCall(params,
						new Callback<NewLeaderBoard>() {
							@Override
							public void success(NewLeaderBoard newLeaderBoard, Response response) {
								DialogPopup.dismissLoadingDialog();
								try {
									String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
									Log.i("ShareLeaderbord", jsonString);
									JSONObject jObj;
									jObj = new JSONObject(jsonString);
									int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
									String message = JSONParser.getServerMessage(jObj);
									if (!SplashNewActivity.checkIfTrivialAPIErrors(getActivity(), jObj, flag, null)) {
										if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
											Log.v("success at", "leaderboeard");
											ShareLeaderboardFragment.this.newLeaderBoard = newLeaderBoard;
											update();
										} else {
											retryLeaderboardDialog(message);
										}
									}
								} catch (Exception exception) {
									exception.printStackTrace();
									retryLeaderboardDialog(Data.SERVER_ERROR_MSG);
								}
							}

							@Override
							public void failure(RetrofitError error) {
								DialogPopup.dismissLoadingDialog();
								retryLeaderboardDialog(Data.SERVER_NOT_RESOPNDING_MSG);
							}
						});
			} else {
				retryLeaderboardDialog(Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void retryLeaderboardDialog(String message) {
		try {
			DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message,
					getResources().getString(R.string.retry),
					getResources().getString(R.string.cancel),
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							getLeaderboardCall();
						}
					},
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
						}
					}, true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
