package product.clicklabs.jugnoo.driver.dialogs

import android.app.Activity
import android.app.Dialog
import android.media.MediaPlayer
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.adapters.RingtoneModel
import product.clicklabs.jugnoo.driver.adapters.RingtoneSelectionAdapter
import product.clicklabs.jugnoo.driver.home.RingtoneTypes
import product.clicklabs.jugnoo.driver.utils.Prefs

object RingtoneSelectionDialog{

    var mediaPlayer:MediaPlayer? = null

    fun show(activity: Activity) {
        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
        with(dialog) {
            window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            setContentView(R.layout.dialog_ringtone_selection)
            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            layoutParams.gravity = Gravity.CENTER
            window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setCancelable(false)
            setCanceledOnTouchOutside(false)

            val btnCancel = findViewById<Button>(R.id.btnCancel)
            val btnOk = findViewById<Button>(R.id.btnOk)

            val rvRingtones = findViewById<RecyclerView>(R.id.rvRingtones)
            val ringtones = mutableListOf<RingtoneModel>()
            val selectedRingType = Prefs.with(context).getInt(Constants.KEY_RING_TYPE, 0)

            ringtones.add(RingtoneModel(RingtoneTypes.RING_TYPE_ONE, "Ringtone 1", selectedRingType == RingtoneTypes.RING_TYPE_ONE))
            ringtones.add(RingtoneModel(RingtoneTypes.RING_TYPE_TWO, "Ringtone 2", selectedRingType == RingtoneTypes.RING_TYPE_TWO))
            ringtones.add(RingtoneModel(RingtoneTypes.RING_TYPE_THREE, "Ringtone 3", selectedRingType == RingtoneTypes.RING_TYPE_THREE))
            ringtones.add(RingtoneModel(RingtoneTypes.RING_TYPE_FOUR, "Ringtone 4", selectedRingType == RingtoneTypes.RING_TYPE_FOUR))
            ringtones.add(RingtoneModel(RingtoneTypes.RING_TYPE_FIVE, "Ringtone 5", selectedRingType == RingtoneTypes.RING_TYPE_FIVE))
            ringtones.add(RingtoneModel(RingtoneTypes.RING_TYPE_SIX, "Ringtone 6", selectedRingType == RingtoneTypes.RING_TYPE_SIX))
            ringtones.add(RingtoneModel(RingtoneTypes.RING_TYPE_SEVEN, "Ringtone 7", selectedRingType == RingtoneTypes.RING_TYPE_SEVEN))


            val ringtoneSelectionAdapter = RingtoneSelectionAdapter(activity, rvRingtones, object:RingtoneSelectionAdapter.Callback{
                override fun onRingtoneClick(pos: Int, category: RingtoneModel) {
                    if(mediaPlayer != null){
                        mediaPlayer!!.stop()
                        mediaPlayer!!.reset()
                        mediaPlayer!!.release()
                    }
                    mediaPlayer = RingtoneTypes.getMediaPlayerFromRingtone(activity, category.ringType, false)
                    mediaPlayer!!.start()
                }

            })
            rvRingtones.adapter = ringtoneSelectionAdapter
            ringtoneSelectionAdapter.setList(ringtones)


            btnOk.setOnClickListener {

                for(i in ringtones){
                    if(i.isChecked) {
                        Prefs.with(activity).save(Constants.KEY_RING_TYPE, i.ringType)
                        break
                    }
                }

                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }

            setOnDismissListener{
                if(mediaPlayer != null){
                    mediaPlayer!!.stop()
                    mediaPlayer!!.reset()
                    mediaPlayer!!.release()
                    mediaPlayer = null
                }
            }

            show()
        }
    }

}