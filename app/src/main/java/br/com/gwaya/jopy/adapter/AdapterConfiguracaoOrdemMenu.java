package br.com.gwaya.jopy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makeramen.dragsortadapter.DragSortAdapter;

import java.util.List;

import br.com.gwaya.jopy.R;
import br.com.gwaya.jopy.adapter.viewholder.ViewHolderAdapterConfiguracaoOrdemMenu;


/**
 * Created by pedro.sousa on 11/06/15.
 */
public class AdapterConfiguracaoOrdemMenu extends DragSortAdapter<ViewHolderAdapterConfiguracaoOrdemMenu> {

    private List<Integer> data;

    public AdapterConfiguracaoOrdemMenu(RecyclerView recyclerView, List<Integer> data) {
        super(recyclerView);
        this.data = data;
    }

    @Override
    public ViewHolderAdapterConfiguracaoOrdemMenu onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.adapter_lista_carregamento, parent, false);
        ViewHolderAdapterConfiguracaoOrdemMenu holder = new ViewHolderAdapterConfiguracaoOrdemMenu(this, view);
        view.setOnLongClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderAdapterConfiguracaoOrdemMenu holder, final int position) {
        int itemId = data.get(position);
//        holder.text.setText(AbasPrincipais.getNome(itemId));
        holder.text.setGravity(Gravity.CENTER);
        holder.container.setVisibility(getDraggingId() == itemId ? View.INVISIBLE : View.VISIBLE);
        holder.container.postInvalidate();
    }

    public List<Integer> getData() {
        return data;
    }

    @Override
    public long getItemId(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getPositionForId(long id) {
        return data.indexOf((int) id);
    }

    @Override
    public boolean move(int fromPosition, int toPosition) {
        data.add(toPosition, data.remove(fromPosition));
        return true;
    }

}
