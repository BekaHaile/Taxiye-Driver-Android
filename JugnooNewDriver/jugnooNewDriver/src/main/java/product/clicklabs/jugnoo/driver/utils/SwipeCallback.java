package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextPaint;
import android.view.View;

import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;

abstract public class SwipeCallback extends ItemTouchHelper.Callback {

    HomeActivity homeActivity;
    private Paint mClearPaint;
    private ColorDrawable mBackground;
    private int backgroundColor;
    private Drawable deleteDrawable;
    private int intrinsicWidth;
    private int intrinsicHeight;


    public SwipeCallback(HomeActivity context) {
        homeActivity = context;
        mBackground = new ColorDrawable();
        backgroundColor = Color.parseColor("#ff5b29");
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        deleteDrawable = ContextCompat.getDrawable(homeActivity, R.drawable.ic_accept_ride);
        intrinsicWidth = deleteDrawable.getIntrinsicWidth();
        intrinsicHeight = deleteDrawable.getIntrinsicHeight();


    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        try {
            return homeActivity.offlineRequests.get(viewHolder.getAdapterPosition()).getCanAcceptRequest()==1?makeMovementFlags(0, ItemTouchHelper.LEFT):0;
        }
        catch (Exception e){
            return 0;
        }

    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        mBackground.setColor(backgroundColor);
        mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        mBackground.draw(c);

        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
        int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
        int deleteIconRight = itemView.getRight() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;


        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteDrawable.draw(c);



//        mClearPaint.setColor(Color.WHITE);
//        mClearPaint.setTextSize(40);
//        int xPos = (c.getWidth() / 2);
//        int yPos = (int) ((c.getHeight() / 2) - ((mClearPaint.descent() + mClearPaint.ascent()) / 2)) ;
//        c.drawText("Accept this ride", xPos, yPos, mClearPaint);


//        Paint paint = new Paint();
//        paint.setColor(Color.WHITE);
//
//        TextPaint textPaint = new TextPaint();
//        textPaint.setColor(Color.WHITE);
//        textPaint.setTypeface(Fonts.mavenBold(homeActivity));
//        textPaint.setTextSize(60);
//        textPaint.setTextAlign(Paint.Align.CENTER);
//        float textHeight = textPaint.descent() - textPaint.ascent();
//        float textOffset = (textHeight / 2) - textPaint.descent();
//
//        RectF bounds = new RectF(itemView.getX(), itemView.getY(), itemView.getX() + itemView.getWidth(), itemView.getY() + itemView.getHeight());
////        c.drawOval(bounds, paint);
//        c.drawText("Accept this ride", bounds.centerX(), bounds.centerY() + textOffset, textPaint);
//
//        Log.e("SwipeCallback", "c.getHeight="+c.getHeight()+", bounds h="+bounds.centerY() + textOffset);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, mClearPaint);

    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }
}
