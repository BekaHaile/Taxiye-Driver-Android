package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;

public class BlankActivityForDialog extends Activity{

	LinearLayout relative;

    String message = "";



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blank_for_dialog);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(BlankActivityForDialog.this, relative, 1134, 720, false);

        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(getIntent().hasExtra("wakeUp")){
			Intent homeScreen = new Intent(this, SplashNewActivity.class);
			startActivity(homeScreen);
			finish();
        }

        if(getIntent().hasExtra("message1")){
            message = getIntent().getStringExtra("message1");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    DialogPopup.alertPopupWithListener(BlankActivityForDialog.this, "", message,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            });
                }
            }, 200);
        }
        else if(getIntent().hasExtra("message2")){
            message = getIntent().getStringExtra("message2");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    DialogPopup.dialogBanner(BlankActivityForDialog.this, message);
                }
            }, 200);
        }
        else{
            finish();
        }




	}


	
	@Override
	public void onBackPressed() {
        super.onBackPressed();
	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Toast.makeText(BlankActivityForDialog.this, "Blank", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
}