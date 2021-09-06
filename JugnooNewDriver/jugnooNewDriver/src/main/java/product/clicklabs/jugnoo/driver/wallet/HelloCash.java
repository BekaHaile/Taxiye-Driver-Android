package product.clicklabs.jugnoo.driver.wallet;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import product.clicklabs.jugnoo.driver.wallet.model.HelloCashCashoutResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HelloCash extends BaseActivity implements OnCountryPickerListener<Country> {

    Button buttonDone, fiftyButton, hundredButton, hundredFiftyButton;
    ImageView backBtn;
    EditText editAmount, edtPhoneNo;
    String currency = Data.userData.getCurrency() + " %s";
    LinearLayout minimumAmount;

    private TextView tvCountryCode, redLabelMessage;
    private CountryPicker countryPicker;
    boolean isTopUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hellocash_top_up);

        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(Fonts.mavenRegular(this));
        title.setText(R.string.hello_cash);

        isTopUp = Prefs.with(getApplicationContext()).getBoolean("isTopUp", false);
        minimumAmount = (LinearLayout) findViewById(R.id.minimumAmount);
        redLabelMessage = (TextView) findViewById(R.id.redLabelMessage);

        editAmount = (EditText) findViewById(R.id.editAmount);
        editAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!isTopUp && !editable.toString().equals("")) {
                    if (Integer.parseInt(editable.toString()) < 25) {
                        if (Integer.parseInt(editable.toString()) < 25) {
                            redLabelMessage.setText(getResources().getString(R.string.minimumCashOut));
                            minimumAmount.setVisibility(View.VISIBLE);
                            buttonDone.setEnabled(false);
                        } else {
                            buttonDone.setEnabled(true);
                            minimumAmount.setVisibility(View.GONE);
                        }
                    } else {

                        int minimumDriverBalance = Prefs.with(HelloCash.this).getInt("minimumDriverBalance", 0) - 25;
                        int walletBalance = Prefs.with(HelloCash.this).getInt("walletBalance", 0);
                        if (walletBalance - Integer.parseInt(editable.toString()) <= minimumDriverBalance) {
                            redLabelMessage.setText(getResources().getString(R.string.walletBalanceAfterCheckout,
                                   Integer.toString(minimumDriverBalance)));
                            minimumAmount.setVisibility(View.VISIBLE);
                            buttonDone.setEnabled(false);
                        } else {
                            buttonDone.setEnabled(true);
                            minimumAmount.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

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
                    Toast.makeText(HelloCash.this, HelloCash.this.getString(R.string.number_should_not_start_with_zero), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvCountryCode = (TextView) findViewById(R.id.tvCountryCode);
        tvCountryCode.setText(Utils.getCountryCode(this));
        tvCountryCode.setOnClickListener(countryCodeClickListener);
        countryPicker =
                new CountryPicker.Builder().with(this)
                        .listener(this)
                        .build();

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

        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"Lion", "Wegagen"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        buttonDone = (Button) findViewById(R.id.btn_done);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogPopup.showLoadingDialog(HelloCash.this, HelloCash.this.getResources().getString(R.string.loading));
                if(isTopUp){
                    HashMap<String, String> params = new HashMap<>();
                    params.put("payment_method", "HELLO-CASH");
                    params.put("user_id", Data.userData.userId);
                    params.put("amount", editAmount.getText().toString());
                    params.put("access_token", Data.userData.accessToken);
                    params.put("user_type", "DRIVER");

                    params.put("phone_no", tvCountryCode.getText().toString() + edtPhoneNo.getText().toString());
                    params.put("system", dropdown.getSelectedItem().toString());

                    RestClient.getApiServices().helloCashTopUp(params, new Callback<HelloCashCashoutResponse>() {
                        @Override
                        public void success(HelloCashCashoutResponse helloCashCashoutResponse, Response response) {
                            DialogPopup.dismissLoadingDialog();
                            if(!helloCashCashoutResponse.isUpcoming())
                                buildDialog(helloCashCashoutResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("Error", "Top up error");
                            DialogPopup.dismissLoadingDialog();

                            Toast.makeText(HelloCash.this, HelloCash.this.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    HashMap<String, String> params = new HashMap<>();
                    params.put("payment_method", "HELLO-CASH");
                    params.put("access_token", Data.userData.accessToken);
                    params.put("user_id", Data.userData.userId);
                    params.put("amount", editAmount.getText().toString());
                    params.put("user_type", "DRIVER");

                    params.put("phone_no", tvCountryCode.getText().toString() + edtPhoneNo.getText().toString());
                    params.put("system", dropdown.getSelectedItem().toString());

                    RestClient.getApiServices().helloCashCashout(params, new Callback<HelloCashCashoutResponse>() {
                        @Override
                        public void success(HelloCashCashoutResponse helloCashCashoutResponse, Response response) {
                            DialogPopup.dismissLoadingDialog();
                            Toast.makeText(HelloCash.this, HelloCash.this.getString(R.string.success), Toast.LENGTH_SHORT).show();
                            if(!helloCashCashoutResponse.isUpcoming())
                                finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            DialogPopup.dismissLoadingDialog();
                            Log.e("Error", "Cash out error");

                            Toast.makeText(HelloCash.this, HelloCash.this.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
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

    public void runUssd(String ussd){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+ussd+"#"));
        try{
                startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void buildDialog(HelloCashCashoutResponse helloCashCashoutResponse){
        Dialog dialog = new Dialog(HelloCash.this);

        LayoutInflater layoutInflater = LayoutInflater.from(HelloCash.this);
        View dialogView = layoutInflater.inflate(R.layout.cbe_dialogue, null);

        dialog.setContentView(dialogView);


        String id = String.valueOf(helloCashCashoutResponse.getId());
        String code = String.valueOf(helloCashCashoutResponse.getCode());
        int amount = helloCashCashoutResponse.getAmount();
        String description = helloCashCashoutResponse.getDescription();

        TextView stepsDialogue = (TextView) dialogView.findViewById(R.id.stepsDialogue);
        stepsDialogue.setText(getString(R.string.hello_cash_instruction, id, code, String.valueOf(amount), description));

        Button btnDone = (Button) dialogView.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runUssd("*912");
            }
        });

        Button btn_cancel = (Button)  dialogView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                finish();
//                CbeTopUp.super.onBackPressed();
            }
        });
        dialog.show();
    }

    @Override
    public void onSelectCountry(Country country) {
        tvCountryCode.setText(country.getDialCode());
    }
}
