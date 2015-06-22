package br.com.gwaya.jopy.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.controller.ControllerPermissao;
import br.com.gwaya.jopy.interfaces.QueryExecutor;
import br.com.gwaya.jopy.model.DadosAcesso;
import br.com.gwaya.jopy.model.RespostaLogin;

public class DadosAcessoDAO {

    private final String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.ACCESS_TOKEN,
            MySQLiteHelper.REFRESH_TOKEN,
            MySQLiteHelper.USUARIO,
            MySQLiteHelper.SENHA,
            MySQLiteHelper.TOKEN_TYPE
    };
    private ControllerPermissao controllerPermissao = new ControllerPermissao();

    public DadosAcesso createDadosAcesso(final RespostaLogin respostaLogin, final String usuario, final String senha) {
        final List<DadosAcesso> newDadosAcesso = new ArrayList<>();

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

//                controllerPermissao.create(respostaLogin.getPermissoes());

                long insertId = database.insert(MySQLiteHelper.TABLE_ACESSO, null,
                        values);
                Cursor cursor = database.query(MySQLiteHelper.TABLE_ACESSO,
                        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                        null, null, null);
                cursor.moveToFirst();
                newDadosAcesso.add(cursorToDadosAcesso(cursor));
                cursor.close();

            }
        });
        return newDadosAcesso.get(0);
    }

    private void deleteAll() {
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                database.delete(MySQLiteHelper.TABLE_ACESSO, null, null);
            }
        });
    }

    public void updateDadosAcesso(final DadosAcesso dadosAcesso) {
        DatabaseManager.getInstance().executeQuery(
                new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        ContentValues values = new ContentValues();
                        values.put(MySQLiteHelper.DT_MOD, dadosAcesso.getDtMod());
                        database.update(MySQLiteHelper.TABLE_ACESSO, values, MySQLiteHelper.COLUMN_ID
                                + " = " + dadosAcesso.getId(), null);
                    }
                });
    }

    public void deleteDadosAcesso() {
        DatabaseManager.getInstance().executeQuery(
                new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        database.delete(MySQLiteHelper.TABLE_ACESSO, " 1 = 1 ", null);
                        return;
/* CÓDIGO QUE NUNCA FOI UTILIZADO
                        database.delete(MySQLiteHelper.TABLE_ACESSO, MySQLiteHelper.COLUMN_ID
                                + " = " + acesso.getId(), null); */
                    }
                });
    }

    public List<DadosAcesso> getAllDadosAcesso() {

        final List<DadosAcesso> dadosAcessos = new ArrayList<>();
        DatabaseManager.getInstance().executeQuery(
                new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        Cursor cursor = database.query(MySQLiteHelper.TABLE_ACESSO,
                                allColumns, null, null, null, null, null);

                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            DadosAcesso dadosAcesso = cursorToDadosAcesso(cursor);
                            dadosAcessos.add(dadosAcesso);
                            cursor.moveToNext();
                        }

                        cursor.close();
                    }
                });
        return dadosAcessos;
    }

    private DadosAcesso cursorToDadosAcesso(Cursor cursor) {
        DadosAcesso dadosAcesso = new DadosAcesso();
        dadosAcesso.setId(cursor.getLong(0));
        dadosAcesso.setAccess_Token(cursor.getString(1));
        dadosAcesso.setRefresh_Token(cursor.getString(2));
        dadosAcesso.setUsuario(cursor.getString(3));
        dadosAcesso.setSenha(cursor.getString(4));
        dadosAcesso.setToken_Type(cursor.getString(5));
        return dadosAcesso;
    }
}
