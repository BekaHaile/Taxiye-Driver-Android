package product.clicklabs.jugnoo.driver;

import android.os.Bundle;

import androidx.annotation.Nullable;

import product.clicklabs.jugnoo.driver.fragments.WalletFragment;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;

/**
 * Created by gurmail on 24/08/17.
 */

public class WalletActivity extends BaseFragmentActivity {

    private static final String TAG = WalletActivity.class.getSimpleName();
    private double walletBalance;
    String data = "[]";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        if(getIntent().hasExtra("data")) {
            data = getIntent().getStringExtra("data");
        }
        if(getIntent().hasExtra("amount"))
            walletBalance = getIntent().getDoubleExtra("amount", 0);

        WalletFragment walletFragment = new WalletFragment();
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        bundle.putDouble("amount", walletBalance);
        walletFragment.setArguments(bundle);
        loadFragment(R.id.main_layout, walletFragment, WalletFragment.class.getName(), true, true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
