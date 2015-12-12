package product.clicklabs.jugnoo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.Fonts;
import rmn.androidscreenlibrary.ASSL;

public class ItemInfoDescriptionActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	ImageView imageViewItem;
	TextView textViewItemName, textViewItemPrice, textViewItemDescription;
	Button buttonAddToCart;
	
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
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_info_description);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(ItemInfoDescriptionActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Fonts.latoRegular(getApplicationContext()));
		
		imageViewItem = (ImageView) findViewById(R.id.imageViewItem);
		
		textViewItemName = (TextView) findViewById(R.id.textViewItemName); textViewItemName.setTypeface(Fonts.latoRegular(getApplicationContext()));
		textViewItemPrice = (TextView) findViewById(R.id.textViewItemPrice); textViewItemPrice.setTypeface(Fonts.latoRegular(getApplicationContext()));
		textViewItemDescription = (TextView) findViewById(R.id.textViewItemDescription); textViewItemDescription.setTypeface(Fonts.latoRegular(getApplicationContext()));
		
		buttonAddToCart = (Button) findViewById(R.id.buttonAddToCart); buttonAddToCart.setTypeface(Fonts.latoRegular(getApplicationContext()));
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		buttonAddToCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ItemInfosListActivity.selectedItemInfo != null){
					ItemInfosListActivity.itemInfosList.get(ItemInfosListActivity.itemInfosList.indexOf(ItemInfosListActivity.selectedItemInfo)).countSelected++;
				}
				performBackPressed();
			}
		});
		
		if(ItemInfosListActivity.selectedItemInfo != null){
			textViewItemName.setText(ItemInfosListActivity.selectedItemInfo.name);
			textViewItemPrice.setText("Rs. "+ItemInfosListActivity.selectedItemInfo.price);
			textViewItemDescription.setText(ItemInfosListActivity.selectedItemInfo.description);
			
			try{Picasso.with(this).load(ItemInfosListActivity.selectedItemInfo.image).into(imageViewItem);}catch(Exception e){}
		}
		
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
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
}
