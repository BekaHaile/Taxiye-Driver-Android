package product.clicklabs.jugnoo.driver.utils

import android.app.Activity
import android.os.Build
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.clicklabs.jugnoo.driver.R

// ========================= VIEW VISIBILITY =========================

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
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
    beginTransaction().func().commit()
}

inline fun FragmentManager.inTransactionWithSharedTransition(view: View, func: FragmentTransaction.() -> FragmentTransaction) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        beginTransaction().addSharedElement(view, view.transitionName).func().commit()
    }
}

inline fun FragmentManager.inTransactionWithAnimation(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
            .func()
            .commit()
}

infix fun Int.with(x: Int) = this.or(x)
