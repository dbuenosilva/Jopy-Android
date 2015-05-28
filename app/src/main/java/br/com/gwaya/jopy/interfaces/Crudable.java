package br.com.gwaya.jopy.interfaces;

import java.util.List;

/**
 * Created by pedro.sousa on 28/05/15.
 */
public interface Crudable<T> {

    public boolean create(T... t);

    public T read(int id);

    public List<T> readAll();

    public void update(T t);

    public void delete(T t);

    public void deleteAll();
}
