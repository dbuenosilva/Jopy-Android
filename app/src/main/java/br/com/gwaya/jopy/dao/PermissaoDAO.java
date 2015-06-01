package br.com.gwaya.jopy.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.gwaya.jopy.interfaces.Crudable;
import br.com.gwaya.jopy.interfaces.QueryExecutor;
import br.com.gwaya.jopy.model.Permissao;

/**
 * Created by pedro.sousa on 28/05/15.
 */
public class PermissaoDAO implements Crudable<Permissao> {

    private List<Boolean> listaResultado;
    private boolean retorno = false;

    @Override
    public boolean create(Permissao... permissaos) {
        if (permissaos == null) {
            return false;
        }
        listaResultado = new ArrayList<>();

        for (final Permissao permissao : permissaos) {
            DatabaseManager.getInstance().executeQuery(new QueryExecutor() {

                @Override
                public void run(SQLiteDatabase database) {
                    ContentValues values = new ContentValues();

                    values.put(Permissao.ACESSO, permissao.getAcesso());
                    values.put(Permissao.NIVEL, permissao.getNivel());

                    listaResultado.add(database.insert(Permissao.TABELA, null, values) != -1);
                }
            });
        }

        for (boolean salvo : listaResultado) {
            if (!salvo) {
                deleteAll();
                return false;
            }
        }

        return true;
    }

    @Override
    public Permissao read(final int id) {
        return null;
    }

    @Override
    public List<Permissao> readAll() {
        final List<Permissao> listaResultado = new ArrayList<>();
        DatabaseManager.getInstance().executeQuery(
                new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        Cursor cursor = database.query(Permissao.TABELA,
                                new String[]{Permissao.ID, Permissao.ACESSO, Permissao.NIVEL}, null, null, null, null, null);

                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            listaResultado.add(convertCursorToObject(cursor));
                            cursor.moveToNext();
                        }

                        cursor.close();
                    }
                });
        return listaResultado;
    }

    @Override
    public boolean update(final Permissao permissao) {
        if (permissao == null) {
            return false;
        }
        retorno = false;
        DatabaseManager.getInstance().executeQuery(
                new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        ContentValues values = new ContentValues();
                        values.put(Permissao.ACESSO, permissao.getAcesso());
                        values.put(Permissao.NIVEL, permissao.getNivel());
                        retorno = database.update(Permissao.TABELA, values, Permissao.ID
                                + " = " + permissao.getId(), null) > 0;
                    }
                });
        return retorno;
    }

    @Override
    public boolean delete(final Permissao permissao) {
        if (permissao == null) {
            return false;
        }
        retorno = false;
        DatabaseManager.getInstance().executeQuery(
                new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        retorno = database.delete(Permissao.TABELA, Permissao.ID + " = " + permissao.getId(), null) > 0;
                    }
                });
        return retorno;
    }

    @Override
    public boolean deleteAll() {
        retorno = false;
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                retorno = database.delete(Permissao.TABELA, null, null) > 0;
            }
        });
        return retorno;
    }

    @Override
    public Permissao convertCursorToObject(Cursor cursor) {
        Permissao permissao = new Permissao();
        permissao.setId(cursor.getInt(cursor.getColumnIndex(Permissao.ID)));
        permissao.setAcesso(cursor.getInt(cursor.getColumnIndex(Permissao.ACESSO)));
        permissao.setNivel(cursor.getInt(cursor.getColumnIndex(Permissao.NIVEL)));
        return permissao;
    }

}
