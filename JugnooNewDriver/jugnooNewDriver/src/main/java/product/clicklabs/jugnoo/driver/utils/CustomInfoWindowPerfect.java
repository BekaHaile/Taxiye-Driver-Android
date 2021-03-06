package product.clicklabs.jugnoo.driver.utils;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import product.clicklabs.jugnoo.driver.R;

/**
 * Class for displaying custom info window for marker
 * @author shankar
 *
 */
public class CustomInfoWindowPerfect implements InfoWindowAdapter {
	View mymarkerview;
	String titleStr;
	String snippetStr;

	public CustomInfoWindowPerfect(Activity activity, String title, String snippet) {
		mymarkerview = activity.getLayoutInflater().inflate(
				R.layout.info_window_custom, null);
		this.titleStr = title;
		this.snippetStr = snippet;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		render(marker, mymarkerview);
		return mymarkerview;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	void render(Marker marker, View view) {

		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText("" + titleStr);

		TextView snippet = (TextView) view.findViewById(R.id.snippet);
		snippet.setText("" + snippetStr);

//		title.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Utils.openCallIntent(, Data.userData.driverSupportNumber);
//			}
//		});

	}
}


