package product.clicklabs.jugnoo.driver.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.RoundBorderTransform
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import java.io.File

class DocImagesAdapter(val activity:Activity, val rv:RecyclerView, val callback:Callback) : RecyclerView.Adapter<DocImagesAdapter.DocImageViewHolder>(),ItemListener {

    private var docImages:MutableList<DocImage>? = mutableListOf()

    fun setList(docImages: MutableList<DocImage>?){
        this.docImages = docImages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocImagesAdapter.DocImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_document_image, parent, false)
        return DocImageViewHolder(itemView, this)
    }

    override fun getItemCount(): Int {
        return if(docImages == null) 0 else docImages!!.size;
    }

    override fun onBindViewHolder(holder: DocImagesAdapter.DocImageViewHolder, position: Int) {
        if(!TextUtils.isEmpty(docImages!![position].imageUrl)){
            holder.deleteImage1.visibility = View.VISIBLE
            holder.ivDocImage1.isEnabled = false

            Picasso.with(activity).load(docImages!![position].imageUrl)
                    .transform(RoundBorderTransform()).resize(200, 200).centerCrop()
                    .into(holder.ivDocImage1)

        }
        else if(docImages!![position].file != null){
            holder.deleteImage1.visibility = View.VISIBLE
            holder.ivDocImage1.isEnabled = false

            Picasso.with(activity).load(docImages!![position].file)
                    .transform(RoundBorderTransform()).resize(200, 200).centerCrop()
                    .into(holder.ivDocImage1)
        }
        else {
            holder.deleteImage1.visibility = View.GONE
            holder.ivDocImage1.isEnabled = true
            holder.ivDocImage1.setImageDrawable(null)
        }
    }


    inner class DocImageViewHolder(view: View, listener:ItemListener): RecyclerView.ViewHolder(view) {
        val ivDocImage1: ImageView = view.findViewById(R.id.ivDocImage1) as ImageView
        val deleteImage1: ImageView = view.findViewById(R.id.deleteImage1) as ImageView
        init{
            ivDocImage1.setOnClickListener{v->
                listener.onClickItem(view, v)
            }
            deleteImage1.setOnClickListener{v->
                listener.onClickItem(view, v)
            }
        }
    }

    override fun onClickItem(parentView: View?, childView: View?) {
        val pos:Int = rv.getChildLayoutPosition(parentView)
        if(pos != RecyclerView.NO_POSITION){
            when(childView!!.id){
                R.id.ivDocImage1->{
                    callback.onClick(pos, docImages!![pos])
                }
                R.id.deleteImage1->{
                    DialogPopup.alertPopupTwoButtonsWithListeners(activity, activity.getString(R.string.are_you_sure_you_want_to_delete_this_image)
                    ) { callback.onDeleteClick(pos, docImages!![pos]) }
                }
            }
        }
    }

    interface Callback {
        fun onDeleteClick(pos:Int, docImage:DocImage)
        fun onClick(pos:Int, docImage:DocImage)
    }
}

class DocImage(var imageUrl:String? = null, var file: File? = null)