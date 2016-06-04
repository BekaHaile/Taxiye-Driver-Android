package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import rmn.androidscreenlibrary.ASSL;

public class DebugOptionsActivity extends Activity {

	RelativeLayout relative;

	TextView textViewTitle;
	ImageView imageViewBack;


    RelativeLayout relativeLayoutShowAllDrivers, relativeLayoutShowDriverInfo;
    ImageView imageViewShowAllDrivers, imageViewShowDriverInfo;

    RelativeLayout relativeLayoutLive4012, relativeLayoutTest8012, relativeLayoutTest8012_1, relativeLayoutTest8013, relativeLayoutTest8014;
    ImageView imageViewLive4012, imageViewTest8012, imageViewTest8012_1, imageViewTest8013, imageViewTest8014;

    Button buttonSave, buttonCancel;

    int showAllDriversValue = 0;
    int showDriverInfoValue = 0;
    int trainingSelected = 0;

    String selectedServer = Config.getDefaultServerUrl();

	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug_options);

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, (ViewGroup) relative, 1134, 720, false);

		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);


        ((TextView) findViewById(R.id.textViewDebugOptions)).setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewServerEnv)).setTypeface(Fonts.latoRegular(this), Typeface.BOLD);

        relativeLayoutShowAllDrivers = (RelativeLayout) findViewById(R.id.relativeLayoutShowAllDrivers);
        relativeLayoutShowDriverInfo = (RelativeLayout) findViewById(R.id.relativeLayoutShowDriverInfo);

        imageViewShowAllDrivers = (ImageView) findViewById(R.id.imageViewShowAllDrivers);
        imageViewShowDriverInfo = (ImageView) findViewById(R.id.imageViewShowDriverInfo);

        ((TextView) findViewById(R.id.textViewShowAllDrivers)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewShowDriverInfo)).setTypeface(Fonts.latoRegular(this));




        relativeLayoutLive4012 = (RelativeLayout) findViewById(R.id.relativeLayoutLive4012);
        relativeLayoutTest8012 = (RelativeLayout) findViewById(R.id.relativeLayoutTest8012);
        relativeLayoutTest8012_1 = (RelativeLayout) findViewById(R.id.relativeLayoutTest8012_1);
        relativeLayoutTest8013 = (RelativeLayout) findViewById(R.id.relativeLayoutTest8013);
        relativeLayoutTest8014 = (RelativeLayout) findViewById(R.id.relativeLayoutTest8014);

        imageViewLive4012 = (ImageView) findViewById(R.id.imageViewLive4012);
        imageViewTest8012 = (ImageView) findViewById(R.id.imageViewTest8012);
        imageViewTest8012_1 = (ImageView) findViewById(R.id.imageViewTest8012_1);
        imageViewTest8013 = (ImageView) findViewById(R.id.imageViewTest8013);
        imageViewTest8014 = (ImageView) findViewById(R.id.imageViewTest8014);

        ((TextView) findViewById(R.id.textViewLive4012)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewTest8012)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewTest8012_1)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewTest8013)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewTest8014)).setTypeface(Fonts.latoRegular(this));


        buttonSave = (Button) findViewById(R.id.buttonSave); buttonSave.setTypeface(Fonts.latoRegular(this));
        buttonCancel = (Button) findViewById(R.id.buttonCancel); buttonCancel.setTypeface(Fonts.latoRegular(this));



		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


        buttonSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.with(DebugOptionsActivity.this).save(SPLabels.SHOW_ALL_DRIVERS, showAllDriversValue);
                Prefs.with(DebugOptionsActivity.this).save(SPLabels.SHOW_DRIVER_INFO, showDriverInfoValue);
                Prefs.with(DebugOptionsActivity.this).save(SPLabels.SERVER_SELECTED, selectedServer);
                Prefs.with(DebugOptionsActivity.this).save(SPLabels.TRAINING_SELECTED, trainingSelected);
                performBackPressed();
            }
        });

        buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });



        showAllDriversValue = Prefs.with(this).getInt(SPLabels.SHOW_ALL_DRIVERS, 0);
        showDriverInfoValue = Prefs.with(this).getInt(SPLabels.SHOW_DRIVER_INFO, 0);

        selectedServer = Prefs.with(this).getString(SPLabels.SERVER_SELECTED, Config.getDefaultServerUrl());

        trainingSelected = Prefs.with(this).getInt(SPLabels.TRAINING_SELECTED, 0);


        if(showAllDriversValue == 1){
            relativeLayoutShowAllDrivers.setBackgroundColor(Color.WHITE);
            imageViewShowAllDrivers.setImageResource(R.drawable.check_box_checked_new);
        }
        else{
            relativeLayoutShowAllDrivers.setBackgroundColor(Color.TRANSPARENT);
            imageViewShowAllDrivers.setImageResource(R.drawable.check_box_unchecked_new);
        }

        if(showDriverInfoValue == 1){
            relativeLayoutShowDriverInfo.setBackgroundColor(Color.WHITE);
            imageViewShowDriverInfo.setImageResource(R.drawable.check_box_checked_new);
        }
        else{
            relativeLayoutShowDriverInfo.setBackgroundColor(Color.TRANSPARENT);
            imageViewShowDriverInfo.setImageResource(R.drawable.check_box_unchecked_new);
        }

        setServerUI(selectedServer);



        relativeLayoutShowAllDrivers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showAllDriversValue == 0){
                    showAllDriversValue = 1;
                    relativeLayoutShowAllDrivers.setBackgroundColor(Color.WHITE);
                    imageViewShowAllDrivers.setImageResource(R.drawable.check_box_checked_new);
                }
                else{
                    showAllDriversValue = 0;
                    relativeLayoutShowAllDrivers.setBackgroundColor(Color.TRANSPARENT);
                    imageViewShowAllDrivers.setImageResource(R.drawable.check_box_unchecked_new);
                }
            }
        });


        relativeLayoutShowDriverInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showDriverInfoValue == 0){
                    showDriverInfoValue = 1;
                    relativeLayoutShowDriverInfo.setBackgroundColor(Color.WHITE);
                    imageViewShowDriverInfo.setImageResource(R.drawable.check_box_checked_new);
                }
                else{
                    showDriverInfoValue = 0;
                    relativeLayoutShowDriverInfo.setBackgroundColor(Color.TRANSPARENT);
                    imageViewShowDriverInfo.setImageResource(R.drawable.check_box_unchecked_new);
                }
            }
        });



        relativeLayoutLive4012.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServer = Config.getLiveServerUrl();
                setServerUI(selectedServer);
            }
        });

        relativeLayoutTest8012.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServer = Config.getDevServerUrl();
                trainingSelected = 1;
                setServerUI(selectedServer);
            }
        });

        relativeLayoutTest8012_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServer = Config.getDevServerUrl();
                trainingSelected = 0;
                setServerUI(selectedServer);
            }
        });

        relativeLayoutTest8013.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServer = Config.getDev1ServerUrl();
                setServerUI(selectedServer);
            }
        });

        relativeLayoutTest8014.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServer = Config.getDev2ServerUrl();
                setServerUI(selectedServer);
            }
        });


	}


    public void setServerUI(String selectedServer){
        if(selectedServer.equalsIgnoreCase(Config.getLiveServerUrl())){
            relativeLayoutLive4012.setBackgroundColor(Color.WHITE);
            relativeLayoutTest8012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8012_1.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8013.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8014.setBackgroundColor(Color.TRANSPARENT);

            imageViewLive4012.setImageResource(R.drawable.check_box_checked_new);
            imageViewTest8012.setImageResource(R.drawable.check_box_unchecked_new);
            imageViewTest8012_1.setImageResource(R.drawable.check_box_unchecked_new);
            imageViewTest8013.setImageResource(R.drawable.check_box_unchecked_new);
            imageViewTest8014.setImageResource(R.drawable.check_box_unchecked_new);
        }
        else if(selectedServer.equalsIgnoreCase(Config.getDevServerUrl())){
            if(trainingSelected == 1){
                relativeLayoutLive4012.setBackgroundColor(Color.TRANSPARENT);
                relativeLayoutTest8012.setBackgroundColor(Color.WHITE);
                relativeLayoutTest8012_1.setBackgroundColor(Color.TRANSPARENT);
                relativeLayoutTest8013.setBackgroundColor(Color.TRANSPARENT);
                relativeLayoutTest8014.setBackgroundColor(Color.TRANSPARENT);

                imageViewLive4012.setImageResource(R.drawable.check_box_unchecked_new);
                imageViewTest8012.setImageResource(R.drawable.check_box_checked_new);
                imageViewTest8012_1.setImageResource(R.drawable.check_box_unchecked_new);
                imageViewTest8013.setImageResource(R.drawable.check_box_unchecked_new);
                imageViewTest8014.setImageResource(R.drawable.check_box_unchecked_new);
            }
            else{
                relativeLayoutLive4012.setBackgroundColor(Color.TRANSPARENT);
                relativeLayoutTest8012.setBackgroundColor(Color.TRANSPARENT);
                relativeLayoutTest8012_1.setBackgroundColor(Color.WHITE);
                relativeLayoutTest8013.setBackgroundColor(Color.TRANSPARENT);
                relativeLayoutTest8014.setBackgroundColor(Color.TRANSPARENT);

                imageViewLive4012.setImageResource(R.drawable.check_box_unchecked_new);
                imageViewTest8012.setImageResource(R.drawable.check_box_unchecked_new);
                imageViewTest8012_1.setImageResource(R.drawable.check_box_checked_new);
                imageViewTest8013.setImageResource(R.drawable.check_box_unchecked_new);
                imageViewTest8014.setImageResource(R.drawable.check_box_unchecked_new);
            }
        }
        else if(selectedServer.equalsIgnoreCase(Config.getDev1ServerUrl())){
            relativeLayoutLive4012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8012_1.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8013.setBackgroundColor(Color.WHITE);
            relativeLayoutTest8014.setBackgroundColor(Color.TRANSPARENT);

            imageViewLive4012.setImageResource(R.drawable.check_box_unchecked_new);
            imageViewTest8012.setImageResource(R.drawable.check_box_unchecked_new);
            imageViewTest8012_1.setImageResource(R.drawable.check_box_unchecked_new);
            imageViewTest8013.setImageResource(R.drawable.check_box_checked_new);
            imageViewTest8014.setImageResource(R.drawable.check_box_unchecked_new);
        }
        else if(selectedServer.equalsIgnoreCase(Config.getDev2ServerUrl())){
            relativeLayoutLive4012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8012_1.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8013.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8014.setBackgroundColor(Color.WHITE);

            imageViewLive4012.setImageResource(R.drawable.check_box_unchecked_new);
            imageViewTest8012.setImageResource(R.drawable.check_box_unchecked_new);
            imageViewTest8012_1.setImageResource(R.drawable.check_box_unchecked_new);
            imageViewTest8013.setImageResource(R.drawable.check_box_unchecked_new);
            imageViewTest8014.setImageResource(R.drawable.check_box_checked_new);
        }
    }
    



	
	public void performBackPressed(){
        startActivity(new Intent(this, SplashNewActivity.class));
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	

	
}
