package product.clicklabs.jugnoo.driver

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_switch_slide.view.*

class SlidingSwitch(context: Context,var view : View) {

    var paramF = view.viewSlide.layoutParams as RelativeLayout.LayoutParams
    var animDuration = 150
    init {
        setUpSwitcher()
    }

    fun setUpSwitcher() {
        view.viewSlide.setOnTouchListener { v, event -> kotlin.run {
            when(event.action) {
                MotionEvent.ACTION_DOWN -> kotlin.run {

                }
                MotionEvent.ACTION_MOVE -> kotlin.run {
                    if(event.x - getRelativeSliderLeftMargin() > view.viewSlide.width/2 && event.rawX - getRelativeSliderLeftMargin() < view.switchContainer.getWidth() - view.viewSlide.getWidth()/2) {
                        paramF.leftMargin = (layoutX(event.rawX - getRelativeSliderLeftMargin())).toInt()
                        paramF.marginStart = (layoutX(event.rawX - getRelativeSliderLeftMargin())).toInt()
                        view.switchContainer.updateViewLayout(view.viewSlide, paramF)
                    }
                }
                MotionEvent.ACTION_UP -> kotlin.run {
                    if((event.rawX - getRelativeSliderLeftMargin()) < (view.switchContainer.width - view.viewSlide.width/2)*0.6f) {
                        setSlideInitial()
                    } else {
                        animateSliderButton(paramF.getMarginStart(), (view.switchContainer.getWidth() - view.viewSlide.width.toFloat()))
                    }
                }
            }
            true
            }
        }
    }

    fun getRelativeSliderLeftMargin(): Int {
        val layoutParams = view.switchContainer.layoutParams as RelativeLayout.LayoutParams
        return layoutParams.marginStart
    }

    fun layoutX(rawX :Float) = (rawX - (sliderWidth()/2))

    fun sliderWidth(): Float {
        val layoutParams = view.viewSlide.layoutParams as RelativeLayout.LayoutParams
        return layoutParams.width.toFloat()
    }

    private fun animateSliderButton(currMargin: Int, newMargin: Float) {
        val diff = newMargin - currMargin.toFloat()
        val translateAnim = TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, diff,
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f)
        translateAnim.duration = animDuration.toLong()
        translateAnim.interpolator = AccelerateDecelerateInterpolator()
        translateAnim.fillAfter = false
        translateAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                view.viewSlide.clearAnimation()
                paramF.leftMargin = newMargin.toInt()
                paramF.setMarginStart(newMargin.toInt())
                view.switchContainer.updateViewLayout(view.viewSlide, paramF)
                view.viewSlide.setEnabled(true)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        view.viewSlide.clearAnimation()
        view.viewSlide.isEnabled = false
        view.viewSlide.startAnimation(translateAnim)
    }

    fun setSlideInitial() {
        animateSliderButton(paramF.getMarginStart(), 0f)
        view.viewSlide.setVisibility(View.VISIBLE)
    }
}