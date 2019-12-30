package product.clicklabs.jugnoo.driver.fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;

/**
 * Created by gurmail on 24/08/17.
 */

public abstract class DriverBaseFragment extends Fragment {

    private static final String TAG = "DriverBaseFragment";

    private static final int FADE_CROSSOVER_TIME_MILLIS = 300;

    /**
     * Flag that indicates that this fragment is attached to an Activity
     */
    private boolean mIsAttached;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIsAttached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mIsAttached = false;
    }

    /**
     * A Tag to add to all async tasks. This must be unique for all Fragments types
     *
     * @return An Object that's the tag for this fragment
     */
    protected abstract Object getTaskTag();

    /**
     * Whether this Fragment is currently attached to an Activity
     *
     * @return <code>true</code> if attached, <code>false</code> otherwise
     */
    public boolean isAttached() {
        return mIsAttached;
    }

    /**
     * Helper method to load fragments into layout
     *
     * @param containerResId The container resource Id in the content view into which to load the
     *                       fragment
     * @param fragment       The fragment to load
     * @param tag            The fragment tag
     * @param customAnimate Whether the transaction should be animated
     */
    public void loadFragment(final int containerResId, final Fragment fragment,
                             final String tag, final boolean customAnimate, final boolean hideView) {

        if (mIsAttached) {
            ((BaseFragmentActivity) getActivity()).loadFragment(containerResId, fragment, tag, customAnimate, hideView);
        }

    }

    public void setTextSpan(TextView textView, int start, int end, int color, float size) {
        Spannable spannable = (Spannable) textView.getText();
        if(color != -1)
            spannable.setSpan(new RelativeSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(color != -1) {
            spannable.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
