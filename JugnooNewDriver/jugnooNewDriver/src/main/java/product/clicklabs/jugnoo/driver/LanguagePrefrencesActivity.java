package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.driver.datastructure.LanguageInfo;
import product.clicklabs.jugnoo.driver.utils.ASSL;

public class LanguagePrefrencesActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	ListView languageList;
	
	LanguageListAdapter languageListAdapter;
	
	ArrayList<LanguageInfo> languageInfos = new ArrayList<LanguageInfo>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_language);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(LanguagePrefrencesActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		
		languageList = (ListView) findViewById(R.id.languageList);
		languageListAdapter = new LanguageListAdapter();
		
		languageList.setAdapter(languageListAdapter);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		
		languageInfos.clear();
		
		
		SharedPreferences preferences = getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
		String languageSelected = preferences.getString(Data.LANGUAGE_SELECTED, "default");
		
		if("default".equalsIgnoreCase(languageSelected)){
			Locale locale = Locale.getDefault();
			if("hi".equalsIgnoreCase(locale.getLanguage())){
				languageInfos.add(new LanguageInfo("en", getResources().getString(R.string.language_english_text), false));
				languageInfos.add(new LanguageInfo("hi", getResources().getString(R.string.language_hindi_text), true));
			}
			else{
				languageInfos.add(new LanguageInfo("en", getResources().getString(R.string.language_english_text), true));
				languageInfos.add(new LanguageInfo("hi", getResources().getString(R.string.language_hindi_text), false));
			}
		}
		else{
			if("hi".equalsIgnoreCase(languageSelected)){
				languageInfos.add(new LanguageInfo("en", getResources().getString(R.string.language_english_text), false));
				languageInfos.add(new LanguageInfo("hi", getResources().getString(R.string.language_hindi_text), true));
			}
			else{
				languageInfos.add(new LanguageInfo("en", getResources().getString(R.string.language_english_text), true));
				languageInfos.add(new LanguageInfo("hi", getResources().getString(R.string.language_hindi_text), false));
			}
		}
		
		
		
		
		languageListAdapter.notifyDataSetChanged();
		
	}
	
	void refreshPage(){
		title.setText(getResources().getString(R.string.select_language));
	}
	
	
	
	class ViewHolderLanguage {
		TextView languageName;
		ImageView tick;
		LinearLayout relative;
		int id;
	}

	class LanguageListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderLanguage holder;

		public LanguageListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return languageInfos.size();
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
				
				holder = new ViewHolderLanguage();
				convertView = mInflater.inflate(R.layout.list_item_language, null);
				
				holder.languageName = (TextView) convertView.findViewById(R.id.languageName); holder.languageName.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.tick = (ImageView) convertView.findViewById(R.id.tick);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, 127));
				ASSL.DoMagic(holder.relative);
				
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderLanguage) convertView.getTag();
			}
			
			
			holder.id = position;
			
			holder.languageName.setText(languageInfos.get(position).displayName);
			
			if(languageInfos.get(position).selected){
				holder.tick.setImageResource(R.drawable.check_on);
			}
			else{
				holder.tick.setImageResource(R.drawable.check_off);
			}
			
			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderLanguage) v.getTag();
					
					for(int i=0; i<languageInfos.size(); i++){
						languageInfos.get(i).selected = false;
					}
					
					languageInfos.get(holder.id).selected = true;
					
					Locale locale = new Locale(languageInfos.get(holder.id).name); 
				    Locale.setDefault(locale);
				    Configuration config = new Configuration();
				    config.locale = locale;
				    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
				    
					
				    SharedPreferences.Editor editor = getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0).edit();
				    editor.putString(Data.LANGUAGE_SELECTED, languageInfos.get(holder.id).name);
				    editor.commit();
				    
					
					notifyDataSetChanged();
					
					refreshPage();
				}
			});
			
			
			
			return convertView;
		}

	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
}
