package br.com.gwaya.jopy.controller;

import android.database.Cursor;

import java.util.List;

import br.com.gwaya.jopy.dao.PermissaoDAO;
import br.com.gwaya.jopy.interfaces.Crudable;
import br.com.gwaya.jopy.model.Permissao;

/**
 * Created by pedro.sousa on 28/05/15.
 */
public class ControllerPermissao implements Crudable<Permissao> {

    private PermissaoDAO permissaoDAO = new PermissaoDAO();

    @Override
    public boolean create(Permissao... permissaos) {
        return permissaoDAO.create(permissaos);
    }

    @Override
    public List<Permissao> readAll() {
        return permissaoDAO.readAll();
    }

    @Override
    public boolean update(Permissao permissao) {
        return permissaoDAO.update(permissao);
    }

    @Override
    public boolean delete(Permissao permissao) {
        return permissaoDAO.delete(permissao);
    }

    @Override
    public Permissao read(int id) {
        return null;
    }

    @Override
    public boolean deleteAll() {
        return permissaoDAO.deleteAll();
    }

    @Override
    public Permissao convertCursorToObject(Cursor cursor) {
        return null;
    }
}
