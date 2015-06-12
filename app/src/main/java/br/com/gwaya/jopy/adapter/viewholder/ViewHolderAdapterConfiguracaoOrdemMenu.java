package br.com.gwaya.jopy.adapter.viewholder;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.dragsortadapter.DragSortAdapter;
import com.makeramen.dragsortadapter.NoForegroundShadowBuilder;

import br.com.gwaya.jopy.R;


/**
 * Created by pedro.sousa on 12/06/15.
 */
public class ViewHolderAdapterConfiguracaoOrdemMenu extends DragSortAdapter.ViewHolder implements
        View.OnLongClickListener {

    public ViewGroup container;
    public TextView text;

    public ViewHolderAdapterConfiguracaoOrdemMenu(DragSortAdapter adapter, View itemView) {
        super(adapter, itemView);
        container = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        text = (TextView) itemView.findViewById(R.id.maxTextViewNumcarreg);
    }

    @Override
    public boolean onLongClick(@NonNull View v) {
        startDrag();
        return true;
    }

    @Override
    public View.DragShadowBuilder getShadowBuilder(View itemView, Point touchPoint) {
        return new NoForegroundShadowBuilder(itemView, touchPoint);
    }
}