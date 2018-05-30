package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import product.clicklabs.jugnoo.driver.R;

/**
 * Created by aneeshbansal on 13/09/16.
 */
public class CustomMarkerView extends MarkerView {

	private TextView tvContent;
	private Listener mListener;
	private int mLayoutX;
	private int mLayoutY;
	private int mMarkerVisibility;

	public CustomMarkerView (final Context context, int layoutResource, Listener listener) {
		super(context, layoutResource);
		/*setLayoutParams(new LayoutParams(120, 64));
		ASSL.DoMagic(this);*/
		mListener = listener;
		// this markerview only displays a textview
		tvContent = (TextView) findViewById(R.id.tvContent);

	}

	// callbacks everytime the MarkerView is redrawn, can be used to update the
	// content (user-interface)
	@Override
	public void refreshContent(Entry e, Highlight highlight) {
	//	DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#", new DecimalFormatSymbols(Locale.ENGLISH));
	//	tvContent.setText(getResources().getString(R.string.rupee)+ decimalFormatNoDecimal.format(e.getVal())); // set the entry-value as the display text
		String currencyUnit =null;
		if(e.getData() instanceof String){
			currencyUnit = (String) e.getData();
		}
		tvContent.setText(Utils.formatCurrencyValue(currencyUnit,e.getVal()));
	}

	@Override
	public int getXOffset(float xpos) {
		// this will center the marker-view horizontally
		mLayoutX = (int) (xpos - (getWidth() / 2));
		return -(getWidth() / 2);
	}

	@Override
	public int getYOffset(float ypos) {
		// this will cause the marker-view to be above the selected value
		mLayoutY = (int) (ypos - getWidth());
		return -getHeight()-10;
	}

	public interface Listener {
		/**
		 * A callback with the x,y position of the marker
		 * @param x the x in pixels
		 * @param y the y in pixels
		 */
		void onMarkerViewLayout(int x, int y);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mMarkerVisibility == View.VISIBLE) mListener.onMarkerViewLayout(mLayoutX, mLayoutY);
	}

	public void setMarkerVisibility(int markerVisibility) {
		mMarkerVisibility = markerVisibility;
	}

}
