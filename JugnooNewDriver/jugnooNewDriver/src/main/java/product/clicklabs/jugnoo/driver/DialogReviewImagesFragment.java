package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.listeners.DirectionsGestureListener;
import product.clicklabs.jugnoo.driver.utils.FeedUtils;

public class DialogReviewImagesFragment extends DialogFragment {

    private Activity activity;
    private View rootView;

    private ImageView ivClose;
    private ViewPager vpImages;
    private RelativeLayout rlRoot;

    public static DialogReviewImagesFragment newInstance(int positionImageClicked,ArrayList<String> customerImages){
        DialogReviewImagesFragment dialog = new DialogReviewImagesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_POSITION_REVIEW, positionImageClicked);
        bundle.putStringArrayList(Constants.LIST_IMAGES_REVIEW,customerImages);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragmentTrans);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_review_images, container, false);
        int positionImageClicked = getArguments().getInt(Constants.KEY_POSITION_REVIEW, 0);

        List<String> imageArrayList =null;
        if(getArguments().containsKey(Constants.LIST_IMAGES_REVIEW)) {
            imageArrayList = (List<String>) getArguments().getSerializable(Constants.LIST_IMAGES_REVIEW);
        }

        activity = getActivity();

        ivClose = (ImageView) rootView.findViewById(R.id.ivClose);
        ivClose.setVisibility(View.GONE);
        vpImages = (ViewPager) rootView.findViewById(R.id.vpImages);

        try {
            RelativeLayout.LayoutParams vpParams = (RelativeLayout.LayoutParams) vpImages.getLayoutParams();
            vpParams.height = FeedUtils.dpToPx(270);

            vpParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            vpImages.setLayoutParams(vpParams);

            List<String> display = imageArrayList;

            vpImages.setAdapter(new ImagePagerAdapter(activity,display));



            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });

            WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
            vpImages.setCurrentItem(positionImageClicked);
        } catch (Exception e) {
            e.printStackTrace();
        }

        rlRoot = (RelativeLayout) rootView.findViewById(R.id.rlRoot);
        rlRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });



        return rootView;
    }


    public class ImagePagerAdapter extends PagerAdapter {

        private Context context;
        private List<String> reviewImages;
        private LayoutInflater inflater;

        public ImagePagerAdapter(Context context, List<String> reviewImages){
            this.context = context;
            this.reviewImages = reviewImages;
            this.inflater = LayoutInflater.from(context);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.dialog_item_review_image_pager, container, false);
            final ImageView ivReviewImage = (ImageView) root.findViewById(R.id.ivReviewImage);
            final View progressBar =  root.findViewById(R.id.pbar);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });

            ivReviewImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gesture.onTouchEvent(event);
                }
            });


            RequestOptions options = new RequestOptions()
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .error(R.drawable.ic_fresh_item_placeholder);
            if(reviewImages!=null && reviewImages.size()==1) {
                Glide.with(activity).load(reviewImages.get(position)).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        ivReviewImage.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                        return false;
                    }
                }).apply(options).into(ivReviewImage);
            }
            else {
                Glide.with(activity).load(reviewImages.get(position)).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        ivReviewImage.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                        return false;
                    }
                }).apply(options).into(ivReviewImage);
            }
            container.addView(root);
            return root;
        }



        @Override
        public int getCount() {
            return reviewImages == null ? 0 : reviewImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                container.removeView((View)object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    final GestureDetector gesture = new GestureDetector(getActivity(),
            new DirectionsGestureListener(new DirectionsGestureListener.Callback() {
                @Override
                public void topSwipe() {
                    getDialog().dismiss();
                }

                @Override
                public void bottomSwipe() {
                    getDialog().dismiss();
                }

                @Override
                public void leftSwipe() {

                }

                @Override
                public void rightSwipe() {

                }
            }));

}
