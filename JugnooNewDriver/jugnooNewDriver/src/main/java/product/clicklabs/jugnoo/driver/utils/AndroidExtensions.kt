package product.clicklabs.jugnoo.driver.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.R

// ========================= VIEW VISIBILITY =========================

const val TAG = "AndroidExtensions"

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun  View.isGone():Boolean{
    return  visibility==View.GONE
}

fun View.isVisible():Boolean {
    return visibility == View.VISIBLE
}

// ========================= VIEW INFLATE =========================

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

// ========================= BINDING FUNCTIONS =========================

fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return lazy(LazyThreadSafetyMode.NONE) { findViewById(res) as T }
}

fun <T : View> Fragment.bind(@IdRes res: Int, view: View?): Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return lazy(LazyThreadSafetyMode.NONE) { view?.findViewById(res) as T }
}


// ========================= FRAGMENT MANAGER TRANSACTIONS =========================

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commitAllowingStateLoss()
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
inline fun FragmentManager.inTransactionWithSharedTransition(view: View, func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().addSharedElement(view, view.transitionName).func().commit()
}

inline fun FragmentManager.inTransactionWithAnimation(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
            .func()
            .commitAllowingStateLoss()
}

infix fun Int.with(x: Int) = this.or(x)


// ========================= NETWORK CHECK =========================

fun Activity.withNetwork(block: () -> Unit, showDialog: Boolean = true, notConnected: () -> Unit = {} ) {
    if (AppStatus.getInstance(this).isOnline(this)) block.invoke()
    else {
        if (showDialog) DialogPopup.alertPopup(this, "", Data.CHECK_INTERNET_MSG)
        notConnected.invoke()
    }
}

 fun Int.pxValue(context : Context,unit: Int = TypedValue.COMPLEX_UNIT_DIP): Int {
    return TypedValue.applyDimension(unit, this.toFloat(), context.resources.displayMetrics).toInt()
}