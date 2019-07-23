package product.clicklabs.jugnoo.driver

import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.layout_switch_slide.view.*
import product.clicklabs.jugnoo.driver.utils.Log
import product.clicklabs.jugnoo.driver.utils.Utils

class SlidingSwitch(var view: View, var callbackSlideOnOff: CallbackSlideOnOff) {

    var paramF = view.viewSlide.layoutParams as RelativeLayout.LayoutParams
    var animDuration = 150
    init {
        setUpSwitcher()
    }
    var isLeft = true
    var movePx = 0f
    fun setUpSwitcher() {
        view.viewSlide.setOnTouchListener { v, event -> kotlin.run {
            Log.i("SlidingSwitch", "setOnTouchListener event="+event.rawX+", view.x="+view.x)
            val rawX = event.rawX - getRelativeSliderLeftMargin() - Utils.dpToPx(view.context, 4f) - view.x //- getViewSlideLeftMargin()
            when(event.action) {
                MotionEvent.ACTION_DOWN -> kotlin.run {
                    movePx = event.rawX
                }
                MotionEvent.ACTION_MOVE -> kotlin.run {
                    if(rawX > view.viewSlide.width/2 && rawX < view.switchContainer.width - view.viewSlide.getWidth()/2) {
                        paramF.leftMargin = (layoutX(rawX)).toInt()
                        paramF.marginStart = (layoutX(rawX)).toInt()
                        view.switchContainer.updateViewLayout(view.viewSlide, paramF)
                    }
                }
                MotionEvent.ACTION_UP -> kotlin.run {
                    if((rawX) < (view.switchContainer.width)*0.5f) {
                        setSlideLeft()
                        callbackSlideOnOff.onClickStandAction(SlideDirection.LEFT.i)
                    } else {
                        setSlideRight()
                        callbackSlideOnOff.onClickStandAction(SlideDirection.RIGHT.i)
                    }
                }
            }
            true
            }
        }
        view.viewSlide.setOnClickListener {
            toggle()
        }
        view.switchContainer.setOnClickListener{
            toggle()
        }
    }


    fun getRelativeSliderLeftMargin(): Int {
        val layoutParams = view.switchContainer.layoutParams as RelativeLayout.LayoutParams
        return layoutParams.marginStart
    }
    fun getSwitchContainerWidth(): Int {
        val layoutParams = view.switchContainer.layoutParams as RelativeLayout.LayoutParams
        return layoutParams.width
    }
    fun getViewSlideLeftMargin(): Int {
        val layoutParams = view.viewSlide.layoutParams as RelativeLayout.LayoutParams
        return layoutParams.marginStart
    }
    fun getViewSlideWidth(): Float {
        val layoutParams = view.viewSlide.layoutParams as RelativeLayout.LayoutParams
        return layoutParams.width.toFloat()
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

    fun setSlideLeft() {
        animateSliderButton(paramF.getMarginStart(), Utils.dpToPx(view.context, 2f).toFloat())
        isLeft = true
    }

    fun setSlideRight() {
        animateSliderButton(paramF.getMarginStart(), (view.switchContainer.measuredWidth - view.viewSlide.measuredWidth.toFloat())-Utils.dpToPx(view.context, 2f))
        isLeft = false
    }

    fun toggle(){
        if(isLeft){
            setSlideRight()
            callbackSlideOnOff.onClickStandAction(SlideDirection.RIGHT.i)
        } else {
            setSlideLeft()
            callbackSlideOnOff.onClickStandAction(SlideDirection.LEFT.i)
        }

    }

    fun toggleWithoutAction() {
        if(isLeft){
            setSlideRight()
        } else {
            setSlideLeft()
        }
    }

}

interface CallbackSlideOnOff {
    fun onClickStandAction(slideDir: Int)
}
enum class SlideDirection(var i:Int ) {
    LEFT(0),
    RIGHT(1)
}