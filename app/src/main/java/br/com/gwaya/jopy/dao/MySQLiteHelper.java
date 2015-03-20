package br.com.gwaya.jopy.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_ACESSO = "ACESSO";
    public static final String TABLE_PEDIDO_COMPRA = "PEDIDO_COMPRA";
    public static final String TABLE_PEDIDO_COMPRA_ITEM = "PEDIDO_COMPRA_ITEM";
    public static final String TABLE_PEDIDO_COMPRA_FILA = "PEDIDO_COMPRA_FILA";

    public static final String COLUMN_ID = "_id";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String USUARIO = "USUARIO";
    public static final String SENHA = "SENHA";
    public static final String TOKEN_TYPE = "TOKEN_TYPE";
    public static final String ID_SISTEMA = "ID_SISTEMA";
    public static final String APROVADORES = "APROVADORES";
    public static final String ENVIADO = "ENVIADO";
    public static final String STATUS_PEDIDO = "STATUS_PEDIDO";
    public static final String NOME_FORN = "NOME_FORN";
    public static final String CPF_CNPJ_FORN = "CPF_CNPJ_FORN";
    public static final String COD_FORN = "COD_FORN";
    public static final String DT_EMI = "DT_EMI";
    public static final String DT_NECES = "DT_NECES";
    public static final String DT_REJ = "DT_REJ";
    public static final String CENTRO_CUSTO = "CENTRO_CUSTO";
    public static final String COND_PAGTO = "COND_PAGTO";
    public static final String ID_SOLICITANTE = "ID_SOLICITANTE";
    public static final String SOLICITANTE = "SOLICITANTE";
    public static final String MOTIVO = "MOTIVO";
    public static final String MOTIVO_REJEICAO = "MOTIVO_REJEICAO";
    public static final String TOTAL_PEDIDO = "TOTAL_PEDIDO";
    public static final String OBS = "OBS";
    public static final String DT_MOD = "DT_MOD";
    public static final String ID_PAI = "id_pai";
    public static final String PRODUTO = "produto";
    public static final String QTDE = "qtde";
    public static final String VALOR = "valor";
    public static final String TOTAL = "total";
    private static final String DATABASE_CREATE4 =
            "create table "
                    + TABLE_PEDIDO_COMPRA_FILA + "(" + COLUMN_ID + " text primary key);";
    // Database creation sql statement
    private static final String DATABASE_CREATE1 =
            "create table "
                    + TABLE_ACESSO + "(" + COLUMN_ID + " integer primary key autoincrement, "
                    + ACCESS_TOKEN + " text not null, "
                    + REFRESH_TOKEN + " text not null, "
                    + USUARIO + " text not null, "
                    + SENHA + " text not null, "
                    + TOKEN_TYPE + " text not null "
                    + ");";
    private static final String DATABASE_CREATE2 =
            "create table "
                    + TABLE_PEDIDO_COMPRA + "(" + COLUMN_ID + " text primary key, "
                    + ID_SISTEMA + " text null, "
                    + APROVADORES + " text null, "
                    + ENVIADO + " integer null, "
                    + STATUS_PEDIDO + " text null, "
                    + NOME_FORN + " text null, "
                    + CPF_CNPJ_FORN + " text null, "
                    + COD_FORN + " text null, "
                    + DT_EMI + " text null, "
                    + DT_NECES + " text null, "
                    + DT_REJ + " text null, "
                    + CENTRO_CUSTO + " text null, "
                    + COND_PAGTO + " text null, "
                    + ID_SOLICITANTE + " text null, "
                    + SOLICITANTE + " text null, "
                    + MOTIVO + " text null, "
                    + MOTIVO_REJEICAO + " text null, "
                    + TOTAL_PEDIDO + " real null, "
                    + OBS + " text null, "
                    + DT_MOD + " text null "
                    + ");";
    private static final String DATABASE_CREATE3 =
            "create table "
                    + TABLE_PEDIDO_COMPRA_ITEM + "(" + COLUMN_ID + " text primary key, "
                    + ID_PAI + " text not null, "
                    + PRODUTO + " text null, "
                    + QTDE + " real null, "
                    + VALOR + " real null, "
                    + OBS + " text null, "
                    + TOTAL + " real null "
                    + ");";
    private static final String DATABASE_NAME = "july.db";
    private static final int DATABASE_VERSION = 27;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE1);
        database.execSQL(DATABASE_CREATE2);
        database.execSQL(DATABASE_CREATE3);
        database.execSQL(DATABASE_CREATE4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACESSO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEDIDO_COMPRA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEDIDO_COMPRA_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEDIDO_COMPRA_FILA);
        onCreate(db);
    }

}
