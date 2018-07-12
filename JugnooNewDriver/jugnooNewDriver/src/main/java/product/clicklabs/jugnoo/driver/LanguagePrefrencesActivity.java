package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.UpdateDriverEarnings;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class LanguagePrefrencesActivity extends BaseActivity {

	ProgressBar progressBar;
	TextView textViewInfoDisplay;
	ListView listView;
	View backBtn;
	TextView title;

	LanguageListAdapter languageListAdapter;
	Configuration conf;

	LinearLayout relative;

	List<String> languages = new ArrayList<>();

	UpdateDriverEarnings updateDriverEarnings;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		languages.clear();

		setContentView(R.layout.activity_language);

		relative = (LinearLayout) findViewById(R.id.relative);

		new ASSL(LanguagePrefrencesActivity.this, relative, 1134, 720, false);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		backBtn = findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title); title.setText(R.string.select_language);
		title.setTypeface(Fonts.mavenMedium(this));
		textViewInfoDisplay = (TextView) findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setTypeface(Fonts.mavenRegular(this));
		listView = (ListView) findViewById(R.id.listView);

		languageListAdapter = new LanguageListAdapter();
		listView.setAdapter(languageListAdapter);

		progressBar.setVisibility(View.GONE);
		textViewInfoDisplay.setVisibility(View.GONE);

		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fetchLangList(LanguagePrefrencesActivity.this);
			}
		});

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApplication.getInstance().logEvent(FirebaseEvents.HOME_SCREEN+"_"+
						FirebaseEvents.LANGUAGE+"_"+FirebaseEvents.BACK,null);
				performBackPressed();
			}
		});

		fetchLangList(this);

	}


	public void updateListData(String message, boolean errorOccurred) {
		if (errorOccurred) {
			textViewInfoDisplay.setText(message);
			textViewInfoDisplay.setVisibility(View.VISIBLE);

			languages.clear();
			languageListAdapter.notifyDataSetChanged();
		} else {
			if (languages.size() == 0) {
				textViewInfoDisplay.setText(message);
				textViewInfoDisplay.setVisibility(View.VISIBLE);
			} else {
				textViewInfoDisplay.setVisibility(View.GONE);
			}
			languageListAdapter.notifyDataSetChanged();
		}
	}


	public void performBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
	}


	class ViewHolderLanguages {
		TextView languageName;
		ImageView imageViewRequestType;
		LinearLayout relative;
		int id;
	}

	class LanguageListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderLanguages holder;

		public LanguageListAdapter() {
			mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return languages.size();
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
				holder = new ViewHolderLanguages();
				convertView = mInflater.inflate(R.layout.list_item_language, null);

				holder.languageName = (TextView) convertView.findViewById(R.id.languageName);
				holder.languageName.setTypeface(Fonts.mavenRegular(getApplicationContext()));

				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);
				holder.relative.setTag(holder);

				holder.relative.setLayoutParams(new ListView.LayoutParams(720, 120));
				ASSL.DoMagic(holder.relative);

				holder.relative.setTag(holder);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderLanguages) convertView.getTag();
			}


			try {
				languages.get(position);
				holder.id = position;
				holder.languageName.setText(languages.get(position));


				holder.relative.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						holder = (ViewHolderLanguages) v.getTag();
						Prefs.with(LanguagePrefrencesActivity.this).save(SPLabels.SELECTED_LANGUAGE, languages.get(holder.id).toString());
						MyApplication.getInstance().logEvent(FirebaseEvents.HOME_SCREEN+"_"+
								FirebaseEvents.LANGUAGE+"_"+languages.get(holder.id).toString()+"_"+position,null);
						BaseFragmentActivity.updateLanguage(LanguagePrefrencesActivity.this,null);
						conf = getResources().getConfiguration();
						setPreferredLanguage();
						finish();
						overridePendingTransition(R.anim.left_in, R.anim.left_out);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}

	}


//	Retrofit

	private void fetchLangList(final Activity activity) {
		progressBar.setVisibility(View.VISIBLE);

		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("device_model_name", android.os.Build.MODEL);
			params.put("android_version", android.os.Build.VERSION.RELEASE);
			params.put("access_token", Data.userData.accessToken);
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().fetchLanguageList(params, new Callback<RegisterScreenResponse>() {
                @Override
                public void success(RegisterScreenResponse registerScreenResponse, Response response) {
                    try {
                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                        JSONObject jObj;
                        jObj = new JSONObject(jsonString);
                        if (!jObj.isNull("error")) {
                            String errorMessage = jObj.getString("error");
                            if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
                                HomeActivity.logoutUser(activity, null);
                            } else {
                                updateListData(activity.getResources().getString(R.string.error_occured_tap_to_retry), true);
                            }
                        } else {

                            JSONArray jArray = jObj.getJSONArray("locales");
                            if (jArray != null) {
								languages.clear();
                                for (int i = 0; i < jArray.length(); i++) {
                                    languages.add(jArray.get(i).toString());
                                }
                            }
                            updateListData(activity.getResources().getString(R.string.no_language_to_select), false);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        updateListData(activity.getResources().getString(R.string.error_occured_tap_to_retry), true);
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void failure(RetrofitError error) {
                    progressBar.setVisibility(View.GONE);
                    updateListData(activity.getResources().getString(R.string.error_occured_tap_to_retry), true);
                }
            });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	private void setPreferredLanguage() {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("locale", conf.locale.toString());
			params.put("access_token", Data.userData.accessToken);
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().setPreferredLang(params, new Callback<RegisterScreenResponse>() {
                @Override
                public void success(RegisterScreenResponse registerScreenResponse, Response response) {
                    try {
                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                        JSONObject jObj;
                        jObj = new JSONObject(jsonString);

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
