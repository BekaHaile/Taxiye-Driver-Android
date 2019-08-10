package product.clicklabs.jugnoo.driver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.R;


public class BidRequestFragment extends Fragment {

    TextView tvPickup,tvDistance,tvDrop,tvPrice,tvCommision,tvOffer,tvSkip;
    Button btAccept;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_bid_request, container, false);

        return rootView;
    }
}
