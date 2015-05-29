package br.com.gwaya.jopy.interfaces;

import android.database.Cursor;

import java.util.List;

/**
 * Created by pedro.sousa on 28/05/15.
 */
public interface Crudable<T> {

    public boolean create(T... t);

    public T read(int id);

    public List<T> readAll();

    public boolean update(T t);

    public boolean delete(T t);

    public boolean deleteAll();

    public T convertCursorToObject(Cursor cursor);
}
