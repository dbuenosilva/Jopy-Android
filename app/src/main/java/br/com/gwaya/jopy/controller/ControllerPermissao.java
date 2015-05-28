package br.com.gwaya.jopy.controller;

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
    public boolean create(Permissao... t) {
        return permissaoDAO.create(t);
    }

    @Override
    public Permissao read(int id) {
        return null;
    }

    @Override
    public List<Permissao> readAll() {
        return permissaoDAO.readAll();
    }

    @Override
    public void update(Permissao permissao) {
        permissaoDAO.update(permissao);
    }

    @Override
    public void delete(Permissao permissao) {
        permissaoDAO.delete(permissao);
    }

    @Override
    public void deleteAll() {
        permissaoDAO.deleteAll();
    }
}
