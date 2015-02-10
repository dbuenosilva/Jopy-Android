package br.com.gwaya.jopy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class PedidoCompraDataSource {
	  // Database fields
      private boolean endTransaction = true;
	  //private SQLiteDatabase database;
    private Context context;
	  private String[] allColumns = { 
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
	  private String[] allColumnsItems = {
			  MySQLiteHelper.COLUMN_ID,
		      MySQLiteHelper.ID_PAI,
		      MySQLiteHelper.PRODUTO,
		      MySQLiteHelper.QTDE,
              MySQLiteHelper.OBS,
		      MySQLiteHelper.VALOR,
		      MySQLiteHelper.TOTAL
	  		};
	
	  public PedidoCompraDataSource(Context context) {

	  }

	  public void open() throws SQLException {
          //database = DatabaseManager.getInstance().openDatabase();
	  }
	
	  public void close() {

          //DatabaseManager.getInstance().closeDatabase();
	  }
	  
	  public void beginTransaction() {
          open();
		  //database.beginTransaction();
		  endTransaction = false;
	  }
	  
	  public void commit() {
		  //database.setTransactionSuccessful();
		  //database.endTransaction();
		  endTransaction = true;
	  }

	  public PedidoCompra[] createUpdatePedidoCompra(PedidoCompra[] pedidos, boolean update) {
          final PedidoCompra[] _pedidos = pedidos;
          DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
              @Override
              public void run(SQLiteDatabase database) {
                    database.beginTransaction();
                    for (PedidoCompra pedidoCompra : _pedidos) {
                      createUpdatePedidoCompra(pedidoCompra, true);
                    }
	  		        database.setTransactionSuccessful();
                    database.endTransaction();
              }
          });
	  		return pedidos;
	  }

        public PedidoCompra updatePedidoCompra(final PedidoCompra pedidoCompra) {
            DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
                @Override
                public void run(SQLiteDatabase database) {
                    ContentValues values = new ContentValues();

                    values.put(MySQLiteHelper.ENVIADO, pedidoCompra.enviado);

                    database.update(MySQLiteHelper.TABLE_PEDIDO_COMPRA, values,
                            MySQLiteHelper.COLUMN_ID + " = '" + pedidoCompra._id + "'", null);
                }
            });

            return pedidoCompra;
        }

	  public PedidoCompra createUpdatePedidoCompra(final PedidoCompra _pedido, boolean update) {
          //final PedidoCompra _pedido = pedido;
          DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
              @Override
              public void run(SQLiteDatabase database) {
                  if (endTransaction) {
                      database.beginTransaction();
                  }

                  ContentValues values = new ContentValues();

                  values.put(MySQLiteHelper.COLUMN_ID, _pedido._id);
                  values.put(MySQLiteHelper.ID_SISTEMA, _pedido.idSistema);
                  values.put(MySQLiteHelper.APROVADORES, _pedido.aprovadores);
                  values.put(MySQLiteHelper.ENVIADO, _pedido.enviado);
                  values.put(MySQLiteHelper.STATUS_PEDIDO, _pedido.statusPedido);
                  values.put(MySQLiteHelper.NOME_FORN, _pedido.nomeForn);
                  values.put(MySQLiteHelper.CPF_CNPJ_FORN, _pedido.cpfCnpjForn);
                  values.put(MySQLiteHelper.COD_FORN, _pedido.codForn);
                  values.put(MySQLiteHelper.DT_EMI, _pedido.dtEmi);
                  values.put(MySQLiteHelper.DT_NECES, _pedido.dtNeces);
                  values.put(MySQLiteHelper.DT_REJ, _pedido.dtRej);
                  values.put(MySQLiteHelper.CENTRO_CUSTO, _pedido.centroCusto);
                  values.put(MySQLiteHelper.COND_PAGTO, _pedido.condPagto);
                  values.put(MySQLiteHelper.ID_SOLICITANTE, _pedido.idSolicitante);
                  values.put(MySQLiteHelper.SOLICITANTE, _pedido.solicitante);
                  values.put(MySQLiteHelper.MOTIVO, _pedido.motivo);
                  values.put(MySQLiteHelper.MOTIVO_REJEICAO, _pedido.motivoRejeicao);
                  values.put(MySQLiteHelper.TOTAL_PEDIDO, _pedido.totalPedido);
                  values.put(MySQLiteHelper.OBS, _pedido.obs);
                  values.put(MySQLiteHelper.DT_MOD, _pedido.dtMod);

                  database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA_ITEM, MySQLiteHelper.ID_PAI + " = '" + _pedido._id + "'", null);
                  database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA, MySQLiteHelper.COLUMN_ID + " = '" + _pedido._id + "'", null);

                  if (_pedido.itens != null) {

                      for (int i = 0; i < _pedido.itens.size(); i++) {
                          ContentValues itemValues = new ContentValues();

                          itemValues.put(MySQLiteHelper.ID_PAI, _pedido._id);
                          itemValues.put(MySQLiteHelper.COLUMN_ID, _pedido.itens.get(i)._id);
                          itemValues.put(MySQLiteHelper.PRODUTO, _pedido.itens.get(i).produto);
                          itemValues.put(MySQLiteHelper.QTDE, _pedido.itens.get(i).qtde);
                          itemValues.put(MySQLiteHelper.VALOR, _pedido.itens.get(i).valor);
                          itemValues.put(MySQLiteHelper.TOTAL, _pedido.itens.get(i).total);
                          itemValues.put(MySQLiteHelper.OBS, _pedido.itens.get(i).obs);

                          database.insert(MySQLiteHelper.TABLE_PEDIDO_COMPRA_ITEM, null,
                                  itemValues);
                      }
                  }

                  database.insert(MySQLiteHelper.TABLE_PEDIDO_COMPRA, null, values);

                  /*
                  database.update(MySQLiteHelper.TABLE_PEDIDO_COMPRA, values,
                          MySQLiteHelper.COLUMN_ID + " = '" + _pedido._id + "'", null);
                          */

                  if (endTransaction) {
                      database.setTransactionSuccessful();
                      database.endTransaction();
                  }
              }
          });
	    return _pedido;
	  }

      public String ultimoSync(){
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
	
	  public void deleteAll(){
          DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
              @Override
              public void run(SQLiteDatabase database) {
                  if (endTransaction) {
                      database.beginTransaction();
                  }
                  database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA_FILA, " 1 = 1 ", null);
                  database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA_ITEM, " 1 = 1 ", null);
                  database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA, " 1 = 1 ", null);
                  if (endTransaction) {
                      database.setTransactionSuccessful();
                      database.endTransaction();
                  }
              }
          });
	  }
	  
	  public void deletePedidoCompra(PedidoCompra pedido) {
        final String id = pedido._id;
          DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
              @Override
              public void run(SQLiteDatabase database) {
                  if (endTransaction) {
                      database.beginTransaction();
                  }
                  database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA_ITEM, MySQLiteHelper.ID_PAI
                          + " = '" + id + "'", null);
                  database.delete(MySQLiteHelper.TABLE_PEDIDO_COMPRA, MySQLiteHelper.COLUMN_ID
                          + " = '" + id + "'", null);
                  if (endTransaction) {
                      database.setTransactionSuccessful();
                      database.endTransaction();
                  }
              }
          });
	  }
	  
	  public List<PedidoCompra> getAllPedidoCompra(String strQuery, String limit) {
	    final List<PedidoCompra> pedidos = new ArrayList<PedidoCompra>();
        final String str = strQuery,
                strLimit = limit;
          DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
              @Override
              public void run(SQLiteDatabase database) {
                  String orderBy =  MySQLiteHelper.DT_NECES + " ASC";
                  Cursor cursor = database.query(MySQLiteHelper.TABLE_PEDIDO_COMPRA,
                          allColumns, str, null, null, null, orderBy, strLimit);

                  cursor.moveToFirst();
                  while (!cursor.isAfterLast()) {
                      PedidoCompra pedido = cursorToPedidoCompra(cursor);
                      pedido.itens = new ArrayList<PedidoCompraItem>();

                      Cursor cursorItem = database.query(MySQLiteHelper.TABLE_PEDIDO_COMPRA_ITEM,
                              allColumnsItems, MySQLiteHelper.ID_PAI + " = '" + pedido._id + "'", null, null, null, null);

                      cursorItem.moveToFirst();
                      while (!cursorItem.isAfterLast()) {
                          PedidoCompraItem item = cursosToPedidoCompraItem(cursorItem);
                          pedido.itens.add(item);
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
	  
	  private PedidoCompraItem cursosToPedidoCompraItem(Cursor cursor){
		  PedidoCompraItem item = new PedidoCompraItem();
		  item.produto = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PRODUTO));
		  item.qtde = cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.QTDE));
		  item.valor = cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.VALOR));
		  item.total = cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.TOTAL));
          item.obs = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OBS));
		  return item;
	  }
	  
	  private PedidoCompra cursorToPedidoCompra(Cursor cursor) {
		  PedidoCompra pedido = new PedidoCompra();

		  pedido._id = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID));
		  pedido.idSistema = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ID_SISTEMA));
		  pedido.aprovadores = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.APROVADORES));
          pedido.enviado = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.ENVIADO));
		  pedido.statusPedido = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.STATUS_PEDIDO));
		  pedido.nomeForn = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOME_FORN));
		  pedido.cpfCnpjForn = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CPF_CNPJ_FORN));
		  pedido.codForn = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COD_FORN));
		  pedido.idSolicitante = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ID_SOLICITANTE));
		  pedido.solicitante = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.SOLICITANTE));
		  pedido.totalPedido = cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.TOTAL_PEDIDO));
          pedido.condPagto = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COND_PAGTO));
          pedido.centroCusto = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CENTRO_CUSTO));
		  pedido.dtMod = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_MOD));
		  pedido.dtNeces = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_NECES));
		  pedido.dtEmi = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_EMI));
		  pedido.motivo = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOTIVO));
		  pedido.motivoRejeicao = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOTIVO_REJEICAO));
		  pedido.obs = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OBS));
		  
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
                        allColumnsItems, MySQLiteHelper.ID_PAI + " = '" + idApi + "'", null, null, null, null).getCount() > 0 ) {
                    isPedido[0] = true;
                }
            }
        });
        return(isPedido[0]);
    }
}
