package br.com.gwaya.jopy.interfaces;

/**
 * Created by marcelorosa on 14/01/15.
 */

import android.database.sqlite.SQLiteDatabase;

public interface QueryExecutor {

    public void run(SQLiteDatabase database);
}