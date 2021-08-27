package product.clicklabs.jugnoo.driver.wallet;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.wallet.model.CbeBirrCashoutResponse;
import product.clicklabs.jugnoo.driver.wallet.model.ResponseModel;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CbeTopUp extends BaseActivity {

    Button buttonDone, fiftyButton, hundredButton, hundredFiftyButton;
    AutoCompleteTextView phoneNo;
    ImageView backBtn;
    EditText editAmount;
    String currency = Data.userData.getCurrency() + " %s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cbe_top_up);

        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(Fonts.mavenRegular(this));
        title.setText(R.string.cbe_birr);

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
                boolean isTopUp = Prefs.with(getApplicationContext()).getBoolean("isTopUp", false);
                if(isTopUp){
                    HashMap<String, String> params = new HashMap<>();
                    params.put("payment_method", "CBE-BIRR");
                    params.put("driver_id", Data.userData.userId);
                    params.put("amount", editAmount.getText().toString());
                    RestClient.getApiServices().cbeTopUp(params, new Callback<ResponseModel<CbeBirrCashoutResponse>>() {
                        @Override
                        public void success(ResponseModel<CbeBirrCashoutResponse> cbeBirrCashoutResponse, Response response) {
                            buildDialog(isTopUp, cbeBirrCashoutResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("Error", "Top up error");
                        }
                    });
                }
                else{
                    HashMap<String, String> params = new HashMap<>();
                    params.put("payment_method", "CBE-BIRR");
                    params.put("driver_id", Data.userData.userId);
                    params.put("amount", editAmount.getText().toString());
                    RestClient.getApiServices().cbeCashOut(params, new Callback<ResponseModel<CbeBirrCashoutResponse>>() {
                        @Override
                        public void success(ResponseModel<CbeBirrCashoutResponse> cbeBirrCashoutResponse, Response response) {
                            buildDialog(isTopUp, cbeBirrCashoutResponse);
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

    public void runUssd(String ussd){
        String ussdCode = "tel:" + ussd + Uri.encode("#");
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(ussdCode));
        try{
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        Integer.parseInt("123"));
            } else {
                startActivity(intent);
            }
        } catch (SecurityException e){
            e.printStackTrace();
        }
    }

    public void buildDialog(boolean isTopUp, ResponseModel<CbeBirrCashoutResponse> cbeBirrCashoutResponse){
        Dialog dialog = new Dialog(CbeTopUp.this);

        LayoutInflater layoutInflater = LayoutInflater.from(CbeTopUp.this);
        View dialogView = layoutInflater.inflate(R.layout.cbe_dialogue, null);

        dialog.setContentView(dialogView);

        String shortCode = cbeBirrCashoutResponse.getData().getCbeShortCode();
        String billRefNo = cbeBirrCashoutResponse.getData().getBillRefNo();

        TextView stepsDialogue = (TextView) dialogView.findViewById(R.id.stepsDialogue);
        stepsDialogue.setText(getString(R.string.press_the_call_button, shortCode, billRefNo));

        Button btnDone = (Button) dialogView.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runUssd("*847*5*1*0*" + shortCode + "*" + billRefNo);
            }
        });

        Button btn_cancel = (Button)  dialogView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
//                CbeTopUp.super.onBackPressed();
            }
        });
        dialog.show();
    }

}
