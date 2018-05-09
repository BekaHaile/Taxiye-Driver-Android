package product.clicklabs.jugnoo.driver.support;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.ItemListener;
import product.clicklabs.jugnoo.driver.utils.Fonts;


/**
 * Created by Shankar on 7/17/15.
 */
public class SupportOptionsAdapter extends RecyclerView.Adapter<SupportOptionsAdapter.ViewHolder> implements ItemListener {

    private RecyclerView recyclerView;
    private ArrayList<SupportOption> supportOptions;
    private Callback callback;

    public SupportOptionsAdapter(RecyclerView recyclerView, ArrayList<SupportOption> supportOptions, Callback callback) {
        this.recyclerView = recyclerView;
        this.supportOptions = supportOptions;
        this.callback = callback;
    }

    public void setList(ArrayList<SupportOption> supportOptions){
        this.supportOptions = supportOptions;
        notifyDataSetChanged();
    }

    @Override
    public SupportOptionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_support_option, parent, false);
        return new ViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(SupportOptionsAdapter.ViewHolder holder, int position) {
        SupportOption supportOption = supportOptions.get(position);
        holder.tvName.setText(supportOption.getName());
	}

    @Override
    public int getItemCount() {
        return supportOptions == null ? 0 : supportOptions.size();
    }

    @Override
    public void onClickItem(View parentView, View childView) {
        int pos = recyclerView.getChildAdapterPosition(parentView);
        if(pos != RecyclerView.NO_POSITION){
            callback.onSupportClick(pos, supportOptions.get(pos));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public ViewHolder(final View itemView, final ItemListener itemListener) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvName.setTypeface(Fonts.mavenRegular(itemView.getContext()));
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(itemView, tvName);
                }
            });
        }
    }

    public interface Callback{
        void onSupportClick(int position, SupportOption supportOption);
    }
}
