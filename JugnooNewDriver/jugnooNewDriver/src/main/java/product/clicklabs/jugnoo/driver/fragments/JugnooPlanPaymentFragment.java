package product.clicklabs.jugnoo.driver.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JugnooPlanPaymentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JugnooPlanPaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JugnooPlanPaymentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;

    private OnFragmentInteractionListener mListener;
    private WebView webView;

    public JugnooPlanPaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JugnooPlanPaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JugnooPlanPaymentFragment newInstance(String param1, String param2,String param3) {
        JugnooPlanPaymentFragment fragment = new JugnooPlanPaymentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DialogPopup.showLoadingDialog(getActivity(), getString(R.string.loading));
        View rootView = inflater.inflate(R.layout.fragment_jugnoo_plan_payment, container, false);
        webView = (WebView) rootView.findViewById(R.id.webViewPayment);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        PaymentWebViewClient paymentWebViewClient = new PaymentWebViewClient();
        webView.setWebViewClient(paymentWebViewClient);

        loadHTMLContent(mParam1, webView);
        return rootView;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(boolean isSuccess);
    }

    private class PaymentWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if(url .equalsIgnoreCase(mParam2) ){
                if(mListener!=null){
                    mListener.onFragmentInteraction(true);

                }
            }else if(url.equalsIgnoreCase(mParam3)){
                if(mListener!=null){
                    mListener.onFragmentInteraction(false);

                }

            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(view.getProgress()>80){
                DialogPopup.dismissLoadingDialog();

            }
            super.onPageFinished(view, url);
        }
    }

    public void loadHTMLContent(String data,WebView webView) {
        try {
            final String mimeType = "text/html";
            final String encoding = "UTF-8";

            webView.loadDataWithBaseURL("", data, mimeType, encoding, "");
        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();

            e.printStackTrace();
            if(mListener!=null){
                mListener.onFragmentInteraction(false);

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
          /*  if(webView!=null){
                webView.stopLoading();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
