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
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;

public class DialogPopup {

	
	
	public DialogPopup(){
	}
	
	public static Dialog dialog;
	public static void alertPopup(Activity activity, String title, String message) {
		try {
			dismissAlertPopup();
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}
			
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_one_button);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
			textHead.setText(title);
			textMessage.setText(message);
			
			textHead.setVisibility(View.GONE);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
			
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

	public static void alertPopupBold(Activity activity, String title, String message) {
		try {
			dismissAlertPopup();
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}

			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_one_button);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
//									textMessage.setTextSize(22f * ASSL.Xscale());

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);

			textHead.setVisibility(View.GONE);

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
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
		try {
			dismissAlertPopup();
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}
			
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_one_button);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
			textHead.setText(title);
			textMessage.setText(message);
			
			textHead.setVisibility(View.GONE);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
			
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

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));

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



			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));

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

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
			ImageView imageViewAlert = (ImageView) dialog.findViewById(R.id.imageViewAlert);

			imageViewAlert.setImageResource(resId);

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);

			textHead.setVisibility(View.GONE);

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));

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
	
	
	public static void alertPopupTwoButtonsWithListeners(Activity activity, String title, String message, String okText, String canceltext, 
			final View.OnClickListener listenerPositive, final View.OnClickListener listenerNegative, final boolean cancelable, final boolean showTitle) {
		try {
			dismissAlertPopup();
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_two_buttons);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(cancelable);

			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog
					.findViewById(R.id.textMessage);
			textMessage.setTypeface(Data.latoRegular(activity));

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
			btnOk.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			if(!"".equalsIgnoreCase(okText)){
				btnOk.setText(okText);
			}
			
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Data.latoRegular(activity));
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



    public static void alertPopupThreeButtonsWithListeners(Activity activity, String title, String message, String okText, String neutralText, String canceltext,
                                                         final View.OnClickListener listenerPositive, final View.OnClickListener listenerNeutral, final View.OnClickListener listenerNegative,
                                                           final boolean cancelable, final boolean showTitle) {
        try {
            dismissAlertPopup();
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            dialog.setContentView(R.layout.dialog_custom_three_buttons);

            FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
            new ASSL(activity, frameLayout, 1134, 720, true);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(cancelable);

            TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
            textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
            TextView textMessage = (TextView) dialog
                .findViewById(R.id.textMessage);
            textMessage.setTypeface(Data.latoRegular(activity));

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
            btnOk.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
            if(!"".equalsIgnoreCase(okText)){
                btnOk.setText(okText);
            }

            Button btnNeutral = (Button) dialog.findViewById(R.id.btnNeutral);
            btnNeutral.setTypeface(Data.latoRegular(activity));
            if(!"".equalsIgnoreCase(neutralText)){
                btnNeutral.setText(neutralText);
            }

            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(Data.latoRegular(activity));
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

            btnNeutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    listenerNeutral.onClick(view);
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
			
			FrameLayout frameLayout = (FrameLayout) progressDialog.findViewById(R.id.rv);
			new ASSL((Activity)context, frameLayout, 1134, 720, false);
			
			TextView messageText = (TextView) progressDialog.findViewById(R.id.textView1); messageText.setTypeface(Data.latoRegular(context));
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

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL((Activity)context, frameLayout, 1134, 720, false);

			TextView messageText = (TextView) dialog.findViewById(R.id.textView1); messageText.setTypeface(Data.latoRegular(context));
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
//			TextView textViewBanner = (TextView) dialog.findViewById(R.id.textViewBanner); textViewBanner.setTypeface(Data.latoRegular(activity));
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
		dialogBannerWithCancelListener(activity, message, null, 5000);
	}

	public static void dialogBannerWithCancelListener(Activity activity, String message, final View.OnClickListener onClickListener, long timeToDismiss){
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

			TextView textViewBanner = (TextView) dialog.findViewById(R.id.textViewBanner); textViewBanner.setTypeface(Data.latoRegular(activity));
			textViewBanner.setText(message);

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

	public static void dialogNewBanner(Activity activity, String message) {
		dialogBannerNewWithCancelListener(activity, message, null, 7000);
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

			TextView textViewBanner = (TextView) dialog.findViewById(R.id.textViewBanner); textViewBanner.setTypeface(Fonts.latoRegular(activity));
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
					//DialogPopup.dismissAlertPopup();
					dialog.dismiss();
				}
			}, timeToDismiss);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
