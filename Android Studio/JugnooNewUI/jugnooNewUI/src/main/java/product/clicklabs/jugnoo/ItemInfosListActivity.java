package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.ItemInfo;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;

public class ItemInfosListActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn, infoBtn;
	TextView title;
	
	TextView textViewInfo;
	ProgressBar progressBar;
	
	ListView listView;
	ItemInfoListAdapter itemInfoListAdapter;
	
	RelativeLayout relativeLayoutCheckout;
	TextView textViewSelectedItemsCount, textViewCheckout;
	
	
	AsyncHttpClient fetchItemInfosClient;
	
	public static ArrayList<ItemInfo> itemInfosList = new ArrayList<ItemInfo>();
	public static String terms = "";
	public static ItemInfo selectedItemInfo = null;
	
	//TODO
	public static String SERVER_URL = Config.getServerUrl().substring(0, Config.getServerUrl().length() - 4)+"5000";
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Config.getFlurryKey());
		FlurryAgent.onStartSession(this, Config.getFlurryKey());
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items_list);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(ItemInfosListActivity.this, relative, 1134, 720, false);
		
		SERVER_URL = Config.getServerUrl().substring(0, Config.getServerUrl().length() - 4)+"5000";
		
		
		backBtn = (Button) findViewById(R.id.backBtn);
		infoBtn = (Button) findViewById(R.id.infoBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Fonts.latoRegular(getApplicationContext()));
		
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Fonts.latoRegular(getApplicationContext()));
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		itemInfosList.clear();
		
		listView = (ListView) findViewById(R.id.listView);
		itemInfoListAdapter = new ItemInfoListAdapter(ItemInfosListActivity.this);
		listView.setAdapter(itemInfoListAdapter);
		
		relativeLayoutCheckout = (RelativeLayout) findViewById(R.id.relativeLayoutCheckout);
		
		textViewSelectedItemsCount = (TextView) findViewById(R.id.textViewSelectedItemsCount); textViewSelectedItemsCount.setTypeface(Fonts.latoRegular(getApplicationContext()));
		textViewCheckout = (TextView) findViewById(R.id.textViewCheckout); textViewCheckout.setTypeface(Fonts.latoRegular(getApplicationContext()));
		
		textViewInfo.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		infoBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!"".equalsIgnoreCase(terms)){
					Intent intent = new Intent(ItemInfosListActivity.this, ItemsCheckoutTNCActivity.class);
					intent.putExtra("only_info", "yes");
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			}
		});
		
		relativeLayoutCheckout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int totalItems = updateCheckoutItemsCount(false);
				if(totalItems > 0){
					startActivity(new Intent(ItemInfosListActivity.this, ItemsCheckoutActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
				else{
					Toast.makeText(ItemInfosListActivity.this, "Please select some items first.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		textViewInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getItemInfosAsync(ItemInfosListActivity.this);
			}
		});
		
		
		
		getItemInfosAsync(ItemInfosListActivity.this);
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		itemInfoListAdapter.notifyDataSetChanged();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	public int updateCheckoutItemsCount(boolean setText){
		int totalCount = 0;
		for(ItemInfo itemInfo : itemInfosList){
			if(itemInfo.countSelected > 0){
				totalCount++;
			}
		}
		if(setText){
			if(totalCount > 0){
				textViewSelectedItemsCount.setText(""+totalCount);
			}
			else{
				textViewSelectedItemsCount.setText("0");
			}
		}
		return totalCount;
	}
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	
	
	@Override
	public void onDestroy() {
		if(fetchItemInfosClient != null){
			fetchItemInfosClient.cancelAllRequests(true);
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			textViewInfo.setText(message);
			textViewInfo.setVisibility(View.VISIBLE);
			
			itemInfosList.clear();
			itemInfoListAdapter.notifyDataSetChanged();
		}
		else{
			if(itemInfosList.size() == 0){
				textViewInfo.setText(message);
				textViewInfo.setVisibility(View.VISIBLE);
			}
			else{
				textViewInfo.setVisibility(View.GONE);
			}
			itemInfoListAdapter.notifyDataSetChanged();
		}
	}
	
	
	class ViewHolderItemInfo {
		TextView textViewItemName, textViewItemPrice, textViewItemCount;
		ImageView imageViewItem, imageViewMinus, imageViewPlus;
		LinearLayout relative;
		int id;
	}

	class ItemInfoListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderItemInfo holder;
		Context context;
		public ItemInfoListAdapter(Context context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return itemInfosList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderItemInfo();
				convertView = mInflater.inflate(R.layout.list_item_item_info, null);
				
				holder.textViewItemName = (TextView) convertView.findViewById(R.id.textViewItemName); holder.textViewItemName.setTypeface(Fonts.latoRegular(context), Typeface.BOLD);
				holder.textViewItemPrice = (TextView) convertView.findViewById(R.id.textViewItemPrice); holder.textViewItemPrice.setTypeface(Fonts.latoRegular(context));
				holder.textViewItemCount = (TextView) convertView.findViewById(R.id.textViewItemCount); holder.textViewItemCount.setTypeface(Fonts.latoRegular(context));
				
				holder.imageViewItem = (ImageView) convertView.findViewById(R.id.imageViewItem);
				holder.imageViewMinus = (ImageView) convertView.findViewById(R.id.imageViewMinus);
				holder.imageViewPlus = (ImageView) convertView.findViewById(R.id.imageViewPlus);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.imageViewMinus.setTag(holder);
				holder.imageViewPlus.setTag(holder);
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderItemInfo) convertView.getTag();
			}
			
			holder.id = position;
			
			ItemInfo itemInfo = itemInfosList.get(position);
			
			holder.textViewItemName.setText(itemInfo.name);
			holder.textViewItemPrice.setText("Rs. "+itemInfo.price);
			
			
			try{Picasso.with(context).load(itemInfo.image).placeholder(R.drawable.item_placeholder).into(holder.imageViewItem);}catch(Exception e){}
			
			if(itemInfo.countSelected > 0){
				holder.textViewItemCount.setText(""+itemInfo.countSelected);
			}
			else{
				holder.textViewItemCount.setText("-");
			}
			
			
			holder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderItemInfo) v.getTag();
					if(itemInfosList.get(holder.id).countSelected > 0){
						itemInfosList.get(holder.id).countSelected--;
						notifyDataSetChanged();
					}
				}
			});
			
			holder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderItemInfo) v.getTag();
					if(itemInfosList.get(holder.id).countSelected < 99){
						itemInfosList.get(holder.id).countSelected++;
						notifyDataSetChanged();
					}
				}
			});
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderItemInfo) v.getTag();
					sendIntentToDescription(itemInfosList.get(holder.id));
				}
			});
			
			return convertView;
		}

		
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			updateCheckoutItemsCount(true);
		}
		
	}
	
	
	public void sendIntentToDescription(ItemInfo itemInfo){
		selectedItemInfo = itemInfo;
		startActivity(new Intent(ItemInfosListActivity.this, ItemInfoDescriptionActivity.class));
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}
	
	/**
	 * ASync for get Account info from server
	 */
	public void getItemInfosAsync(final Activity activity) {
		if(fetchItemInfosClient == null){
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				progressBar.setVisibility(View.VISIBLE);
				itemInfosList.clear();
				itemInfoListAdapter.notifyDataSetChanged();
				textViewInfo.setVisibility(View.GONE);
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				fetchItemInfosClient = Data.getClient();
				fetchItemInfosClient.post(ItemInfosListActivity.SERVER_URL + "/list_all_items", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								progressBar.setVisibility(View.GONE);
								updateListData("Some error occurred. Tap to retry", true);
							}
	
							@Override
							public void onSuccess(String response) {
								Log.e("Server response", "response = " + response);
								try {
									jObj = new JSONObject(response);
									if(!jObj.isNull("error")){
										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											updateListData("Some error occurred. Tap to retry", true);
										}
									}
									else{
										
//										{
//										    "coupons": [
//										        {
//										            "title": "Free ride",
//										            "description": "upte 100/-",
//										            "discount": 100,
//										            "maximum": 100,
//										            "image": null,
//										            "type": 0,
//										            "redeemed_on": null,
//										            "status": 1,
//										            "expiry_date": "2014-11-07T18:29:59.000Z"
//										        }
//										    ]
//										}
										
										itemInfosList.clear();
										
										if(jObj.has("flag")){
											int flag = jObj.getInt("flag");
											if(ApiResponseFlags.COMPLETE_INVENTORY.getOrdinal() == flag){
												if(jObj.has("inventory")){
													JSONArray itemsData = jObj.getJSONArray("inventory");
													if(itemsData.length() > 0){
														for(int i=0; i<itemsData.length(); i++){
															JSONObject itemData = itemsData.getJSONObject(i);
															itemInfosList.add(new ItemInfo(itemData.getInt("item_id"), itemData.getString("name"), itemData.getInt("price"), 
																	itemData.getString("description"), itemData.getString("image")));
														}
													}
												}
												if(jObj.has("terms")){
													terms = jObj.getString("terms");
												}
											}
										}
										updateListData("No items", false);
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									updateListData("Some error occurred. Tap to retry", true);
								}
								progressBar.setVisibility(View.GONE);
							}
							
							@Override
							public void onFinish() {
								fetchItemInfosClient = null;
								super.onFinish();
							}
							
						});
			}
			else {
				updateListData("No Internet connection. Tap to retry", true);
			}
		}

	}

	
}
