package product.clicklabs.jugnoo.driver.wallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.picker.Country;
import com.picker.CountryPicker;
import com.picker.OnCountryPickerListener;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import product.clicklabs.jugnoo.driver.wallet.model.CbeBirrCashoutResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MpesaTopUp extends BaseActivity implements OnCountryPickerListener<Country> {

    Button buttonDone, fiftyButton, hundredButton, hundredFiftyButton;
    ImageView backBtn;
    boolean isTopUp;
    EditText editAmount, edtPhoneNo;
    String currency = Data.userData.getCurrency() + " %s";

    private TextView tvCountryCode;
    private CountryPicker countryPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mpesa_top_up);

        isTopUp = Prefs.with(getApplicationContext()).getBoolean("isTopUp", false);

        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(Fonts.mavenRegular(this));
        title.setText(R.string.mpesa);

        tvCountryCode = (TextView) findViewById(R.id.tvCountryCode);
        tvCountryCode.setText(Utils.getCountryCode(this));
        tvCountryCode.setOnClickListener(countryCodeClickListener);
        countryPicker =
                new CountryPicker.Builder().with(this)
                        .listener(this)
                        .build();

        edtPhoneNo = (EditText) findViewById(R.id.edtPhoneNo);
        edtPhoneNo.setText(Utils.retrievePhoneNumberTenChars(Data.userData.getCountryCode(), Data.userData.phoneNo));
        edtPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().startsWith("0")) {
                    if (editable.toString().length() > 1) {
                        edtPhoneNo.setText(editable.toString().substring(1));
                    } else {
                        edtPhoneNo.setText("");
                    }
                    Toast.makeText(MpesaTopUp.this, MpesaTopUp.this.getString(R.string.number_should_not_start_with_zero), Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                    HashMap<String, String> params = new HashMap<>();
                    params.put("payment_method", "CBE-BIRR");
                    params.put("driver_id", Data.userData.userId);
                    params.put("amount", editAmount.getText().toString());
                    RestClient.getApiServices().mpesaCashOut(params, new Callback<CbeBirrCashoutResponse>() {
                        @Override
                        public void success(CbeBirrCashoutResponse cbeBirrCashoutResponse, Response response) {
                            buildDialog(isTopUp);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                }
                else{
                    HashMap<String, String> params = new HashMap<>();
                    params.put("payment_method", "CBE-BIRR");
                    params.put("driver_id", Data.userData.userId);
                    params.put("amount", editAmount.getText().toString());
                    RestClient.getApiServices().mpesaTopUp(params, new Callback<CbeBirrCashoutResponse>() {
                        @Override
                        public void success(CbeBirrCashoutResponse cbeBirrCashoutResponse, Response response) {
                    buildDialog(isTopUp);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
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

    View.OnClickListener countryCodeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            countryPicker.showDialog(getSupportFragmentManager());
        }
    };

    @Override
    public void onSelectCountry(Country country) {
        tvCountryCode.setText(country.getDialCode());
    }

    public void buildDialog(boolean isTopUp){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MpesaTopUp.this);

        if(isTopUp)
            alertDialog.setTitle("Top UP");
        else
            alertDialog.setTitle("Cash Out");

        alertDialog.setMessage("R.string.cbe_birr_cashout_message");

        alertDialog.setPositiveButton("Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        Toast.makeText(getApplicationContext(), "You clicked on Continue", Toast.LENGTH_SHORT).show();
                    }
                });
        alertDialog.setNegativeButton("Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        MpesaTopUp.super.onBackPressed();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }
}
