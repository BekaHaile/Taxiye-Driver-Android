package product.clicklabs.jugnoo.utils;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogPopup {

	
	
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
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			
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
	
	public static void alertPopupHtml(Activity activity, String title, String message) {
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
			textMessage.setText(Html.fromHtml(message));
			
			textHead.setVisibility(View.GONE);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			
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
	
	
	public static void alertPopupLeftOriented(Activity activity, String title, String message) {
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
			
			textMessage.setGravity(Gravity.LEFT);
			
			textHead.setText(title);
			textMessage.setText(message);
			
			
			textHead.setVisibility(View.GONE);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			
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
	
	
	public static void dismissAlertPopup(){
		try{
			if(dialog != null && dialog.isShowing()){
				dialog.dismiss();
			}
		}catch(Exception e){
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
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			
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
	
	
	
	public static void dialogBanner(Activity activity, String message) {
		try {
			dismissAlertPopup();
			
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
				}
			});
			
			dialog.show();
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					DialogPopup.dismissAlertPopup();
				}
			}, 5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public static ProgressDialog progressDialog;
	
	/**
	 * Displays custom loading dialog
	 * @param c application context
	 * @param msg string message to show in dialog
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
			progressDialog.setContentView(R.layout.dialog_loading_box);
			
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
		        alertDialogPrepare.setTitle("Google Play Services Error");
		        alertDialogPrepare.setCancelable(false);
		 
		        // Setting Dialog Message
		        alertDialogPrepare.setMessage("Google Play services not found or outdated. Please install Google Play Services?");
		 
		        // On pressing Settings button
		        alertDialogPrepare.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	dialog.dismiss();
		            	Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=com.google.android.gms"));
						mContext.startActivity(intent);
		            }
		        });
		 
		        // on pressing cancel button
		        alertDialogPrepare.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
		        alertDialogPrepare.setTitle("Location Settings");
		        alertDialogPrepare.setCancelable(false);
		 
		        // Setting Dialog Message
		        alertDialogPrepare.setMessage("Location is not enabled. Do you want to enable it from settings menu?");
		 
		        // On pressing Settings button
		        alertDialogPrepare.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		            	mContext.startActivity(intent);
		            	dialog.dismiss();
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
	
	
}
