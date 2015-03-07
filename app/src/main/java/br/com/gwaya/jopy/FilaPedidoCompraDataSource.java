package br.com.gwaya.jopy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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
          }
          catch (Exception e) {
              e.printStackTrace();
          }
	  }
	
	  public void close() {

          //DatabaseManager.getInstance().closeDatabase();
	  }
	
	  public void beginTransaction(){
		  //database.beginTransaction();
		  //endTransaction = false;
	  }
	  
	  public void commit() {
		  //database.setTransactionSuccessful();
		  //database.endTransaction();
		  //endTransaction = true;
	  }
	  
	  public PedidoCompra createFilaPedidoCompra(PedidoCompra pedido) {
		final String id = pedido._id == null ? "" : pedido._id;
          final PedidoCompra _pedido = pedido;
          DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
              @Override
              public void run(SQLiteDatabase database) {

                  ContentValues values = new ContentValues();

                  database.beginTransaction();

                  values.put(MySQLiteHelper.STATUS_PEDIDO, _pedido.statusPedido);
                  if (_pedido.motivoRejeicao != null && _pedido.motivoRejeicao.trim() != "") {
                      values.put(MySQLiteHelper.MOTIVO_REJEICAO, _pedido.motivoRejeicao);
                      values.put(MySQLiteHelper.ENVIADO, 0);
                  }

                  database.update(MySQLiteHelper.TABLE_PEDIDO_COMPRA, values,
                          MySQLiteHelper.COLUMN_ID + " = '" + _pedido._id + "'", null);

                  Cursor cursor = database.query(MySQLiteHelper.TABLE_PEDIDO_COMPRA_FILA,
                          allColumns, MySQLiteHelper.COLUMN_ID + " = '" + id + "'",
                          null, null, null, null);

                  if (cursor.getCount() <= 0) {

                      try {
                          values = new ContentValues();
                          values.put(MySQLiteHelper.COLUMN_ID, _pedido._id);
                          database.insert(MySQLiteHelper.TABLE_PEDIDO_COMPRA_FILA, null,
                                  values);
                      } catch (Exception e) {
                          // TODO: handle exception

                      } finally {

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
	    final String id = pedido._id;
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
		  return item;
	  }
	  
	  private PedidoCompra cursorToPedidoCompra(Cursor cursor) {
		  PedidoCompra pedido = new PedidoCompra();

		  pedido._id = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID));
		  pedido.idSistema = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ID_SISTEMA));
		  pedido.aprovadores = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.APROVADORES));
         // pedido.enviado = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.ENVIADO));
		  pedido.statusPedido = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.STATUS_PEDIDO));
		  pedido.nomeForn = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOME_FORN));
		  pedido.cpfCnpjForn = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CPF_CNPJ_FORN));
          pedido.condPagto = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COND_PAGTO));
		  pedido.codForn = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COD_FORN));
          pedido.centroCusto = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CENTRO_CUSTO));
		  pedido.idSolicitante = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ID_SOLICITANTE));
		  pedido.solicitante = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.SOLICITANTE));
		  pedido.totalPedido = cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.TOTAL_PEDIDO));
		  pedido.dtMod = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_MOD));
		  pedido.dtNeces = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_NECES));
		  pedido.dtEmi = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_EMI));
          pedido.dtRej = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DT_REJ));
          pedido.motivo = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOTIVO));
          pedido.motivoRejeicao = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOTIVO_REJEICAO));
          pedido.obs = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.OBS));

		  return pedido;
	  }
}
