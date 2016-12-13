package product.clicklabs.jugnoo.driver.sticky;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.GeniePositonsSaver;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.SimpleAnimator;

/**
 * Created by aneeshbansal on 19/01/16.
 */
public class GeanieView extends Service {

	private WindowManager windowManager;

	private RelativeLayout root;
	private View convertView;
	private ImageView jugnooHead1, removeImg;
	private DriverScreenMode mode;
	private RelativeLayout removeView;
	private RelativeLayout relativeLayoutCloseInner;
	private AbsoluteLayout absoluteLayoutClose;
	private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
	private Point szWindow = new Point();
	private boolean closeAnimating = false;
	private int appsAnim = 0;
	private boolean isLeft = true;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		Pair<String, String> accPair = JSONParser.getAccessTokenPair(GeanieView.this);
		if (!"".equalsIgnoreCase(accPair.first)) {
			if (Prefs.with(GeanieView.this).getInt(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_OFFLINE.getOrdinal())
					!= DriverScreenMode.D_OFFLINE.getOrdinal()) {

				FlurryAgent.init(this, Data.FLURRY_KEY);
				FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
				FlurryAgent.onEvent("Navigation started");




				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

				convertView = inflater.inflate(R.layout.chathead, null);

				removeView = (RelativeLayout) inflater.inflate(R.layout.remove, null);
				WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
						WindowManager.LayoutParams.MATCH_PARENT,
						WindowManager.LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.TYPE_PHONE,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
						PixelFormat.TRANSLUCENT);

				paramRemove.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//				jugnooHead = (ImageView) convertView.findViewById(R.id.chathead_img);
				removeImg = (ImageView) removeView.findViewById(R.id.remove_img);

				removeView.setVisibility(View.GONE);
				relativeLayoutCloseInner = (RelativeLayout) removeView.findViewById(R.id.relativeLayoutCloseInner);
				absoluteLayoutClose = (AbsoluteLayout) removeView.findViewById(R.id.absoluteLayoutClose);
				removeImg = (ImageView) removeView.findViewById(R.id.remove_img);

				try {
					windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//					final WindowManager.LayoutParams paramsA = new WindowManager.LayoutParams(
//							156,
//							156,
//							WindowManager.LayoutParams.TYPE_PHONE,
//							WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//							PixelFormat.TRANSLUCENT);
//
//					paramsA.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
//		paramsA.x = 610;
//		paramsA.y = 450;
					WindowManager.LayoutParams paramsNew = new WindowManager.LayoutParams(
							WindowManager.LayoutParams.WRAP_CONTENT,
							WindowManager.LayoutParams.WRAP_CONTENT,
							WindowManager.LayoutParams.TYPE_PHONE,
							WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
							PixelFormat.TRANSLUCENT);
					paramsNew.gravity = Gravity.TOP | Gravity.LEFT;
					paramsNew.x = 0;
					paramsNew.y = 100;

					windowManager.addView(convertView, paramsNew);
					new ASSL(this, 1134, 720, true);
					ASSL.DoMagic(convertView);
					windowManager.updateViewLayout(convertView, convertView.getLayoutParams());

				} catch (Exception e) {
					e.printStackTrace();
				}

				windowManager.addView(removeView, paramRemove);
				ASSL.DoMagic(removeView);

				windowManager.updateViewLayout(removeView, removeView.getLayoutParams());
				AbsoluteLayout.LayoutParams imageViewCloseParams = (AbsoluteLayout.LayoutParams) removeImg.getLayoutParams();
				imageViewCloseParams.x = (int) (310 * ASSL.Xscale());
				imageViewCloseParams.y = (int) (50 * ASSL.Yscale());
				absoluteLayoutClose.updateViewLayout(removeImg, imageViewCloseParams);
				removeView.setVisibility(View.GONE);



				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					windowManager.getDefaultDisplay().getSize(szWindow);
				} else {
					int w = windowManager.getDefaultDisplay().getWidth();
					int h = windowManager.getDefaultDisplay().getHeight();
					szWindow.set(w, h);
				}


