package br.com.gwaya.jopy.domain;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pedro.sousa on 09/06/15.
 */
public class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public final LayoutInflater layoutInflater;
    public Context context;
    public List<T> lista = new ArrayList<>();
    public OnItemClickListener onItemClickListener;
    private int lastPosition = -1;

    public BaseRecyclerAdapter(Context context, List<T> lista) {
        this.context = context;
        this.lista = lista;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
//        setAnimation(holder.itemView, position);
        if (onItemClickListener != null && holder.itemView != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
    }

//    private void setAnimation(View viewToAnimate, int position) {
//        // If the bound view wasn't previously displayed on screen, it's animated
//        if (position > lastPosition) {
//            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
//            viewToAnimate.startAnimation(animation);
//            lastPosition = position;
//        }
//    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
