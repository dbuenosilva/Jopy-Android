package br.com.gwaya.jopy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.model.PedidoCompra;
import br.com.gwaya.jopy.model.PedidoCompraItem;

public class FilaPedidoCompraDataSource {
    // Database fields
    private boolean endTransaction = true;
    //private SQLiteDatabase database;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID
    };
    private String[] allColumnsPedidoCompra = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.ID_SISTEMA,
            MySQLiteHelper.APROVADORES,
            MySQLiteHelper.STATUS_PEDIDO,
            MySQLiteHelper.NOME_FORN,
            MySQLiteHelper.CPF_CNPJ_FORN,
            MySQLiteHelper.COD_FORN,
            MySQLiteHelper.DT_EMI,
            MySQLiteHelper.DT_NECES,
            MySQLiteHelper.DT_REJ,
            MySQLiteHelper.CENTRO_CUSTO,
            MySQLiteHelper.COND_PAGTO,
            MySQLiteHelper.ID_SOLICITANTE,
            MySQLiteHelper.SOLICITANTE,
            MySQLiteHelper.MOTIVO,
            MySQLiteHelper.MOTIVO_REJEICAO,
            MySQLiteHelper.TOTAL_PEDIDO,
            MySQLiteHelper.OBS,
            MySQLiteHelper.DT_MOD
    };
    private String[] allColumnsItems = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.ID_PAI,
            MySQLiteHelper.PRODUTO,
            MySQLiteHelper.QTDE,
            MySQLiteHelper.VALOR,
            MySQLiteHelper.TOTAL
    };

    public FilaPedidoCompraDataSource(Context context) {

    }

    public void open() throws SQLException {
        try {
            //database = DatabaseManager.getInstance().openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {

        //DatabaseManager.getInstance().closeDatabase();
    }

    public void beginTransaction() {
        //database.beginTransaction();
        //endTransaction = false;
    }

    public void commit() {
        //database.setTransactionSuccessful();
        //database.endTransaction();
        //endTransaction = true;
    }

    public PedidoCompra createFilaPedidoCompra(PedidoCompra pedido) {
        final String id = pedido.get_id() == null ? "" : pedido.get_id();
        final PedidoCompra _pedido = pedido;
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {

                ContentValues values = new ContentValues();

                database.beginTransaction();

                values.put(MySQLiteHelper.STATUS_PEDIDO, _pedido.getStatusPedido());
                if (_pedido.getMotivoRejeicao() != null && _pedido.getMotivoRejeicao().trim() != "") {
                    values.put(MySQLiteHelper.MOTIVO_REJEICAO, _pedido.getMotivoRejeicao());
                    values.put(MySQLiteHelper.ENVIADO, 0);
                }

                database.update(MySQLiteHelper.TABLE_PEDIDO_COMPRA, values,
                        MySQLiteHelper.COLUMN_ID + " = '" + _pedido.get_id() + "'", null);

                Cursor cursor = database.query(MySQLiteHelper.TABLE_PEDIDO_COMPRA_FILA,
                        allColumns, MySQLiteHelper.COLUMN_ID + " = '" + id + "'",
                        null, null, null, null);

                if (cursor.getCount() <= 0) {

                    try {
                        values = new ContentValues();
                        values.put(MySQLiteHelper.COLUMN_ID, _pedido.get_id());
                        database.insert(MySQLiteHelper.TABLE_PEDIDO_COMPRA_FILA, null,
                                values);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                database.setTransactionSuccessful();
                database.endTransaction();
            }
        });
        return pedido;
    }

    public void deleteAll() {
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA_FILA, null, null);
            }
        });
    }

    public void deleteFilaPedidoCompra(PedidoCompra pedido) {
        final String id = pedido.get_id();
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA_FILA, MySQLiteHelper.COLUMN_ID
                        + " = '" + id + "'", null);

                ContentValues values = new ContentValues();
                values.put(MySQLiteHelper.ENVIADO, 1);
                database.update(MySQLiteHelper.TABLE_PEDIDO_COMPRA, values,
                        MySQLiteHelper.COLUMN_ID + " = '" + id + "'", null);
            }
        });
    }

    public List<PedidoCompra> getAllPedidoCompra() {
        final List<PedidoCompra> pedidos = new ArrayList<PedidoCompra>();

        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                String filtro = "";
                Cursor cursorFila = database.query(MySQLiteHelper.TABLE_PEDIDO_COMPRA_FILA,
                        allColumns, null, null, null, null, null);

                cursorFila.moveToFirst();
                while (!cursorFila.isAfterLast()) {
                    filtro += "'" + cursorFila.getString(cursorFila.getColumnIndex(MySQLiteHelper.COLUMN_ID)) + "',";
                    cursorFila.moveToNext();
                }

                if (filtro.length() > 0) {
                    filtro = MySQLiteHelper.COLUMN_ID + " IN (" + filtro.substring(0, filtro.length() - 1) + ")";

                    Cursor cursor = database.query(MySQLiteHelper.TABLE_PEDIDO_COMPRA,
                            allColumnsPedidoCompra, filtro, null, null, null, null);

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
        return item;
    }

    private PedidoCompra cursorToPedidoCompra(Cursor cursor) {
        PedidoCompra pedido = new PedidoCompra();

        pedido.set_id(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
        pedido.setIdSistema(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ID_SISTEMA)));
        pedido.setAprovadores(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.APROVADORES)));
        // pedido.enviado = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.ENVIADO));
        pedido.setStatusPedido(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.STATUS_PEDIDO)));
        pedido.setNomeForn(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOME_FORN)));
        pedido.setCpfCnpjForn(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CPF_CNPJ_FORN)));
        pedido.setCondPagto(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COND_PAGTO)));
        pedido.setCodForn(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COD_FORN)));
        pedido.setCentroCusto(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CENTRO_CUSTO)));
        pedido.setIdSolicitante(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ID_SOLICITANTE)));
        pedido.setSolicitante(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.SOLICITANTE)));
        pedido.setTotalPedido(cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.TOTAL_PEDIDO)));
        pedido.setDtMod(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_MOD)));
        pedido.setDtNeces(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_NECES)));
        pedido.setDtEmi(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_EMI)));
        pedido.setDtRej(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_REJ)));
        pedido.setMotivo(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOTIVO)));
        pedido.setMotivoRejeicao(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOTIVO_REJEICAO)));
        pedido.setObs(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OBS)));

        return pedido;
    }
}
