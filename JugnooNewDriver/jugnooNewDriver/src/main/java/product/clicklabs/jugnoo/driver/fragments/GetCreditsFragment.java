package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by Parminder Saini on 25/05/18.
 */
public class GetCreditsFragment extends BaseFragment {

    private Activity activity;

    public static GetCreditsFragment newInstance(){
        GetCreditsFragment fragment = new GetCreditsFragment();
        Bundle bundle = new Bundle();

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        Bundle bundle = getArguments();

    }

    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_get_credits, container, false);

        ImageView ivBackground = (ImageView) rootView.findViewById(R.id.ivBackground);
        TextView tvInfo = (TextView) rootView.findViewById(R.id.tvInfo); tvInfo.setTypeface(Fonts.mavenRegular(activity));

        if(Data.userData != null) {
            if (!TextUtils.isEmpty(Data.userData.getGetCreditsImage())) {
                Picasso.with(activity).load(Data.userData.getGetCreditsImage())
                        .placeholder(R.drawable.ic_receive_credits)
                        .error(R.drawable.ic_receive_credits)
                        .into(ivBackground);
            }
            tvInfo.setText(Utils.fromHtml(Data.userData.getGetCreditsInfo()));
        }

        return rootView;
    }

    @Override
    public String getTitle() {
        return getString(R.string.get_credits_from_drivers);
    }
}
