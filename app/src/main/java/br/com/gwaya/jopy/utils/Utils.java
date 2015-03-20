package br.com.gwaya.jopy.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.util.List;

import br.com.gwaya.jopy.model.PedidoCompra;

/**
 * Created by pedro on 13/03/15.
 */
public class Utils {

    public static boolean isSameElements(List<PedidoCompra> lista1, List<PedidoCompra> lista2) {
        if (lista1 != null && lista2 != null) {
            if (lista1.size() == lista2.size()) {

                int countIdsIdenticos = 0;

                for (int i = 0; i < lista1.size(); i++) {
                    for (int j = 0; j < lista2.size(); j++) {

                        PedidoCompra a = lista1.get(i);
                        PedidoCompra b = lista2.get(j);

                        if (a.get_id().equals(b.get_id())) {
                            ++countIdsIdenticos;
                        }
                    }
                }

                return countIdsIdenticos/2 == lista1.size();
            }
        }
        return false;
    }

    public boolean isDebuggable(Context context) {
        return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

}
