package product.clicklabs.jugnoo.driver.altmetering.dialog

import android.app.Activity
import android.app.Dialog
import android.support.constraint.ConstraintLayout
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import product.clicklabs.jugnoo.driver.MyApplication
import product.clicklabs.jugnoo.driver.altmetering.db.MeteringDatabase
import product.clicklabs.jugnoo.driver.altmetering.model.Waypoint
import product.clicklabs.jugnoo.driver.utils.Utils


class AltMeteringInfoDialog{


    var meteringDb: MeteringDatabase? = null
        get() {
            if(field == null){
                field = MeteringDatabase.getInstance(MyApplication.getInstance())
            }
            return field
        }

    lateinit var tvMessage:TextView

    fun show(activity:Activity, engagementId:Int){

        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.window!!.attributes.windowAnimations = product.clicklabs.jugnoo.driver.R.style.Animations_LoadingDialogFade
        dialog.setContentView(product.clicklabs.jugnoo.driver.R.layout.dialog_banner_mid)

        val layoutParams = dialog.window!!.attributes
        layoutParams.dimAmount = 0.6f
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        val constraintLayout = dialog.findViewById<ConstraintLayout>(product.clicklabs.jugnoo.driver.R.id.constraintLayout)
        constraintLayout.setOnClickListener{
            dialog.dismiss()
        }

        tvMessage = dialog.findViewById<View>(product.clicklabs.jugnoo.driver.R.id.tvMessage) as TextView
        tvMessage.movementMethod = ScrollingMovementMethod()
        tvMessage.maxHeight = Utils.dpToPx(activity, 400f)


        dialog.show()
        readAsync(engagementId)
    }

    fun readAsync(engagementId:Int){

        val waypointsObservable: Observable<List<Waypoint>> = Observable.fromCallable {
            meteringDb!!.getMeteringDao().getAllWaypoints(engagementId)
        }

        waypointsObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Waypoint>>{
                    override fun onComplete() {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onSubscribe(d: Disposable) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onNext(t: List<Waypoint>) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onError(e: Throwable) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })

    }





}