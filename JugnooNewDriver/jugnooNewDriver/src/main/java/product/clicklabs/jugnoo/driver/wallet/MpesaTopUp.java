package product.clicklabs.jugnoo.driver.wallet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Prefs;

public class MpesaTopUp extends BaseActivity {

    Button buttonDone;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mpesa_top_up);

        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(Fonts.mavenRegular(this));
        title.setText(R.string.mpesa);

        buttonDone = (Button) findViewById(R.id.btn_done);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isTopUp = Prefs.with(getApplicationContext()).getBoolean("isTopUp", false);
                if(isTopUp){
                    //do top up
                }
                else{
                    //do cash out
                }
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
}
