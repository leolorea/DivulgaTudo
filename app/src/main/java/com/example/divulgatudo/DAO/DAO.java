package com.example.divulgatudo.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.divulgatudo.Objeto.Anuncios;

import java.util.ArrayList;
import java.util.List;

public class DAO extends SQLiteOpenHelper {

    private String filtro;
    private ContentValues dados;
    private SQLiteDatabase db;

    public DAO(Context context) {
        super(context,"banco", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE anuncios(nome TEXT, cliente TEXT, datainicial Integer, datatermino Integer, investimento Integer)";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql  = "DROP TABLE IF EXISTS anuncios";
        db.execSQL(sql);
        onCreate(db);
    }

    public void cadastroAnuncio(Anuncios anuncios){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues dados = new ContentValues();

        dados.put("nome", anuncios.getNome());
        dados.put("cliente", anuncios.getCliente());
        dados.put("datainicial", anuncios.getDatainicio());
        dados.put("datatermino", anuncios.getDatatermino());
        dados.put("investimento", anuncios.getInvestimento());
        db.insert("anuncios", null, dados);
    }
    public void Pesquisa(){

        String pesquisa = "SELECT nome FROM anuncios WHERE nome LIKE '"+ filtro +" %'";

    }

    public List<Anuncios> buscaAnuncios(){

     SQLiteDatabase db = getReadableDatabase();
     String sql = "SELECT * FROM anuncios";

        Cursor c = db.rawQuery(sql,null);

        List<Anuncios> anuncio = new ArrayList<Anuncios>();

        while (c.moveToNext()){
            Anuncios anuncios = new Anuncios();
            anuncios.setNome(c.getString(c.getColumnIndex("nome")));
            anuncios.setCliente(c.getString(c.getColumnIndex("cliente")));
            anuncios.setDatainicio(c.getString(c.getColumnIndex("datainicial")));
            anuncios.setDatatermino(c.getString(c.getColumnIndex("datatermino")));
            anuncios.setInvestimento(Integer.valueOf(c.getString(c.getColumnIndex("investimento"))));
            anuncio.add(anuncios);
        }
        return anuncio;

    }
}
