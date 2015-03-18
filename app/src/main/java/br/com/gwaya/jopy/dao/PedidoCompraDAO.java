package br.com.gwaya.jopy.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.StatusPedido;
import br.com.gwaya.jopy.interfaces.QueryExecutor;
import br.com.gwaya.jopy.model.PedidoCompra;
import br.com.gwaya.jopy.model.PedidoCompraItem;

public class PedidoCompraDAO {

    private final String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.ID_SISTEMA,
            MySQLiteHelper.APROVADORES,
            MySQLiteHelper.ENVIADO,
            MySQLiteHelper.STATUS_PEDIDO,
            MySQLiteHelper.NOME_FORN,
            MySQLiteHelper.CPF_CNPJ_FORN,
            MySQLiteHelper.COD_FORN,
            MySQLiteHelper.DT_EMI,
            MySQLiteHelper.DT_NECES,
            MySQLiteHelper.DT_REJ,
            MySQLiteHelper.CENTRO_CUSTO,
            MySQLiteHelper.ID_SOLICITANTE,
            MySQLiteHelper.SOLICITANTE,
            MySQLiteHelper.MOTIVO,
            MySQLiteHelper.MOTIVO_REJEICAO,
            MySQLiteHelper.TOTAL_PEDIDO,
            MySQLiteHelper.COND_PAGTO,
            MySQLiteHelper.OBS,
            MySQLiteHelper.DT_MOD
    };
    private final String[] allColumnsItems = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.ID_PAI,
            MySQLiteHelper.PRODUTO,
            MySQLiteHelper.QTDE,
            MySQLiteHelper.OBS,
            MySQLiteHelper.VALOR,
            MySQLiteHelper.TOTAL
    };

    public PedidoCompraDAO() {

    }

    public void createUpdatePedidoCompra(final PedidoCompra[] pedidos) {
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                database.beginTransaction();
                for (PedidoCompra pedidoCompra : pedidos) {
                    createUpdatePedidoCompra(pedidoCompra);
                }
                database.setTransactionSuccessful();
                database.endTransaction();
            }
        });
    }

    public void updatePedidoCompra(final PedidoCompra pedidoCompra) {
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                ContentValues values = new ContentValues();

                values.put(MySQLiteHelper.ENVIADO, pedidoCompra.getEnviado());

                database.update(MySQLiteHelper.TABLE_PEDIDO_COMPRA, values,
                        MySQLiteHelper.COLUMN_ID + " = '" + pedidoCompra.get_id() + "'", null);
            }
        });
    }

    public void createUpdatePedidoCompra(final PedidoCompra _pedido) {
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                database.beginTransaction();

                ContentValues values = new ContentValues();

                values.put(MySQLiteHelper.COLUMN_ID, _pedido.get_id());
                values.put(MySQLiteHelper.ID_SISTEMA, _pedido.getIdSistema());
                values.put(MySQLiteHelper.APROVADORES, _pedido.getAprovadores());
                values.put(MySQLiteHelper.ENVIADO, _pedido.getEnviado());
                values.put(MySQLiteHelper.STATUS_PEDIDO, _pedido.getStatusPedido());
                values.put(MySQLiteHelper.NOME_FORN, _pedido.getNomeForn());
                values.put(MySQLiteHelper.CPF_CNPJ_FORN, _pedido.getCpfCnpjForn());
                values.put(MySQLiteHelper.COD_FORN, _pedido.getCodForn());
                values.put(MySQLiteHelper.DT_EMI, _pedido.getDtEmi());
                values.put(MySQLiteHelper.DT_NECES, _pedido.getDtNeces());
                values.put(MySQLiteHelper.DT_REJ, _pedido.getDtRej());
                values.put(MySQLiteHelper.CENTRO_CUSTO, _pedido.getCentroCusto());
                values.put(MySQLiteHelper.COND_PAGTO, _pedido.getCondPagto());
                values.put(MySQLiteHelper.ID_SOLICITANTE, _pedido.getIdSolicitante());
                values.put(MySQLiteHelper.SOLICITANTE, _pedido.getSolicitante());
                values.put(MySQLiteHelper.MOTIVO, _pedido.getMotivo());
                values.put(MySQLiteHelper.MOTIVO_REJEICAO, _pedido.getMotivoRejeicao());
                values.put(MySQLiteHelper.TOTAL_PEDIDO, _pedido.getTotalPedido());
                values.put(MySQLiteHelper.OBS, _pedido.getObs());
                values.put(MySQLiteHelper.DT_MOD, _pedido.getDtMod());

                database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA_ITEM, MySQLiteHelper.ID_PAI + " = '" + _pedido.get_id() + "'", null);
                database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA, MySQLiteHelper.COLUMN_ID + " = '" + _pedido.get_id() + "'", null);

                if (_pedido.getItens() != null) {

                    for (int i = 0; i < _pedido.getItens().size(); i++) {
                        ContentValues itemValues = new ContentValues();

                        itemValues.put(MySQLiteHelper.ID_PAI, _pedido.get_id());
                        itemValues.put(MySQLiteHelper.COLUMN_ID, _pedido.getItens().get(i).get_id());
                        itemValues.put(MySQLiteHelper.PRODUTO, _pedido.getItens().get(i).getProduto());
                        itemValues.put(MySQLiteHelper.QTDE, _pedido.getItens().get(i).getQtde());
                        itemValues.put(MySQLiteHelper.VALOR, _pedido.getItens().get(i).getValor());
                        itemValues.put(MySQLiteHelper.TOTAL, _pedido.getItens().get(i).getTotal());
                        itemValues.put(MySQLiteHelper.OBS, _pedido.getItens().get(i).getObs());

                        database.insert(MySQLiteHelper.TABLE_PEDIDO_COMPRA_ITEM, null,
                                itemValues);
                    }
                }

                database.insert(MySQLiteHelper.TABLE_PEDIDO_COMPRA, null, values);

                database.setTransactionSuccessful();
                database.endTransaction();
            }
        });
    }

    public String ultimoSync() {
        final String[] dtMod = new String[1];
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                Cursor cursor = database.rawQuery("SELECT MAX(" + MySQLiteHelper.DT_MOD + ") FROM " + MySQLiteHelper.TABLE_PEDIDO_COMPRA + " limit 1",
                        null);
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    dtMod[0] = cursor.getString(0);
                }
                cursor.close();
            }
        });
        return dtMod[0];
    }

    public void deleteAll() {
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {

                database.beginTransaction();

                database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA_FILA, " 1 = 1 ", null);
                database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA_ITEM, " 1 = 1 ", null);
                database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA, " 1 = 1 ", null);

                database.setTransactionSuccessful();
                database.endTransaction();
            }
        });
    }

    public void deletePedidoCompra(PedidoCompra pedido) {
        final String id = pedido.get_id();
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                database.beginTransaction();

                database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA_ITEM, MySQLiteHelper.ID_PAI
                        + " = '" + id + "'", null);
                database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA, MySQLiteHelper.COLUMN_ID
                        + " = '" + id + "'", null);

                database.setTransactionSuccessful();
                database.endTransaction();
            }
        });
    }

    public List<PedidoCompra> getAllPedidoCompra(final StatusPedido statusPedido) {

        final List<PedidoCompra> pedidos = new ArrayList<>();

        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                Cursor cursor = null;

                if (StatusPedido.EMITIDO.getValor() == statusPedido.getValor()) {
                    cursor = database.query(MySQLiteHelper.TABLE_PEDIDO_COMPRA,
                            allColumns, MySQLiteHelper.STATUS_PEDIDO + " = '" + statusPedido.getTexto() + "'", null, null, null, MySQLiteHelper.DT_NECES+ " ASC", null);
                } else {
                    cursor = database.query(MySQLiteHelper.TABLE_PEDIDO_COMPRA,
                            allColumns, MySQLiteHelper.STATUS_PEDIDO + " = '" + statusPedido.getTexto() + "'", null, null, null, MySQLiteHelper.DT_MOD + " ASC", null);
                }

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    PedidoCompra pedido = cursorToPedidoCompra(cursor);
                    pedido.setItens(new ArrayList<PedidoCompraItem>());

                    Cursor cursorItem = database.query(MySQLiteHelper.TABLE_PEDIDO_COMPRA_ITEM,
                            allColumnsItems, MySQLiteHelper.ID_PAI + " = '" + pedido.get_id() + "'", null, null, null, null);

                    cursorItem.moveToFirst();
                    while (!cursorItem.isAfterLast()) {
                        PedidoCompraItem item = cursosToPedidoCompraItem(cursorItem);
                        pedido.getItens().add(item);
                        cursorItem.moveToNext();
                    }
                    cursorItem.close();

                    pedidos.add(pedido);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
            }
        });
        return pedidos;
    }

    public List<PedidoCompra> getAllPedidoCompra(final String strQuery, final String limit) {

        final List<PedidoCompra> pedidos = new ArrayList<>();

        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {

                String orderBy = MySQLiteHelper.DT_NECES + " ASC";

                Cursor cursor = database.query(MySQLiteHelper.TABLE_PEDIDO_COMPRA,
                        allColumns, strQuery, null, null, null, orderBy, limit);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    PedidoCompra pedido = cursorToPedidoCompra(cursor);
                    pedido.setItens(new ArrayList<PedidoCompraItem>());

                    Cursor cursorItem = database.query(MySQLiteHelper.TABLE_PEDIDO_COMPRA_ITEM,
                            allColumnsItems, MySQLiteHelper.ID_PAI + " = '" + pedido.get_id() + "'", null, null, null, null);

                    cursorItem.moveToFirst();
                    while (!cursorItem.isAfterLast()) {
                        PedidoCompraItem item = cursosToPedidoCompraItem(cursorItem);
                        pedido.getItens().add(item);
                        cursorItem.moveToNext();
                    }
                    cursorItem.close();

                    pedidos.add(pedido);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
            }
        });
        return pedidos;
    }

    private PedidoCompraItem cursosToPedidoCompraItem(Cursor cursor) {
        PedidoCompraItem item = new PedidoCompraItem();
        item.setProduto(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PRODUTO)));
        item.setQtde(cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.QTDE)));
        item.setValor(cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.VALOR)));
        item.setTotal(cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.TOTAL)));
        item.setObs(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OBS)));
        return item;
    }

    private PedidoCompra cursorToPedidoCompra(Cursor cursor) {
        PedidoCompra pedido = new PedidoCompra();

        pedido.set_id(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
        pedido.setIdSistema(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ID_SISTEMA)));
        pedido.setAprovadores(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.APROVADORES)));
        pedido.setEnviado(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.ENVIADO)));
        pedido.setStatusPedido(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.STATUS_PEDIDO)));
        pedido.setNomeForn(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOME_FORN)));
        pedido.setCpfCnpjForn(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CPF_CNPJ_FORN)));
        pedido.setCodForn(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COD_FORN)));
        pedido.setIdSolicitante(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ID_SOLICITANTE)));
        pedido.setSolicitante(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.SOLICITANTE)));
        pedido.setTotalPedido(cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.TOTAL_PEDIDO)));
        pedido.setCondPagto(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COND_PAGTO)));
        pedido.setCentroCusto(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CENTRO_CUSTO)));
        pedido.setDtMod(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_MOD)));
        pedido.setDtNeces(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_NECES)));
        pedido.setDtEmi(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_EMI)));
        pedido.setMotivo(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOTIVO)));
        pedido.setMotivoRejeicao(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOTIVO_REJEICAO)));
        pedido.setObs(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OBS)));

        return pedido;
    }


    public boolean ExistePedidoCompra(String _idApi) {
        final String idApi = _idApi;
        final boolean[] isPedido = new boolean[1];
        isPedido[0] = false;

        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {

                if (database.query(MySQLiteHelper.TABLE_PEDIDO_COMPRA_ITEM,
                        allColumnsItems, MySQLiteHelper.ID_PAI + " = '" + idApi + "'", null, null, null, null).getCount() > 0) {
                    isPedido[0] = true;
                }
            }
        });
        return (isPedido[0]);
    }
}
