package product.clicklabs.jugnoo.driver.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.adapters.ReturnOptionsListAdapter;

public class DialogPopup {

	
	
	public DialogPopup(){
	}
	
	public static Dialog dialog;
	public static void alertPopup(Activity activity, String title, String message){
		alertPopup(activity, title, message, false, true);
	}

	public static void alertPopup(Activity activity, String title, String message, boolean cancellable, boolean okVisible) {
		alertPopup(activity, title, message, cancellable, okVisible, null);
	}

	public static void alertPopup(Activity activity, String title, String message, boolean cancellable, boolean okVisible, final View.OnClickListener listener) {
		try {
			dismissAlertPopup();
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}
			
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_one_button);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(cancellable);
			dialog.setCanceledOnTouchOutside(cancellable);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));
			
			textHead.setText(title);
			textMessage.setText(message);
			
			textHead.setVisibility(View.GONE);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
			btnOk.setVisibility(okVisible ? View.VISIBLE : View.GONE);
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					if(listener != null){
						listener.onClick(view);
					}
				}
				
			});

			if(cancellable) {
				frameLayout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void alertPopupBold(Activity activity, String title, String message) {
		try {
			dismissAlertPopup();
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}

			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_one_button);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenRegular(activity));
//									textMessage.setTextSize(22f * ASSL.Xscale());

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);

			textHead.setVisibility(View.GONE);

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}

			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void alertPopupWithListener(Activity activity, String title, String message, final View.OnClickListener onClickListener) {
		alertPopupWithListenerTopBar(activity, title, message, onClickListener, onClickListener, false, "");
	}
	public static void alertPopupWithListenerTopBar(Activity activity, String title, String message, final View.OnClickListener onClickListener,
													final View.OnClickListener clickListener, boolean isTourEnable, String tourText) {
		try {
			dismissAlertPopup();
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}
			
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_one_button);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
			textHead.setText(title);
			textMessage.setText(message);
			
			textHead.setVisibility(View.GONE);

			RelativeLayout tourLayoutView = (RelativeLayout) dialog.findViewById(R.id.tour_layout);
			TextView tourTextView = (TextView) dialog.findViewById(R.id.tour_textView);
			tourTextView.setTypeface(Fonts.mavenRegular(activity));
			tourTextView.setText(tourText);
			ImageView crossTour = (ImageView) dialog.findViewById(R.id.cross_tour);

			if(isTourEnable) {
				tourLayoutView.setVisibility(View.VISIBLE);
			} else {
				tourLayoutView.setVisibility(View.GONE);
			}

			crossTour.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					clickListener.onClick(v);
				}
			});
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					onClickListener.onClick(view);
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static void alertPopupAuditWithListener(Activity activity, String title, String message, final View.OnClickListener onClickListener) {
		try {
			dismissAlertPopup();
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}

			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_start_audit_gif);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenRegular(activity));

			final ImageView imageViewAuditGif = (ImageView) dialog.findViewById(R.id.imageViewAuditGif);
			Button buttonCancelPopup = (Button) dialog.findViewById(R.id.buttonCancelPopup);

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);

			textHead.setVisibility(View.GONE);

			imageViewAuditGif.setBackgroundResource(R.drawable.auto_loading_frame_anim);
			imageViewAuditGif.post(new Runnable() {
				@Override
				public void run() {
					AnimationDrawable frameAnimation =
							(AnimationDrawable) imageViewAuditGif.getBackground();
					frameAnimation.start();
				}
			});



			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					onClickListener.onClick(view);
				}

			});

			buttonCancelPopup.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static void alertPopupWithImageListener(Activity activity, String title, String message, int resId, final View.OnClickListener onClickListener, final boolean showTitle) {
		try {
			dismissAlertPopup();
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}

			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_one_button_image);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenRegular(activity));
			ImageView imageViewAlert = (ImageView) dialog.findViewById(R.id.imageViewAlert);

			imageViewAlert.setImageResource(resId);

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);

			textHead.setVisibility(View.GONE);

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					onClickListener.onClick(view);
				}

			});

			if(showTitle){
				textHead.setVisibility(View.VISIBLE);
			}
			else{
				textHead.setVisibility(View.GONE);
			}

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void alertPopupWithImageListenerSubs(Activity activity, String title, String message, int resId, final View.OnClickListener onClickListener, final boolean showTitle) {
		try {
			dismissAlertPopup();
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}

			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_one_button_image_subs);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenRegular(activity));
			ImageView imageViewAlert = (ImageView) dialog.findViewById(R.id.imageViewAlert);

			imageViewAlert.setImageResource(resId);

			textHead.setText(title);
			textMessage.setText(message);

			textHead.setVisibility(View.GONE);

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					onClickListener.onClick(view);
				}

			});

			if(showTitle){
				textHead.setVisibility(View.VISIBLE);
			}
			else{
				textHead.setVisibility(View.GONE);
			}

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void alertPopupTwoButtonsWithListeners(Activity activity,String message,View.OnClickListener listenerPositive){
		alertPopupTwoButtonsWithListeners(activity, "", message,"","", listenerPositive, null, true,false);
	}

	public static void alertPopupTwoButtonsWithListeners(Activity activity, String title, String message, String okText, String canceltext,
														 final View.OnClickListener listenerPositive, final View.OnClickListener listenerNegative, final boolean cancelable, final boolean showTitle) {
		try {
			dismissAlertPopup();
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_two_buttons);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(cancelable);

			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog
					.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.mavenRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);

			if(showTitle){
				textHead.setVisibility(View.VISIBLE);
			}
			else{
				textHead.setVisibility(View.GONE);
			}

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			if(!"".equalsIgnoreCase(okText)){
				btnOk.setText(okText);
			}

			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.mavenRegular(activity));
			if(!"".equalsIgnoreCase(canceltext)){
				btnCancel.setText(canceltext);
			}

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					if(listenerPositive!=null){
						listenerPositive.onClick(view);

					}
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if(listenerNegative!=null){
						listenerNegative.onClick(v);

					}
				}
			});


			dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});


			dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (cancelable) {
						dismissAlertPopup();
					}
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void alertPopupTrainingTwoButtonsWithListeners(Activity activity, String title, String message, String okText,
																 String canceltext, final View.OnClickListener listenerPositive,
																 final View.OnClickListener listenerNegative, final boolean cancelable,
																 final boolean showTitle, final boolean showTour, final String tourText,
																 final View.OnClickListener cancelTour) {
		try {
			dismissAlertPopup();
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_two_buttons_tour);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(cancelable);

			RelativeLayout tour_layout = (RelativeLayout) dialog.findViewById(R.id.tour_layout);
			TextView tour_textView = (TextView) dialog.findViewById(R.id.tour_textView);
			tour_textView.setTypeface(Fonts.mavenRegular(activity));
			ImageView cross_tour = (ImageView) dialog.findViewById(R.id.cross_tour);

			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog
					.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.mavenRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);
			tour_textView.setText(tourText);

			if(showTitle){
				textHead.setVisibility(View.VISIBLE);
			}
			else{
				textHead.setVisibility(View.GONE);
			}

			if(showTour){
				tour_layout.setVisibility(View.VISIBLE);
			} else {
				tour_layout.setVisibility(View.GONE);
			}

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			if(!"".equalsIgnoreCase(okText)){
				btnOk.setText(okText);
			}

			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.mavenRegular(activity));
			if(!"".equalsIgnoreCase(canceltext)){
				btnCancel.setText(canceltext);
			}

			Button btnEnterToll = (Button) dialog.findViewById(R.id.btnEnterToll);
			btnEnterToll.setVisibility(View.GONE);
			TextView tvTollValue = (TextView) dialog.findViewById(R.id.tvTollValue);
			tvTollValue.setVisibility(View.GONE);

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					listenerPositive.onClick(view);
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					listenerNegative.onClick(v);
				}
			});

			cross_tour.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					cancelTour.onClick(v);
				}
			});

			dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});


			dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (cancelable) {
						dismissAlertPopup();
					}
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



    public static void alertPopupThreeButtonsWithListeners(Activity activity, String title, String message, String okText, String neutralText, String canceltext,
                                                         final View.OnClickListener listenerPositive, final View.OnClickListener listenerNeutral, final View.OnClickListener listenerNegative,
                                                           final boolean cancelable, final boolean showTitle) {
        try {
            dismissAlertPopup();
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            dialog.setContentView(R.layout.dialog_custom_three_buttons);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
            new ASSL(activity, frameLayout, 1134, 720, true);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(cancelable);

            TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
            textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
            TextView textMessage = (TextView) dialog
                .findViewById(R.id.textMessage);
            textMessage.setTypeface(Fonts.mavenRegular(activity));

            textMessage.setMovementMethod(new ScrollingMovementMethod());
            textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

            textHead.setText(title);
            textMessage.setText(message);

            if(showTitle){
                textHead.setVisibility(View.VISIBLE);
            }
            else{
                textHead.setVisibility(View.GONE);
            }

            Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
            if(!"".equalsIgnoreCase(okText)){
                btnOk.setText(okText);
            }

            Button btnNeutral = (Button) dialog.findViewById(R.id.btnNeutral);
            btnNeutral.setTypeface(Fonts.mavenRegular(activity));
            if(!"".equalsIgnoreCase(neutralText)){
                btnNeutral.setText(neutralText);
            }

            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(Fonts.mavenRegular(activity));
            if(!"".equalsIgnoreCase(canceltext)){
                btnCancel.setText(canceltext);
            }

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listenerPositive.onClick(view);
					dialog.dismiss();
                }
            });

            btnNeutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listenerNeutral.onClick(view);
					dialog.dismiss();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listenerNegative.onClick(v);
					dialog.dismiss();
                }
            });


            dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                }
            });


            dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(cancelable){
                        dismissAlertPopup();
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public static void dismissAlertPopup(){
		try{
			if(dialog != null && dialog.isShowing()){
				dialog.dismiss();
			}
		}catch(Exception e){
		}
	}
	
	
	
	
	
	
	
	public static ProgressDialog progressDialog;
	
	/**
	 * Displays custom loading dialog
	 * @param context application context
	 * @param message string message to show in dialog
	 */
	public static void showLoadingDialog(Context context, String message) {
		try {
			if(isDialogShowing()){
				dismissLoadingDialog();
			}
			progressDialog = new ProgressDialog(context, android.R.style.Theme_Translucent_NoTitleBar);
			progressDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			progressDialog.show();
			WindowManager.LayoutParams layoutParams = progressDialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			progressDialog.setCancelable(false);
			progressDialog.setContentView(R.layout.dialog_loading);

			RelativeLayout frameLayout = (RelativeLayout) progressDialog.findViewById(R.id.rv);
			new ASSL((Activity)context, frameLayout, 1134, 720, false);
			
			TextView messageText = (TextView) progressDialog.findViewById(R.id.textView1); messageText.setTypeface(Fonts.mavenRegular(context));
			messageText.setText(message); 
		} catch (Exception e) {
			e.printStackTrace();
			if(isDialogShowing()){
				dismissLoadingDialog();
			}
		}
		
	}

	public static Dialog showLoadingDialog(Context context, String message, boolean newInstance) {

		try {
			Dialog dialog;
			if(newInstance){
				dialog = new ProgressDialog(context, android.R.style.Theme_Translucent_NoTitleBar);
			} else{
				dismissLoadingDialog();
				progressDialog = new ProgressDialog(context, android.R.style.Theme_Translucent_NoTitleBar);
				dialog = progressDialog;
			}
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.show();
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.dialog_loading);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL((Activity)context, frameLayout, 1134, 720, false);

			TextView messageText = (TextView) dialog.findViewById(R.id.textView1); messageText.setTypeface(Fonts.mavenRegular(context));
			messageText.setText(message);
			return dialog;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public static boolean isDialogShowing(){
		try{
			if(progressDialog == null){
				return false;
			}
			else{
				return progressDialog.isShowing();
			}
		} catch(Exception e){
			return false;
		}
	}
	
	/**
	 * Dismisses above loading dialog
	 */
	public static void dismissLoadingDialog() {
		try{
		if(progressDialog != null){
			progressDialog.dismiss();
			progressDialog = null;
		}} catch(Exception e){
			Log.e("e","="+e);
		}
	}
	
	
	
	public static AlertDialog googlePlayAlertDialog;
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public static void showGooglePlayErrorAlert(final Activity mContext){
		try{
			if(googlePlayAlertDialog != null && googlePlayAlertDialog.isShowing()){
				googlePlayAlertDialog.dismiss();
			}
				AlertDialog.Builder alertDialogPrepare = new AlertDialog.Builder(mContext);
		   	 
		        // Setting Dialog Title
		        alertDialogPrepare.setTitle(mContext.getResources().getString(R.string.google_pay_service_error));
		        alertDialogPrepare.setCancelable(false);
		 
		        // Setting Dialog Message
		        alertDialogPrepare.setMessage(mContext.getResources().getString(R.string.google_pay_service_error_message));
		 
		        // On pressing Settings button
		        alertDialogPrepare.setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						try {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse("market://details?id=com.google.android.gms"));
							mContext.startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
		        });
		 
		        // on pressing cancel button
		        alertDialogPrepare.setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	dialog.dismiss();
		            	mContext.finish();
		            }
		        });
		 
		        googlePlayAlertDialog = alertDialogPrepare.create();
		        
		        // Showing Alert Message
		        googlePlayAlertDialog.show();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	public static AlertDialog locationAlertDialog;
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public static void showLocationSettingsAlert(final Context mContext){
		try{
			if(!((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER)
					&&
					!((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)){
			if(locationAlertDialog != null && locationAlertDialog.isShowing()){
				locationAlertDialog.dismiss();
			}
				AlertDialog.Builder alertDialogPrepare = new AlertDialog.Builder(mContext);
		   	 
		        // Setting Dialog Title
		        alertDialogPrepare.setTitle(mContext.getResources().getString(R.string.location_setting));
		        alertDialogPrepare.setCancelable(false);
		 
		        // Setting Dialog Message
		        alertDialogPrepare.setMessage(mContext.getResources().getString(R.string.location_setting_enable_message));
		 
		        // On pressing Settings button
		        alertDialogPrepare.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
						dialog.dismiss();
						try {
							Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							mContext.startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
						}
		            }
		        });
		 
		        locationAlertDialog = alertDialogPrepare.create();
		        
		        // Showing Alert Message
		        locationAlertDialog.show();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


//	public static void dialogBanner(Activity activity, String message) {
//		try {
//			dismissAlertPopup();
//
//			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
//			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
//			dialog.setContentView(R.layout.dialog_banner);
//
//			LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.rv);
//			new ASSL(activity, linearLayout, 1134, 720, false);
//
//			dialog.setCancelable(true);
//			dialog.setCanceledOnTouchOutside(true);
//
//			TextView textViewBanner = (TextView) dialog.findViewById(R.id.textViewBanner); textViewBanner.setTypeface(Fonts.mavenRegular(activity));
//			textViewBanner.setText(message);
//
//			linearLayout.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					dialog.dismiss();
//				}
//			});
//
//			dialog.show();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static void dialogBanner(Activity activity, String message) {
		dialogBanner(activity, message, null, 5000, 0, 0);
	}

	public static void dialogBanner(Activity activity, String message, final View.OnClickListener onClickListener,
									long timeToDismiss, int textColorRes, int textBgColorRes){
		try {
			dismissAlertPopup();
//
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_banner);

			LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, linearLayout, 1134, 720, false);

			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			TextView textViewBanner = (TextView) dialog.findViewById(R.id.textViewBanner); textViewBanner.setTypeface(Fonts.mavenRegular(activity));
			textViewBanner.setText(message);
			if(textColorRes > 0){
				textViewBanner.setTextColor(ContextCompat.getColor(activity, textColorRes));
			}
			if(textBgColorRes > 0){
				textViewBanner.setBackgroundColor(ContextCompat.getColor(activity, textBgColorRes));
			}

			linearLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (onClickListener != null) {
						onClickListener.onClick(v);
					}
				}
			});

			dialog.show();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					DialogPopup.dismissAlertPopup();
				}
			}, timeToDismiss);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dialogNewBanner(Activity activity, String message, long time) {
		dialogBannerNewWithCancelListener(activity, message, null, time);
	}

	public static void dialogBannerNewWithCancelListener(Activity activity, String message, final View.OnClickListener onClickListener, long timeToDismiss){
		try {
			dismissAlertPopup();

			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_banner);

			LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, linearLayout, 1134, 720, false);

			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			TextView textViewBanner = (TextView) dialog.findViewById(R.id.textViewBanner); textViewBanner.setTypeface(Fonts.mavenRegular(activity));
			textViewBanner.setText(message);

			linearLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if(onClickListener != null) {
						onClickListener.onClick(v);
					}
				}
			});

			dialog.show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						if(dialog!= null && dialog.isShowing()) {
							dialog.dismiss();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, timeToDismiss);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void alertPopupListnTwoButtonsWithListeners(Activity activity, String title, String message, String okText, String canceltext,
														 final View.OnClickListener listenerPositive, final View.OnClickListener listenerNegative, final boolean cancelable, final boolean showTitle) {
		try {
			dismissAlertPopup();
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_delivery_return_reason);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(cancelable);

			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.mavenRegular(activity));

			RecyclerView recyclerViewReturnOptions;
			ReturnOptionsListAdapter returnOptionsListAdapter;
			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);

			recyclerViewReturnOptions = (RecyclerView) dialog.findViewById(R.id.recyclerViewReturnOptions);
			recyclerViewReturnOptions.setLayoutManager(new LinearLayoutManager(activity));
			recyclerViewReturnOptions.setItemAnimator(new DefaultItemAnimator());
			recyclerViewReturnOptions.setHasFixedSize(false);
			returnOptionsListAdapter = new ReturnOptionsListAdapter(activity);
			recyclerViewReturnOptions.setAdapter(returnOptionsListAdapter);

			if(showTitle){
				textHead.setVisibility(View.VISIBLE);
			}
			else{
				textHead.setVisibility(View.GONE);
			}

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			if(!"".equalsIgnoreCase(okText)){
				btnOk.setText(okText);
			}

			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.mavenRegular(activity));
			if(!"".equalsIgnoreCase(canceltext)){
				btnCancel.setText(canceltext);
			}

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					listenerPositive.onClick(view);
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					listenerNegative.onClick(v);
				}
			});


			dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});


			dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (cancelable) {
						dismissAlertPopup();
					}
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void alertPopupDeliveryTwoButtonsWithListeners(Activity activity, String deliveryId, String collectCash, String name, String address, String items, String message , String okText, String canceltext,
														 final View.OnClickListener listenerPositive, final View.OnClickListener listenerNegative, final boolean cancelable, final boolean showTitle) {
		try {
			dismissAlertPopup();
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_mark_delivery);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(cancelable);

			RelativeLayout relativeLayoutNotes = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutNotes);
			if("-1".equalsIgnoreCase(items)){
				relativeLayoutNotes.setVisibility(View.GONE);
			}

			TextView textHead = (TextView) dialog.findViewById(R.id.textViewDeliveryId);
			textHead.setTypeface(Fonts.mavenRegular(activity));

			TextView textMessage = (TextView) dialog.findViewById(R.id.textViewTakeCash);
			textMessage.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

			TextView textViewName = (TextView) dialog.findViewById(R.id.textViewName);
			textViewName.setTypeface(Fonts.mavenRegular(activity));

			TextView textViewAddress = (TextView) dialog.findViewById(R.id.textViewAddress);
			textViewAddress.setTypeface(Fonts.mavenRegular(activity));

			TextView textViewItems = (TextView) dialog.findViewById(R.id.textViewItems);
			textViewItems.setTypeface(Fonts.mavenRegular(activity));
			textViewItems.setMovementMethod(new ScrollingMovementMethod());

			TextView textViewTakeConfMessage = (TextView) dialog.findViewById(R.id.textViewTakeConfMessage);
			textViewTakeConfMessage.setTypeface(Fonts.mavenRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(deliveryId);
			textMessage.setText(collectCash);
			textViewName.setText(name);
			textViewAddress.setText(address);
			textViewItems.setText(items);
			textViewTakeConfMessage.setText(message);

			if(showTitle){
				textHead.setVisibility(View.VISIBLE);
			}
			else{
				textHead.setVisibility(View.GONE);
			}

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Fonts.mavenRegular(activity));
			if(!"".equalsIgnoreCase(okText)){
				btnOk.setText(okText);
			}

			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.mavenRegular(activity));
			if(!"".equalsIgnoreCase(canceltext)){
				btnCancel.setText(canceltext);
			}

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					listenerPositive.onClick(view);
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					listenerNegative.onClick(v);
				}
			});


			dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});


			dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (cancelable) {
						dismissAlertPopup();
					}
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static void alertPopupDeliveryReturnWithListeners(Activity activity, String deliveryId, String collectCash,
															 int total, int delivered, int failed, String message,
															 final View.OnClickListener listenerPositive,
															 final View.OnClickListener listenerNegative,
															 final boolean cancelable, final boolean showTitle) {
		try {
			dismissAlertPopup();
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_mark_delivery_return);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(cancelable);

			TextView textHead = (TextView) dialog.findViewById(R.id.textViewDeliveryId);
			textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewTotalDelivery = (TextView) dialog.findViewById(R.id.textViewTotalDelivery);
			textViewTotalDelivery.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewTotalDeliveryValue = (TextView) dialog.findViewById(R.id.textViewTotalDeliveryValue);
			textViewTotalDeliveryValue.setTypeface(Fonts.mavenRegular(activity));

			TextView textViewTotalDelivered = (TextView) dialog.findViewById(R.id.textViewTotalDelivered);
			textViewTotalDelivered.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewTotalDeliveredValue = (TextView) dialog.findViewById(R.id.textViewTotalDeliveredValue);
			textViewTotalDeliveredValue.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewTotalReturned = (TextView) dialog.findViewById(R.id.textViewTotalReturned);
			textViewTotalReturned.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewTotalReturnedValue = (TextView) dialog.findViewById(R.id.textViewTotalReturnedValue);
			textViewTotalReturnedValue.setTypeface(Fonts.mavenRegular(activity));


			TextView textMessage = (TextView) dialog.findViewById(R.id.textViewTakeCash);
			textMessage.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

			TextView textViewSummaryText = (TextView) dialog.findViewById(R.id.textViewSummaryText);
			textViewSummaryText.setTypeface(Fonts.mavenRegular(activity));

			TextView textViewTakeConfMessage = (TextView) dialog.findViewById(R.id.textViewTakeConfMessage);
			textViewTakeConfMessage.setTypeface(Fonts.mavenRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(deliveryId);
			textMessage.setText(collectCash);
			textViewSummaryText.setText(activity.getResources().getString(R.string.order_summary));

			textViewTotalDeliveryValue.setText(""+total);
			textViewTotalDeliveredValue.setText(""+delivered);
			textViewTotalReturnedValue.setText(""+failed);


			textViewTakeConfMessage.setText(message);

			if(showTitle){
				textHead.setVisibility(View.VISIBLE);
			}
			else{
				textHead.setVisibility(View.GONE);
			}

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Fonts.mavenRegular(activity));
			btnOk.setText(activity.getResources().getString(R.string.submit_small));

			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.mavenRegular(activity));
			btnCancel.setText(activity.getResources().getString(R.string.cancel));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					listenerPositive.onClick(view);
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					listenerNegative.onClick(v);
				}
			});


			dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});


			dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (cancelable) {
						dismissAlertPopup();
					}
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void driverEarningPopup(Activity activity, String heading, String earning, String message, boolean cancellable, boolean okVisible) {
		try {
			dismissAlertPopup();

			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_show_average_sallary);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(cancellable);
			dialog.setCanceledOnTouchOutside(cancellable);

			CardView card1 = (CardView) dialog.findViewById(R.id.card1);
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessageEarnings = (TextView) dialog.findViewById(R.id.textMessageEarnings);
			textMessageEarnings.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage2 = (TextView) dialog.findViewById(R.id.textMessage2);
			textMessage2.setTypeface(Fonts.mavenRegular(activity));
			textMessage2.setText(activity.getString(R.string.keep_jugnoo_on, activity.getString(R.string.appname)));


			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(heading);
			textMessage.setText(message);
			if("".equalsIgnoreCase(earning)){
				textMessageEarnings.setVisibility(View.GONE);
			} else {
				textMessageEarnings.setVisibility(View.VISIBLE);
				textMessageEarnings.setText(activity.getResources().getString(R.string.rupee)+" "+earning+"/"+activity.getResources().getString(R.string.day));
			}


			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
			btnOk.setVisibility(okVisible ? View.VISIBLE : View.GONE);
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}

			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
