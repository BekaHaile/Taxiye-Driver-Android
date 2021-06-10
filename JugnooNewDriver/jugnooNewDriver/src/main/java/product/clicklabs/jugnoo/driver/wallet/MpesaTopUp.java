package product.clicklabs.jugnoo.driver.wallet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Prefs;

public class MpesaTopUp extends BaseActivity {

    Button buttonDone, fiftyButton, hundredButton, hundredFiftyButton;
    ImageView backBtn;
    boolean isTopUp;
    EditText editAmount;
    String currency = Data.userData.getCurrency() + " %s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mpesa_top_up);

        isTopUp = Prefs.with(getApplicationContext()).getBoolean("isTopUp", false);

        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(Fonts.mavenRegular(this));
        title.setText(R.string.mpesa);

        editAmount = (EditText) findViewById(R.id.editAmount);

        fiftyButton = (Button) findViewById(R.id.fiftyButton);
        fiftyButton.setText(String.format(currency, "50"));
        fiftyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAmount.setText("50");
            }
        });

        hundredButton = (Button) findViewById(R.id.hundredButton);
        hundredButton.setText(String.format(currency, "100"));
        hundredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAmount.setText("100");
            }
        });

        hundredFiftyButton = (Button) findViewById(R.id.hundredFiftyButton);
        hundredFiftyButton.setText(String.format(currency, "300"));
        hundredFiftyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAmount.setText("300");
            }
        });

        buttonDone = (Button) findViewById(R.id.btn_done);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
