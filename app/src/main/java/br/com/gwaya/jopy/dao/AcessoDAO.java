package br.com.gwaya.jopy.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.interfaces.QueryExecutor;
import br.com.gwaya.jopy.model.Acesso;
import br.com.gwaya.jopy.model.RespostaLogin;

public class AcessoDAO {

    private final String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.ACCESS_TOKEN,
            MySQLiteHelper.REFRESH_TOKEN,
            MySQLiteHelper.USUARIO,
            MySQLiteHelper.SENHA,
            MySQLiteHelper.TOKEN_TYPE
    };

    public Acesso createAcesso(final RespostaLogin respostaLogin, final String usuario, final String senha) {
        final List<Acesso> newAcesso = new ArrayList<>();

        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {

                ContentValues values = new ContentValues();

                values.put(MySQLiteHelper.ACCESS_TOKEN, respostaLogin.getAccess_token());
                values.put(MySQLiteHelper.REFRESH_TOKEN, respostaLogin.getRefresh_token());
                values.put(MySQLiteHelper.USUARIO, usuario);
                values.put(MySQLiteHelper.SENHA, senha);
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
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                database.delete(MySQLiteHelper.TABLE_ACESSO, null, null);
            }
        });
    }

    public void updateAcesso(final Acesso acesso) {
        DatabaseManager.getInstance().executeQuery(
                new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        ContentValues values = new ContentValues();
                        values.put(MySQLiteHelper.DT_MOD, acesso.getDtMod());
                        database.update(MySQLiteHelper.TABLE_ACESSO, values, MySQLiteHelper.COLUMN_ID
                                + " = " + acesso.getId(), null);
                    }
                });
    }

    public void deleteAcesso(final Acesso acesso) {
        DatabaseManager.getInstance().executeQuery(
                new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        if (acesso == null) {
                            database.delete(MySQLiteHelper.TABLE_ACESSO, " 1 = 1 ", null);
                            return;
                        }

                        database.delete(MySQLiteHelper.TABLE_ACESSO, MySQLiteHelper.COLUMN_ID
                                + " = " + acesso.getId(), null);
                    }
                });
    }

    public List<Acesso> getAllAcesso() {

        final List<Acesso> acessos = new ArrayList<>();
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
