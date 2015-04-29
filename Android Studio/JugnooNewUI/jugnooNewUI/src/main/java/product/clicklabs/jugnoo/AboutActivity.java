package product.clicklabs.jugnoo;

import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {

    RelativeLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

    RelativeLayout relativeLayoutRateUs, relativeLayoutLikeUs, relativeLayoutTNC, relativeLayoutPrivacy, relativeLayoutAbout;
    TextView textViewRateUs, textViewLikeUs, textViewTNC, textViewPrivacy, textViewAbout;


    String facebookPageId = "252184564966458";
    String facebookPageName = "ridejugnoo";

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, (ViewGroup) relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        relativeLayoutRateUs = (RelativeLayout) findViewById(R.id.relativeLayoutRateUs);
        relativeLayoutLikeUs = (RelativeLayout) findViewById(R.id.relativeLayoutLikeUs);
        relativeLayoutTNC = (RelativeLayout) findViewById(R.id.relativeLayoutTNC);
        relativeLayoutPrivacy = (RelativeLayout) findViewById(R.id.relativeLayoutPrivacy);
        relativeLayoutAbout = (RelativeLayout) findViewById(R.id.relativeLayoutAbout);

        textViewRateUs = (TextView) findViewById(R.id.textViewRateUs);
        textViewRateUs.setTypeface(Data.latoRegular(this));
        textViewLikeUs = (TextView) findViewById(R.id.textViewLikeUs);
        textViewLikeUs.setTypeface(Data.latoRegular(this));
        textViewTNC = (TextView) findViewById(R.id.textViewTNC);
        textViewTNC.setTypeface(Data.latoRegular(this));
        textViewPrivacy = (TextView) findViewById(R.id.textViewPrivacy);
        textViewPrivacy.setTypeface(Data.latoRegular(this));
        textViewAbout = (TextView) findViewById(R.id.textViewAbout);
        textViewAbout.setTypeface(Data.latoRegular(this));


        relativeLayoutRateUs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=product.clicklabs.jugnoo"));
                startActivity(intent);
            }
        });

        relativeLayoutLikeUs.setOnClickListener(new View.OnClickListener() {

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
                    Toast.makeText(AboutActivity.this, "Facebook app not enabled", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.facebook.com/" + facebookPageName));
                    startActivity(intent);
                }
            }
        });

        relativeLayoutTNC.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpParticularActivity.helpSection = HelpSection.TERMS;
                startActivity(new Intent(AboutActivity.this, HelpParticularActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        relativeLayoutPrivacy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpParticularActivity.helpSection = HelpSection.PRIVACY;
                startActivity(new Intent(AboutActivity.this, HelpParticularActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        relativeLayoutAbout.setOnClickListener(new View.OnClickListener() {

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
