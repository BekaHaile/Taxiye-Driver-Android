package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by aneeshbansal on 10/05/16.
 */
public class CustomTextVeiw extends TextView {

	public CustomTextVeiw(Context context) {
		super(context);
		demo();

	}

	public CustomTextVeiw(Context context, AttributeSet attrs) {
		super(context, attrs);
		demo();
	}

	public CustomTextVeiw(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		demo();
	}

	private void demo(){
		ViewTreeObserver vto = CustomTextVeiw.this.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				/*Layout l = CustomTextVeiw.this.getLayout();
				if (l != null) {
					int lines = l.getLineCount();
					if (lines > 0)
						if (l.getEllipsisCount(lines - 1) > 0)
							Log.i("size changed", "size changed...");
							CustomTextVeiw.this.setTextSize(CustomTextVeiw.this.getTextSize()-18);
				}*/

				if (1 < CustomTextVeiw.this.getLineCount()) {
					CustomTextVeiw.this.setTextSize(TypedValue.COMPLEX_UNIT_PX, CustomTextVeiw.this.getTextSize() - 2);
				}
			}
		});
	}

}
