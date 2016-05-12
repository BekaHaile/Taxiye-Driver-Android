package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.Button;

/**
 * Created by aneeshbansal on 10/05/16.
 */
public class CustomButtonView extends Button {
	public CustomButtonView(Context context) {
		super(context);
		demo();
	}

	public CustomButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		demo();
	}

	public CustomButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		demo();
	}

	private void demo(){
		ViewTreeObserver vto = CustomButtonView.this.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (1 < CustomButtonView.this.getLineCount()) {
					CustomButtonView.this.setTextSize(TypedValue.COMPLEX_UNIT_PX, CustomButtonView.this.getTextSize() - 2);
				}
			}
		});
	}
}