				convertView.setOnTouchListener(new View.OnTouchListener() {
					long time_start = 0, time_end = 0;
					boolean isLongclick = false, inBounded = false;
					int remove_img_width = 0, remove_img_height = 0;

					Handler handler_longClick = new Handler();
					Runnable runnable_longClick = new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							isLongclick = true;
							showCloseView();
						}
					};

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) convertView.getLayoutParams();

						int x_cord = (int) event.getRawX();
						int y_cord = (int) event.getRawY();
						int x_cord_Destination, y_cord_Destination;

						switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								time_start = System.currentTimeMillis();
								handler_longClick.postDelayed(runnable_longClick, 500);

								remove_img_width = removeImg.getLayoutParams().width;
								remove_img_height = removeImg.getLayoutParams().height;

								x_init_cord = x_cord;
								y_init_cord = y_cord;

								x_init_margin = layoutParams.x;
								y_init_margin = layoutParams.y;


								break;
							case MotionEvent.ACTION_MOVE:
								int x_diff_move = x_cord - x_init_cord;
								int y_diff_move = y_cord - y_init_cord;

								float diff = Math.max(x_diff_move, y_diff_move);

								if (diff > Math.abs(5)) {
									if (convertView.getVisibility() == View.VISIBLE) {
//										convertView.setVisibility(View.GONE);
//										clearAnims();
//										clearAnims2();
									}
								}

								x_cord_Destination = x_init_margin + x_diff_move;
								y_cord_Destination = y_init_margin + y_diff_move;

								if (isLongclick) {

									// 1. We need to clear the animations first.
//									clearAnims();
//									clearAnims2();
									// 2. We check if the chat head is in the range of trash icon or not.
									int x_bound_left = (szWindow.x - removeImg.getWidth()) / 2 - (int) (ASSL.Xscale() * 20);
									int x_bound_right = (szWindow.x + removeImg.getWidth()) / 2 + (int) (ASSL.Xscale() * 20);

									int y_bound_top = szWindow.y - (removeImg.getHeight() + getStatusBarHeight()) - (int) (ASSL.Yscale() * 200);

									if ((x_cord_Destination >= x_bound_left && x_cord_Destination <= x_bound_right) && y_cord_Destination >= y_bound_top) {
										inBounded = true;
										Log.d("Chat head is now", "Inbounded");

										// this is working perfectly fine
										layoutParams.x = (szWindow.x - convertView.getWidth()) / 2;
										layoutParams.y = szWindow.y - (removeImg.getHeight() + getStatusBarHeight() + getStatusBarHeight());


										final AbsoluteLayout.LayoutParams param_remove = (AbsoluteLayout.LayoutParams) removeImg.getLayoutParams();
										param_remove.height = (int) (remove_img_height * 1.5);
										param_remove.width = (int) (remove_img_width * 1.5);

										int x_cord_remove = (int) (((szWindow.x - (remove_img_width + remove_img_width / 2)) / 2));
										int y_cord_remove = (int) (absoluteLayoutClose.getHeight() / 2 - remove_img_height);
										param_remove.x = x_cord_remove;
										param_remove.y = y_cord_remove;
										absoluteLayoutClose.updateViewLayout(removeImg, param_remove);


										// chak de phatte !

										removeImg.setVisibility(View.VISIBLE);


										windowManager.updateViewLayout(convertView, layoutParams);
										break;
									} else {
										inBounded = false;
										removeImg.getLayoutParams().height = remove_img_height;
										removeImg.getLayoutParams().width = remove_img_width;

										moveCloseView(x_cord_Destination, y_cord_Destination);
									}

								}

								layoutParams.x = x_cord_Destination;
								layoutParams.y = y_cord_Destination;

								windowManager.updateViewLayout(convertView, layoutParams);

								break;
							case MotionEvent.ACTION_UP:

								isLongclick = false;

								handler_longClick.removeCallbacks(runnable_longClick);
								hideCloseView(remove_img_height, remove_img_width);

								if (inBounded) {
									stopService(new Intent(GeanieView.this, GeanieView.class));
									inBounded = false;
									break;
								}


								int x_diff = x_cord - x_init_cord;
								int y_diff = y_cord - y_init_cord;

								x_cord_Destination = x_init_margin + x_diff;
								y_cord_Destination = y_init_margin + y_diff;

								int x_start;
								x_start = x_cord_Destination;

								int BarHeight = getStatusBarHeight();
								if (y_cord_Destination < 0) {
									y_cord_Destination = 0;
								} else if (y_cord_Destination + (convertView.getHeight() + BarHeight) > szWindow.y) {
									y_cord_Destination = szWindow.y - (convertView.getHeight() + BarHeight);
								}
								layoutParams.y = y_cord_Destination;

								updateAnimLayoutParams();

								if (Math.abs(x_diff) < 10 && Math.abs(y_diff) < 10) {
									time_end = System.currentTimeMillis();
									//showAllJugnooApps();

//                            Intent intent = new Intent(GenieService.this, GenieActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);

//									showJeaniePopup();
									if (HomeActivity.appInterruptHandler != null) {
										Intent newIntent = new Intent(GeanieView.this, HomeActivity.class);
										newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
										newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										startActivity(newIntent);
									} else {
										Intent homeScreen = new Intent(GeanieView.this, SplashNewActivity.class);
										homeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										startActivity(homeScreen);
									}


								} else {
									resetGeniePostion(x_start);
								}

								inBounded = false;


								break;
							default:
								break;
						}
						return true;
					}
				});
			}
		}
	}


	// this function calculates the status bar height
	private int getStatusBarHeight() {
		int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
		return statusBarHeight;
	}

	private void resetGeniePostion(int x_cord_now) {
		int w = convertView.getWidth();

		if (convertView.getVisibility() == View.VISIBLE) {
//			convertView.setVisibility(View.GONE);
//			clearAnims();
//			clearAnims2();
		}

		if (x_cord_now == 0 || x_cord_now == szWindow.x - w) {

		} else if (x_cord_now + w / 2 <= szWindow.x / 2) {
			isLeft = true;
			moveToLeft(x_cord_now);

		} else if (x_cord_now + w / 2 > szWindow.x / 2) {
			isLeft = false;
			moveToRight(x_cord_now);
		}
	}

	public void moveCloseView(int x, int y) {
		if (!closeAnimating) {
			int scaledX = x / 10;
			int scaledY = y / 10;
			AbsoluteLayout.LayoutParams imageViewCloseParams = (AbsoluteLayout.LayoutParams) removeImg.getLayoutParams();
			imageViewCloseParams.x = (int) ((288 * ASSL.Xscale()) + scaledX);
			imageViewCloseParams.y = (int) (scaledY);
			absoluteLayoutClose.updateViewLayout(removeImg, imageViewCloseParams);
		}
	}


	private void updateAnimLayoutParams() {
		if (getChatHeadParams().y < szWindow.y / 2) {
			final WindowManager.LayoutParams paramsA = (WindowManager.LayoutParams) convertView.getLayoutParams();
			paramsA.x = getChatHeadParams().x + (int) (ASSL.Xscale() * 10);
			paramsA.y = getChatHeadParams().y;
			windowManager.updateViewLayout(convertView, paramsA);
			appsAnim = -1;
		} else {
			final WindowManager.LayoutParams paramsA = (WindowManager.LayoutParams) convertView.getLayoutParams();
			paramsA.x = getChatHeadParams().x + (int) (ASSL.Xscale() * 10);
			paramsA.y = ((convertView.getHeight() + getChatHeadParams().y - convertView.getHeight()) > 0) ? (convertView.getHeight() + getChatHeadParams().y - convertView.getHeight()) : 0;
			windowManager.updateViewLayout(convertView, paramsA);
			appsAnim = 1;
		}
	}

	private WindowManager.LayoutParams getChatHeadParams() {
		return (WindowManager.LayoutParams) convertView.getLayoutParams();
	}


	// this function hides the trash icon and the shadow from bottom when Genie is untouched
	public void hideCloseView(int remove_img_height, int remove_img_width) {

		removeView.setVisibility(View.GONE);
		removeView.setPadding(0, 0, 0, 0);
		removeImg.getLayoutParams().height = remove_img_height;
		removeImg.getLayoutParams().width = remove_img_width;
		SimpleAnimator mShadowFadeOut = new SimpleAnimator(relativeLayoutCloseInner, R.anim.fade_out);
		SimpleAnimator mHideAnim = new SimpleAnimator(removeImg, R.anim.slide_down);
		mShadowFadeOut.startAnimation();
		mHideAnim.startAnimation(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				closeAnimating = true;
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				closeAnimating = false;
//				closeView.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}


	@Override
	public void onDestroy(){
		FlurryAgent.onEndSession(this);
		try {
			windowManager.removeViewImmediate(convertView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showCloseView() {
		removeView.setVisibility(View.VISIBLE);
		SimpleAnimator mShadowFadeIn = new SimpleAnimator(relativeLayoutCloseInner, R.anim.fade_in);
		SimpleAnimator mShowAnim = new SimpleAnimator(removeImg, R.anim.slide_up);
		mShadowFadeIn.startAnimation();
		mShowAnim.startAnimation(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				closeAnimating = true;
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				closeAnimating = false;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	private void moveToLeft(int x_cord_now) {

		final int x = x_cord_now;
		product.clicklabs.jugnoo.driver.utils.Log.e("xcordleft", String.valueOf(x));
		final long start = System.currentTimeMillis();
		new CountDownTimer(280, 1) {
			WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) convertView.getLayoutParams();

			public void onTick(long t) {
				long step = (280 - t);
				Log.e("bounceValueLeft", String.valueOf((int) (double) bounceValue(step, x) + "    "+step+"    "+x+"     "+t));
				Log.e("bounceValueLeft", String.valueOf((int) (x-step)));
				mParams.x = (int) (double) bounceValue(step, x);
//				int xPraram = (int) (x - step);
//				if(xPraram >= 0){
//					mParams.x = xPraram;
//				}

				windowManager.updateViewLayout(convertView, mParams);
			}

			public void onFinish() {
				Log.e("param x before finish", String.valueOf(mParams.x));
				mParams.x = 0;
				windowManager.updateViewLayout(convertView, mParams);

				saveGenieParams(mParams);

//				updateAnimLayoutParams();
				Log.v("timeTaken", "left " + (System.currentTimeMillis() - start));
			}
		}.start();

	}


	private void moveToRight(int x_cord_now) {
		final int x = x_cord_now;
		product.clicklabs.jugnoo.driver.utils.Log.e("xcordright", String.valueOf(x));
		final long start = System.currentTimeMillis();
		new CountDownTimer(280, 1) {
			WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) convertView.getLayoutParams();

			public void onTick(long t) {
				long step = (280 - t);

				Log.e("bounceValueright", String.valueOf((int) (double) bounceValue(step, x) + "    "+step+"    "+x));
				Log.e("bounceright_convert", String.valueOf(convertView.getWidth()));
				Log.e("szWindow", String.valueOf(szWindow.x));
				Log.e("paramX", String.valueOf( szWindow.x + (int) (double
						) bounceValue(step, x) - convertView.getWidth()));
				Log.e("paramXNEW", String.valueOf( szWindow.x + (int) (x-step)- convertView.getWidth()));

//				mParams.x = szWindow.x - (int) (x-step)- convertView.getWidth();
				mParams.x =  szWindow.x + (int) (double) bounceValue(step, x) - convertView.getWidth();
//				mParams.x = szWindow.x + (int) finalBonce(step, x);
				windowManager.updateViewLayout(convertView, mParams);
			}

			public void onFinish() {
				Log.e("param x before finish", String.valueOf(mParams.x));
				mParams.x = szWindow.x - convertView.getWidth();
				Log.e("param x after finish", String.valueOf(mParams.x));
				windowManager.updateViewLayout(convertView, mParams);

				saveGenieParams(mParams);

//				updateAnimLayoutParams();
				Log.v("timeTaken", "right " + (System.currentTimeMillis() - start));
			}
		}.start();

	}


	private double bounceValue(long step, long scale) {
		double value = scale * java.lang.Math.exp(-10 * step) * java.lang.Math.cos(0 * step);
		return value;
	}

	public void saveGenieParams(WindowManager.LayoutParams params) {
		GeniePositonsSaver.writeGenieParams(params.x, params.y);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}