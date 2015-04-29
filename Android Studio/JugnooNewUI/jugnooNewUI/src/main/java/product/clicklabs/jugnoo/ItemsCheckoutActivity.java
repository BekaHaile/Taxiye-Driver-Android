package product.clicklabs.jugnoo;

import java.util.ArrayList;

import product.clicklabs.jugnoo.datastructure.ItemInfo;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.model.LatLng;

public class ItemsCheckoutActivity extends Activity implements LocationUpdate{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	TextView textViewUserName, textViewContactNumber, textViewAddress, textViewEdit;
	EditText editTextUserName, editTextContactNumber, editTextAddressLine1, editTextAddressLine2;
	
	TextView textViewProductName, textViewQuantity, textViewPrice;
	
	ListView listViewSelectedItems;
	
	TextView textViewTotal, textViewTotaValue;
	
	SelectedItemsListAdapter selectedItemsListAdapter;
	
	RelativeLayout relativeLayoutContinue;
	TextView textViewContinue;
	
	
	public static ArrayList<ItemInfo> selectedItemInfosList = new ArrayList<ItemInfo>();
	public static LatLng currentLatLng = new LatLng(30.75, 76.78);
	public static String completeAddress = "";
	
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items_checkout);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(ItemsCheckoutActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		
		textViewUserName = (TextView) findViewById(R.id.textViewUserName); textViewUserName.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		textViewContactNumber = (TextView) findViewById(R.id.textViewContactNumber); textViewContactNumber.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		textViewAddress = (TextView) findViewById(R.id.textViewAddress); textViewAddress.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		textViewEdit = (TextView) findViewById(R.id.textViewEdit); textViewEdit.setTypeface(Data.latoRegular(getApplicationContext()));
		
		editTextUserName = (EditText) findViewById(R.id.editTextUserName); editTextUserName.setTypeface(Data.latoRegular(getApplicationContext()));
		editTextContactNumber = (EditText) findViewById(R.id.editTextContactNumber); editTextContactNumber.setTypeface(Data.latoRegular(getApplicationContext()));
		editTextAddressLine1 = (EditText) findViewById(R.id.editTextAddressLine1); editTextAddressLine1.setTypeface(Data.latoRegular(getApplicationContext()));
		editTextAddressLine2 = (EditText) findViewById(R.id.editTextAddressLine2); editTextAddressLine2.setTypeface(Data.latoRegular(getApplicationContext()));
		
		textViewProductName = (TextView) findViewById(R.id.textViewProductName); textViewProductName.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		textViewQuantity = (TextView) findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		textViewPrice = (TextView) findViewById(R.id.textViewPrice); textViewPrice.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		
		listViewSelectedItems = (ListView) findViewById(R.id.listViewSelectedItems);
		
		textViewTotal = (TextView) findViewById(R.id.textViewTotal); textViewTotal.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		textViewTotaValue = (TextView) findViewById(R.id.textViewTotaValue); textViewTotaValue.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		
		selectedItemsListAdapter = new SelectedItemsListAdapter(ItemsCheckoutActivity.this);
		listViewSelectedItems.setAdapter(selectedItemsListAdapter);
		
		relativeLayoutContinue = (RelativeLayout) findViewById(R.id.relativeLayoutContinue);
		textViewContinue = (TextView) findViewById(R.id.textViewContinue); textViewContinue.setTypeface(Data.latoRegular(getApplicationContext()));
		textViewContinue.setText("GET THEM");
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		relativeLayoutContinue.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String addressLine1 = editTextAddressLine1.getText().toString().trim();
				String addressLine2 = editTextAddressLine2.getText().toString().trim();
				if(addressLine1.isEmpty() && addressLine2.isEmpty()){
					editTextAddressLine1.requestFocus();
					editTextAddressLine1.setError("Please fill some Address");
				}
				else{
					completeAddress = "";
					if(addressLine1.isEmpty()){
						completeAddress = addressLine2;
					}
					else if(addressLine2.isEmpty()){
						completeAddress = addressLine1;
					}
					else{
						completeAddress = addressLine1 + ", " +addressLine2;
					}
					if(currentLatLng == null){
						if(Data.locationFetcher == null){
							Data.locationFetcher = new LocationFetcher(ItemsCheckoutActivity.this, 1000, 1);
						}
						currentLatLng = new LatLng(Data.locationFetcher.getLatitude(), Data.locationFetcher.getLongitude());
					}
					startActivity(new Intent(ItemsCheckoutActivity.this, ItemsCheckoutTNCActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
				
			}
		});
		
		
		editTextAddressLine1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				editTextAddressLine1.setError(null);
			}
		});
		
		editTextAddressLine2.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						relativeLayoutContinue.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		if(Data.userData != null){
			editTextUserName.setText(Data.userData.userName);
			editTextContactNumber.setText(Data.userData.phoneNo);
			editTextUserName.setEnabled(false);
			editTextContactNumber.setEnabled(false);
		}
		if(HomeActivity.myLocation != null){
			currentLatLng = new LatLng(HomeActivity.myLocation.getLatitude(), HomeActivity.myLocation.getLongitude());
			getCheckoutAddress(currentLatLng);
		}
		setSelectedItemsToList();
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	}
	
	
	public void setSelectedItemsToList(){
		selectedItemInfosList.clear();
		int totalPrice = 0;
		for(ItemInfo itemInfo : ItemInfosListActivity.itemInfosList){
			if(itemInfo.countSelected > 0){
				selectedItemInfosList.add(itemInfo);
				totalPrice = totalPrice + (itemInfo.price * itemInfo.countSelected);
			}
		}
		textViewTotaValue.setText("Rs. "+totalPrice);
		selectedItemsListAdapter.notifyDataSetChanged();
		Utils.expandListForFixedHeight(listViewSelectedItems);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		try {
			if(Data.locationFetcher == null){
				Data.locationFetcher = new LocationFetcher(this, 1000, 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		HomeActivity.checkForAccessTokenChange(this);
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
	protected void onPause() {
		super.onPause();
		try{
			if(Data.locationFetcher != null){
				Data.locationFetcher.destroy();
				Data.locationFetcher = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		stopCheckoutAddressFetcherThread();
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	
	class ViewHolderSelectedItem {
		TextView textViewItemName, textViewQuantity, textViewPrice;
		LinearLayout relative;
		int id;
	}

	class SelectedItemsListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderSelectedItem holder;
		Context context;
		public SelectedItemsListAdapter(Context context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return selectedItemInfosList.size();
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
				holder = new ViewHolderSelectedItem();
				convertView = mInflater.inflate(R.layout.list_item_selected_item, null);
				
				holder.textViewItemName = (TextView) convertView.findViewById(R.id.textViewItemName); holder.textViewItemName.setTypeface(Data.latoRegular(context));
				holder.textViewQuantity = (TextView) convertView.findViewById(R.id.textViewQuantity); holder.textViewQuantity.setTypeface(Data.latoRegular(context));
				holder.textViewPrice = (TextView) convertView.findViewById(R.id.textViewPrice); holder.textViewPrice.setTypeface(Data.latoRegular(context));
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(660, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderSelectedItem) convertView.getTag();
			}
			
			holder.id = position;
			
			ItemInfo itemInfo = selectedItemInfosList.get(position);
			
			holder.textViewItemName.setText(itemInfo.name);
			holder.textViewQuantity.setText(""+itemInfo.countSelected);
			String totalPrice = "Rs. "+(itemInfo.price * itemInfo.countSelected);
			holder.textViewPrice.setText(totalPrice);
			
			return convertView;
		}
		
	}
	

	
	public void setCheckoutAddress(final String address){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if(address.length() > 40){
						int lastCommaPos = address.substring(0, 40).lastIndexOf(',');
						editTextAddressLine1.setText(""+address.substring(0, lastCommaPos));
						editTextAddressLine2.setText(""+address.substring(lastCommaPos+2, address.length()));
					}
					else{
						editTextAddressLine1.setText(address);
					}
				} catch (Exception e) {
					editTextAddressLine1.setText(address);
					e.printStackTrace();
				}
			}
		});
	}
	
	Thread checkoutAddressFetcherThread;
	public void getCheckoutAddress(final LatLng currentLatLng){
		stopCheckoutAddressFetcherThread();
		try{
			checkoutAddressFetcherThread = new Thread(new Runnable() {
				@Override
				public void run() {
					String address = MapUtils.getGAPIAddress(currentLatLng);
					setCheckoutAddress(address);
					stopCheckoutAddressFetcherThread();
				}
			});
			checkoutAddressFetcherThread.start();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void stopCheckoutAddressFetcherThread(){
		try{
			if(checkoutAddressFetcherThread != null){
				checkoutAddressFetcherThread.interrupt();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		checkoutAddressFetcherThread = null;
	}

	@Override
	public void onLocationChanged(Location location, int priority) {
		currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
	}
	
	
}
