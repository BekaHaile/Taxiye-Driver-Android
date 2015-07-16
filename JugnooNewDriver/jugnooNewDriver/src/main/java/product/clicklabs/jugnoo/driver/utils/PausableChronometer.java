package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;

public class PausableChronometer extends Chronometer {

	  public long eclipsedTime;
	  public boolean isRunning;

	  public PausableChronometer(Context context) {
	    super(context);
	    init();
	  }

	  public PausableChronometer(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    init();
	  }

	  public PausableChronometer(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    init();
	  }

	  public void start() {
	    setBase(SystemClock.elapsedRealtime() - eclipsedTime);
	    super.start();
	    this.isRunning = true;
	  }

	  public void restart() {
	    stop();
	    this.eclipsedTime = 0l;
	    start();
	    
	  }

	  public void stop() {
		  if(this.isRunning){
			  this.eclipsedTime = SystemClock.elapsedRealtime() - this.getBase();
			  super.stop();
			  this.isRunning = false;
		  }
	  }

	  private void init() {
	    this.eclipsedTime = 0l;
	    this.isRunning = false;
	    this.setOnChronometerTickListener(new OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer cArg) {
				long time = SystemClock.elapsedRealtime() - cArg.getBase();
				PausableChronometer.this.eclipsedTime = time;
				int h = (int) (time / 3600000);
				int m = (int) (time - h * 3600000) / 60000;
				int s = (int) (time - h * 3600000 - m * 60000) / 1000;
				String hh = h < 10 ? "0" + h : "" + h;
				String mm = m < 10 ? "0" + m : "" + m;
				String ss = s < 10 ? "0" + s : "" + s;
				cArg.setText(hh + ":" + mm + ":" + ss);
			}
		});
	  }
	}
