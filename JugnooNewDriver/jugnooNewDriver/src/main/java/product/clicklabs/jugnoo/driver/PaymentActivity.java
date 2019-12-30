package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.adapters.PaymentFragmentAdapter;
import product.clicklabs.jugnoo.driver.fragments.InvoiceHistoryFragment;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.widgets.PagerSlidingTabStrip;


public class PaymentActivity extends BaseFragmentActivity implements FlurryEventNames {
	
	LinearLayout linearLayoutRoot;

	View backBtn;
	TextView title;
	RelativeLayout relativeContainer;
	ViewPager viewPager;
	PaymentFragmentAdapter paymentFragmentAdapter;
	Shader textShader;
	PagerSlidingTabStrip tabs;
	InvoiceHistoryFragment invoiceHistoryFragment;
	@Override
	protected void onStart() {
		super.onStart();


	}

	@Override
	protected void onStop() {
		super.onStop();

	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_payments);

		linearLayoutRoot = (LinearLayout) findViewById(R.id.linearLayoutRoot);
		new ASSL(PaymentActivity.this, linearLayoutRoot, 1134, 720, false);
		relativeContainer = (RelativeLayout) findViewById(R.id.relativeContainer);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		paymentFragmentAdapter = new PaymentFragmentAdapter(PaymentActivity.this, getSupportFragmentManager());
		viewPager.setAdapter(paymentFragmentAdapter);
		invoiceHistoryFragment = new InvoiceHistoryFragment();
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setIndicatorColor(getResources().getColor(R.color.themeColor));
		tabs.setTextColorResource(R.color.themeColor, R.color.menu_black);
		tabs.setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		tabs.setViewPager(viewPager);

		backBtn =  findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		title.setText(R.string.Invoices_cap);

//		textShader=new LinearGradient(0, 0, 0, 20,
//				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
//				new float[]{0, 1}, Shader.TileMode.CLAMP);
//		textViewTitle.getPaint().setShader(textShader);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.relativeContainer, invoiceHistoryFragment, InvoiceHistoryFragment.class.getName())
				.addToBackStack(InvoiceHistoryFragment.class.getName())
				.commit();


		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performbackPressed();
			}
		});

		try {
			if(getIntent().getExtras().getInt("trick_page") ==1){
				viewPager.setCurrentItem(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if(position == 1){
				} else if(position == 2){
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});


	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void performbackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		performbackPressed();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(linearLayoutRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();


	}
}
