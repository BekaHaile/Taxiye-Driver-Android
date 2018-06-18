package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.datastructure.HelpSection;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;


public class AboutActivity extends BaseActivity {

    RelativeLayout relative;

    TextView textViewTitle;
    Button imageViewBack;

    RelativeLayout relativeLayoutRateUs, relativeLayoutLikeUs, relativeLayoutTNC, relativeLayoutPrivacy, relativeLayoutAbout;
    TextView textViewRateUs, textViewLikeUs, textViewTNC, textViewPrivacy, textViewAbout;


    String facebookPageId;
    String facebookPageName;
    private final String  TAG = "About";
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        facebookPageId = getString(R.string.facebook_page_id);
        facebookPageName = getString(R.string.facebook_page_name);
        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, (ViewGroup) relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.mavenMedium(this));
        imageViewBack = (Button) findViewById(R.id.backBtn);

        relativeLayoutRateUs = (RelativeLayout) findViewById(R.id.relativeLayoutRateUs);
        relativeLayoutLikeUs = (RelativeLayout) findViewById(R.id.relativeLayoutLikeUs);
        relativeLayoutTNC = (RelativeLayout) findViewById(R.id.relativeLayoutTNC);
        relativeLayoutPrivacy = (RelativeLayout) findViewById(R.id.relativeLayoutPrivacy);
        relativeLayoutAbout = (RelativeLayout) findViewById(R.id.relativeLayoutWho);

        textViewRateUs = (TextView) findViewById(R.id.textViewRateUs);
        textViewRateUs.setTypeface(Fonts.mavenLight(this));
        textViewLikeUs = (TextView) findViewById(R.id.textViewLike);
        textViewLikeUs.setTypeface(Fonts.mavenLight(this));
        textViewTNC = (TextView) findViewById(R.id.textViewTNC);
        textViewTNC.setTypeface(Fonts.mavenLight(this));
        textViewPrivacy = (TextView) findViewById(R.id.textViewPrivacy);
        textViewPrivacy.setTypeface(Fonts.mavenLight(this));
        textViewAbout = (TextView) findViewById(R.id.textViewWho);
        textViewAbout.setTypeface(Fonts.mavenLight(this));

        relativeLayoutRateUs.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID));
                startActivity(intent);
            }
        });

        relativeLayoutLikeUs.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent intent;
                    if (Utils.appInstalledOrNot(AboutActivity.this, "com.facebook.katana")) {
                        try {
                            getPackageManager().getPackageInfo("com.facebook.katana", 0);
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + facebookPageId));
                        } catch (Exception e) {
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + facebookPageName));
                        }
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.facebook.com/" + facebookPageName));
                    }
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.showToast(AboutActivity.this, getString(R.string.facebook_not_installed));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.facebook.com/" + facebookPageName));
                    startActivity(intent);
                }
            }
        });

        relativeLayoutTNC.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpParticularActivity.helpSection = HelpSection.TERMS;
                startActivity(new Intent(AboutActivity.this, HelpParticularActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        relativeLayoutPrivacy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpParticularActivity.helpSection = HelpSection.PRIVACY;
                startActivity(new Intent(AboutActivity.this, HelpParticularActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        relativeLayoutAbout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpParticularActivity.helpSection = HelpSection.ABOUT;
                startActivity(new Intent(AboutActivity.this, HelpParticularActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });


        imageViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });
    }


    public void performBackPressed() {
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
