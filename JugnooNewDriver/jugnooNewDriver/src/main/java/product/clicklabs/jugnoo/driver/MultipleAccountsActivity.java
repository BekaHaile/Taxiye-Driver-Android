package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.EnglishNumberToWords;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class MultipleAccountsActivity extends BaseActivity {

	LinearLayout relative;

	TextView textViewTitle;
	ImageView imageViewBack;

    ScrollView scrollView;

    TextView textViewMultipleAccountsCreated, textViewPleaseLogin;
    ListView listViewPreviousAccounts;
    TextView textViewLikeToCreate;
    RelativeLayout relativeLayoutMailUs;
    TextView textViewContactUs, textViewMailUs;

    PreviousAccountsAdapter previousAccountsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiple_accounts);


		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);

		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        textViewMultipleAccountsCreated = (TextView) findViewById(R.id.textViewMultipleAccountsCreated); textViewMultipleAccountsCreated.setTypeface(Fonts.mavenRegular(this));
        textViewPleaseLogin = (TextView) findViewById(R.id.textViewPleaseLogin); textViewPleaseLogin.setTypeface(Fonts.mavenLight(this), Typeface.BOLD);

        listViewPreviousAccounts = (ListView) findViewById(R.id.listViewPreviousAccounts);

        textViewLikeToCreate = (TextView) findViewById(R.id.textViewLikeToCreate); textViewLikeToCreate.setTypeface(Fonts.mavenRegular(this));

        relativeLayoutMailUs = (RelativeLayout) findViewById(R.id.relativeLayoutMailUs);
        textViewContactUs = (TextView) findViewById(R.id.textViewContactUs);
		textViewContactUs.setTypeface(Fonts.mavenLight(this), Typeface.BOLD);
		textViewContactUs.setText(getStringText(R.string.please_contact_customer_care));

        textViewMailUs = (TextView) findViewById(R.id.textViewMailUs);
		textViewMailUs.setTypeface(Fonts.mavenRegular(this));


        previousAccountsAdapter = new PreviousAccountsAdapter(this);
        listViewPreviousAccounts.setAdapter(previousAccountsAdapter);



		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


        relativeLayoutMailUs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MultipleAccountsActivity.this, RequestDuplicateRegistrationActivity.class));
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });


        previousAccountsAdapter.notifyDataSetChanged();
        Utils.expandListForFixedHeight(listViewPreviousAccounts);

        scrollView.smoothScrollTo(0,0);

        try {
            textViewMultipleAccountsCreated.setText(EnglishNumberToWords.convert(Data.previousAccountInfoList.size()) + getResources().getString(R.string.no_of_account));
        } catch(Exception e){
            e.printStackTrace();
            performBackPressed();
        }

    }

	
	
	public void performBackPressed(){
        Intent intent = new Intent(MultipleAccountsActivity.this, SplashNewActivity.class);
        intent.putExtra("back_from_otp", true);
        startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}
	
	

	
	class ViewHolderPreviousAccount {
		TextView textViewAccountEmail, textViewAccountPhone, textViewLogin;
		LinearLayout relative;
		int id;
	}

	class PreviousAccountsAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderPreviousAccount holder;
		Context context;
		public PreviousAccountsAdapter(Context context) {
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;
		}

		@Override
		public int getCount() {
			return Data.previousAccountInfoList.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderPreviousAccount();
				convertView = mInflater.inflate(R.layout.list_item_previous_account, null);
				
				holder.textViewAccountEmail = (TextView) convertView.findViewById(R.id.textViewAccountEmail); holder.textViewAccountEmail.setTypeface(Fonts.mavenRegular(context));
				holder.textViewAccountPhone = (TextView) convertView.findViewById(R.id.textViewAccountPhone); holder.textViewAccountPhone.setTypeface(Fonts.mavenLight(context), Typeface.BOLD);
				holder.textViewLogin = (TextView) convertView.findViewById(R.id.textViewLogin); holder.textViewLogin.setTypeface(Fonts.mavenRegular(context));

				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);

				holder.relative.setTag(holder);

				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderPreviousAccount) convertView.getTag();
			}
			
			
			holder.id = position;

            holder.textViewAccountEmail.setText(Data.previousAccountInfoList.get(position).userEmail);
            holder.textViewAccountPhone.setText(Utils.hidePhoneNoString(Data.previousAccountInfoList.get(position).phoneNo));


			holder.relative.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder = (ViewHolderPreviousAccount) v.getTag();
                    String previousEmail = Data.previousAccountInfoList.get(holder.id).userEmail;
                    Intent intent = new Intent(MultipleAccountsActivity.this, LoginViaOTP.class);
                    intent.putExtra("previous_login_email", previousEmail);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }
            });
			
			
			return convertView;
		}


		
	}


}
