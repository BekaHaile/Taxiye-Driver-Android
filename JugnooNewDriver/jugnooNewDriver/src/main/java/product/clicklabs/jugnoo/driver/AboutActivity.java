package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.datastructure.HelpSection;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;


public class AboutActivity extends BaseActivity {

    RelativeLayout relative;

    TextView title;
    ImageView backBtn;

    RelativeLayout relativeLayoutRateUs, relativeLayoutLikeUs, relativeLayoutTNC, relativeLayoutPrivacy,
            relativeLayoutAbout, relativeLayoutFAQ, relativeLayoutDriverAgreement;
    TextView textViewRateUs, textViewLikeUs, textViewTNC, textViewPrivacy, textViewAbout;


    String facebookPageId;
    String facebookPageUrl;
    private final String  TAG = "About";
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        facebookPageId = Prefs.with(this).getString(Constants.KEY_FACEBOOK_PAGE_ID, getString(R.string.facebook_page_id));
        facebookPageUrl = Prefs.with(this).getString(Constants.KEY_FACEBOOK_PAGE_URL, getString(R.string.facebook_page_url));
        relative = (RelativeLayout) findViewById(R.id.relative);

        title = (TextView) findViewById(R.id.title);
        title.setText(R.string.about);
        backBtn = (ImageView) findViewById(R.id.backBtn);

        relativeLayoutRateUs = (RelativeLayout) findViewById(R.id.relativeLayoutRateUs);
        relativeLayoutLikeUs = (RelativeLayout) findViewById(R.id.relativeLayoutLikeUs);
        relativeLayoutTNC = (RelativeLayout) findViewById(R.id.relativeLayoutTNC);
        relativeLayoutPrivacy = (RelativeLayout) findViewById(R.id.relativeLayoutPrivacy);
        relativeLayoutAbout = (RelativeLayout) findViewById(R.id.relativeLayoutWho);
        relativeLayoutFAQ = (RelativeLayout) findViewById(R.id.relativeLayoutFAQ);
        relativeLayoutDriverAgreement = (RelativeLayout) findViewById(R.id.relativeLayoutDriverAgreement);

        textViewRateUs = (TextView) findViewById(R.id.textViewRateUs);
        textViewLikeUs = (TextView) findViewById(R.id.textViewLike);
        textViewTNC = (TextView) findViewById(R.id.textViewTNC);
        textViewPrivacy = (TextView) findViewById(R.id.textViewPrivacy);
        textViewAbout = (TextView) findViewById(R.id.textViewWho);

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
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + facebookPageUrl));
                        } catch (Exception e) {
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookPageUrl));
                        }
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(facebookPageUrl));
                    }
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.showToast(AboutActivity.this, getString(R.string.facebook_not_installed));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(facebookPageUrl));
                    startActivity(intent);
                }
            }
        });
        relativeLayoutLikeUs.setVisibility(Prefs.with(this).getInt(Constants.KEY_FACEBOOK_LIKE_ENABLED, 1) == 1 ? View.VISIBLE : View.GONE);
        relativeLayoutFAQ.setVisibility(Prefs.with(this).getInt(Constants.KEY_SHOW_FAQ, 1) == 1 ? View.VISIBLE : View.GONE);
        relativeLayoutDriverAgreement.setVisibility(Prefs.with(this).getInt(Constants.KEY_SHOW_DRIVER_AGREEMENT, 1) == 1 ? View.VISIBLE : View.GONE);

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

        relativeLayoutFAQ.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpParticularActivity.helpSection = HelpSection.FAQ;
                startActivity(new Intent(AboutActivity.this, HelpParticularActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        relativeLayoutDriverAgreement.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpParticularActivity.helpSection = HelpSection.DRIVER_AGREEMENT;
                startActivity(new Intent(AboutActivity.this, HelpParticularActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });


        backBtn.setOnClickListener(new OnClickListener() {

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
        System.gc();
        super.onDestroy();
    }

}
