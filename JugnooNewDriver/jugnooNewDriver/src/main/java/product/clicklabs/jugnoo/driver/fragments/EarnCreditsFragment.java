package product.clicklabs.jugnoo.driver.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.listeners.DriverCreditsListener;
import product.clicklabs.jugnoo.driver.support.SupportOption;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by Parminder Saini on 25/05/18.
 */
public class EarnCreditsFragment extends BaseFragment implements View.OnClickListener{


    View rootView;
    private DriverCreditsListener driverCreditsListener;

    public static EarnCreditsFragment newInstance() {

        Bundle args = new Bundle();

        EarnCreditsFragment fragment = new EarnCreditsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DriverCreditsListener){
            driverCreditsListener = (DriverCreditsListener) context;
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_earn_credits, container, false);

        if(Data.getCreditOptions() != null) {
            for(SupportOption supportOption : Data.getCreditOptions()) {
                addTextView(supportOption);
            }
        }

        return rootView;

    }

    @Override
    public String getTitle() {
        return getString(R.string.title_earn_more_credits);
    }

    private void addTextView(SupportOption supportOption){
        if(Data.userData == null){
            return;
        }
        TextView textView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.textview_earn_credits, null);

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(supportOption.getName());

        Spannable spannable = new SpannableString(getString(R.string.earn_credits_format,
                Utils.formatCurrencyValue(Data.userData.getCurrency(), supportOption.getAmount())));
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.text_color_light)),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssb.append(" ");
        ssb.append(spannable);

        textView.setText(ssb);
        textView.setTag(supportOption.getTag());
        textView.setOnClickListener(this);
        Utils.setTypeface(getActivity(), textView);

        ((LinearLayout)rootView.findViewById(R.id.llRoot)).addView(textView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()){
            case Constants.KEY_REFER_A_DRIVER:
                if(driverCreditsListener!=null)driverCreditsListener.openDriverEarnScreen();
                break;
            case Constants.KEY_REFER_A_CUSTOMER:
                if(driverCreditsListener!=null)driverCreditsListener.openCustomerEarnScreen();
                break;
            case Constants.KEY_ADVERTISE_WITH_US:
                if(driverCreditsListener!=null)driverCreditsListener.openAdvertiseScreen();
                break;
            case Constants.KEY_GET_CREDITS:
                if(driverCreditsListener!=null)driverCreditsListener.openGetCreditsInfoScreen();
                break;
        }
    }
}
