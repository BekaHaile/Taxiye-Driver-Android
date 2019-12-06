package product.clicklabs.jugnoo.driver.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

public class PrefixedEditText extends AppCompatEditText {

	private ColorStateList mPrefixTextColor;
	private TextDrawable textDrawable;

	public PrefixedEditText(Context context) {
		this(context, null);
	}

	public PrefixedEditText(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
	}

	public PrefixedEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPrefixTextColor = getTextColors();
	}

	public TextDrawable setPrefix(String prefix) {
		 textDrawable = new TextDrawable(prefix);
		setCompoundDrawables(textDrawable, null, null, null);
		return textDrawable;
	}

	public TextDrawable getTextDrawable() {
		return textDrawable;
	}

	public void setPrefixTextColor(int color) {
		mPrefixTextColor = ColorStateList.valueOf(color);
	}

	public void setPrefixTextColor(ColorStateList color) {
		mPrefixTextColor = color;
	}

	public class TextDrawable extends Drawable {
		private String mText = "";

		public TextDrawable(String text) {
			mText = text;
			setBounds(0, 0, (int) getPaint().measureText(mText) + 2, (int) getTextSize());
		}

		@Override
		public void draw(Canvas canvas) {
			Paint paint = getPaint();
			paint.setColor(mPrefixTextColor.getColorForState(getDrawableState(), 0));
			int lineBaseline = getLineBounds(0, null);
			canvas.drawText(mText, 0, canvas.getClipBounds().top + lineBaseline, paint);
		}

		@Override
		public void setAlpha(int alpha) {/* Not supported */}

		@Override
		public void setColorFilter(ColorFilter colorFilter) {/* Not supported */}

		@Override
		public int getOpacity() {
			return PixelFormat.OPAQUE;
		}
	}
}