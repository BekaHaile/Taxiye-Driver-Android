package product.clicklabs.jugnoo.driver.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Prefs;

public class TopUpOptionSelector extends BaseActivity {

    TextView cbeBirrOption, mpesaOption, helloCashOption;
    ImageView backBtn;
    boolean isTopUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallet_top_up);

        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(Fonts.mavenRegular(this));

        isTopUp = Prefs.with(this).getBoolean("isTopUp", false);
        if(isTopUp)
            title.setText(R.string.top_up_wallet);
        else title.setText(R.string.cash_out_from_wallet);

        cbeBirrOption = (TextView) findViewById(R.id.cbeBirrOption);
        cbeBirrOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCbeTopUpActivity();
            }
        });

        mpesaOption = (TextView) findViewById(R.id.mpesaOption);
        mpesaOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMpesaTopUpActivity();
            }
        });

        helloCashOption = (TextView) findViewById(R.id.helloCashOption);
        helloCashOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHelloCashTopUpActivity();
            }
        });

        backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
    }

    public void openCbeTopUpActivity(){
        startActivity(new Intent(TopUpOptionSelector.this, CbeTopUp.class));
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public void openMpesaTopUpActivity(){
        startActivity(new Intent(TopUpOptionSelector.this, MpesaTopUp.class));
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public void openHelloCashTopUpActivity(){
        startActivity(new Intent(TopUpOptionSelector.this, HelloCash.class));
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}


