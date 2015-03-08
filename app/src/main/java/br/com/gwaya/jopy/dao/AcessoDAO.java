package br.com.gwaya.jopy.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.interfaces.QueryExecutor;
import br.com.gwaya.jopy.model.Acesso;
import br.com.gwaya.jopy.model.RespostaLogin;

public class AcessoDAO {
    // Database fields
    //private SQLiteDatabase database;

    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.ACCESS_TOKEN,
            MySQLiteHelper.REFRESH_TOKEN,
            MySQLiteHelper.USUARIO,
            MySQLiteHelper.SENHA,
            MySQLiteHelper.TOKEN_TYPE
    };

    public AcessoDAO(Context context) {

    }

    public AcessoDAO() {

    }

    public void open() throws SQLException {

        //database = DatabaseManager.getInstance().openDatabase();
    }

    public void close() {

    }

    public Acesso createAcesso(RespostaLogin respLogin, String usuario, String senha) {
        final List<Acesso> newAcesso = new ArrayList<>();
        final RespostaLogin respostaLogin = respLogin;
        final String user = usuario;
        final String pass = senha;
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {

                ContentValues values = new ContentValues();

                values.put(MySQLiteHelper.ACCESS_TOKEN, respostaLogin.getAccess_token());
                values.put(MySQLiteHelper.REFRESH_TOKEN, respostaLogin.getRefresh_token());
                values.put(MySQLiteHelper.USUARIO, user);
                values.put(MySQLiteHelper.SENHA, pass);
                values.put(MySQLiteHelper.TOKEN_TYPE, respostaLogin.getToken_type());

                deleteAll();

                long insertId = database.insert(MySQLiteHelper.TABLE_ACESSO, null,
                        values);
                Cursor cursor = database.query(MySQLiteHelper.TABLE_ACESSO,
                        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                        null, null, null);
                cursor.moveToFirst();
                newAcesso.add(cursorToAcesso(cursor));
                cursor.close();

            }
        });
        return newAcesso.get(0);
    }

    private void deleteAll() {
        System.out.println("Comment deleted with id: ");


        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                database.delete(MySQLiteHelper.TABLE_ACESSO, null, null);
            }
        });
    }

    public void updateAcesso(Acesso acesso) {
        final long id = acesso.getId();
        final Acesso tmpAcesso = acesso;
        DatabaseManager.getInstance().executeQuery(
                new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        ContentValues values = new ContentValues();
                        values.put(MySQLiteHelper.DT_MOD, tmpAcesso.getDtMod());
                        database.update(MySQLiteHelper.TABLE_ACESSO, values, MySQLiteHelper.COLUMN_ID
                                + " = " + id, null);
                    }
                });
    }

    public void deleteAcesso(Acesso acesso) {
        final Acesso tmp = acesso;
        DatabaseManager.getInstance().executeQuery(
                new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        if (tmp == null) {
                            database.delete(MySQLiteHelper.TABLE_ACESSO, " 1 = 1 ", null);
                            return;
                        }
                        long id = tmp.getId();
                        System.out.println("Comment deleted with id: " + id);
                        database.delete(MySQLiteHelper.TABLE_ACESSO, MySQLiteHelper.COLUMN_ID
                                + " = " + id, null);
                    }
                });
    }

    public List<Acesso> getAllAcesso() {

        final List<Acesso> acessos = new ArrayList<Acesso>();
        DatabaseManager.getInstance().executeQuery(
                new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        Cursor cursor = database.query(MySQLiteHelper.TABLE_ACESSO,
                                allColumns, null, null, null, null, null);

                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            Acesso acesso = cursorToAcesso(cursor);
                            acessos.add(acesso);
                            cursor.moveToNext();
                        }
                        // make sure to close the cursor
                        cursor.close();
                    }
                });
        return acessos;
    }

    private Acesso cursorToAcesso(Cursor cursor) {
        Acesso acesso = new Acesso();
        acesso.setId(cursor.getLong(0));
        acesso.setAccess_Token(cursor.getString(1));
        acesso.setRefresh_Token(cursor.getString(2));
        acesso.setUsuario(cursor.getString(3));
        acesso.setSenha(cursor.getString(4));
        acesso.setToken_Type(cursor.getString(5));
        return acesso;
    }
}
