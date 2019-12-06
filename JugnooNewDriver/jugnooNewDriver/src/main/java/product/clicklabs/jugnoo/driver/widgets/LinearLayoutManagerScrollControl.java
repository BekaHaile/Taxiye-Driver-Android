package product.clicklabs.jugnoo.driver.widgets;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Created by aneeshbansal on 26/09/16.
 */
public class LinearLayoutManagerScrollControl extends LinearLayoutManager {
	private boolean isScrollEnabled = true;

	public LinearLayoutManagerScrollControl(Context context) {
		super(context);
	}

	public void setScrollEnabled(boolean flag) {
		this.isScrollEnabled = flag;
	}

	@Override
	public boolean canScrollVertically() {
		//Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
		return isScrollEnabled && super.canScrollVertically();
	}
}
