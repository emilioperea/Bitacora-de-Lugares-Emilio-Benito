package com.aditusstudios.bitacoralugares;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bitacora";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "paradas";
    public static final String ID = "_id";
    public static final String TITULO = "titulo";
    public static final String CATEGORIA = "categoria";
    public static final String NOTA = "nota";
    public static final String FOTO = "foto";
    public static final String LATITUD = "latitud";
    public static final String LONGITUD = "longitud";
    public static final String FECHA = "fecha";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //"create table paradas (_id integer primary key autoincrement, titulo text not null, categoria text not null,
        // nota text, foto text, latitud real, longitud real, fecha integer);"
        db.execSQL("create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, " +
                TITULO + " text not null, " + CATEGORIA + " text not null, " + NOTA + " text, " +
                FOTO + " text, " + LATITUD + " real, " + LONGITUD + " real, " + FECHA + " integer);");

        long ahora = System.currentTimeMillis();
        long unDia = 24 * 60 * 60 * 1000L;
        ContentValues cv = new ContentValues();

        cv.put(TITULO, "Atardecer en Cholula");
        cv.put(CATEGORIA, "Paisaje");
        cv.put(NOTA, "Subir a la pirámide como a las 6 pm. Con cielo despejado se ve el volcán.");
        cv.put(FOTO, "");
        cv.put(LATITUD, 19.05741);
        cv.put(LONGITUD, -98.30219);
        cv.put(FECHA, ahora - 12 * unDia);
        db.insert(TABLE_NAME, TITULO, cv);

        cv.put(TITULO, "Tacos árabes en el centro");
        cv.put(CATEGORIA, "Comida");
        cv.put(NOTA, "El puesto de la esquina. Pedir con cebolla asada y salsa de chipotle.");
        cv.put(FOTO, "");
        cv.put(LATITUD, 19.04331);
        cv.put(LONGITUD, -98.19812);
        cv.put(FECHA, ahora - 5 * unDia);
        db.insert(TABLE_NAME, TITULO, cv);

        cv.put(TITULO, "Biblioteca de la universidad");
        cv.put(CATEGORIA, "Escuela");
        cv.put(NOTA, "Segundo piso, mesas junto a la ventana. Hay contactos para la laptop.");
        cv.put(FOTO, "");
        cv.put(LATITUD, 19.04690);
        cv.put(LONGITUD, -98.21208);
        cv.put(FECHA, ahora - unDia);
        db.insert(TABLE_NAME, TITULO, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public long insertaParada(Parada parada) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = creaValores(parada);
        return db.insert(TABLE_NAME, TITULO, cv);
    }

    public int actualizaParada(Parada parada) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = creaValores(parada);
        return db.update(TABLE_NAME, cv, ID + " = ?", new String[]{String.valueOf(parada.getId())});
    }

    public int borraParada(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Parada obtenParada(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Parada parada = null;
        if (cursor.moveToFirst()) {
            parada = leeParada(cursor);
        }
        cursor.close();
        return parada;
    }

    public ArrayList<Parada> obtenParadas(String busqueda, String categoria, boolean porNombre) {
        ArrayList<Parada> paradas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String patron = "%" + busqueda + "%";
        String seleccion = "(" + TITULO + " like ? or " + NOTA + " like ?)";
        String[] argumentos;
        if (categoria == null) {
            argumentos = new String[]{patron, patron};
        } else {
            seleccion = seleccion + " and " + CATEGORIA + " = ?";
            argumentos = new String[]{patron, patron, categoria};
        }

        String orden;
        if (porNombre)
            orden = TITULO + " collate nocase asc";
        else
            orden = FECHA + " desc";

        Cursor cursor = db.query(TABLE_NAME, null, seleccion, argumentos, null, null, orden);
        if (cursor.moveToFirst()) {
            do {
                paradas.add(leeParada(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return paradas;
    }

    private ContentValues creaValores(Parada parada) {
        ContentValues cv = new ContentValues();
        cv.put(TITULO, parada.getTitulo());
        cv.put(CATEGORIA, parada.getCategoria());
        cv.put(NOTA, parada.getNota());
        cv.put(FOTO, parada.getFoto());
        cv.put(LATITUD, parada.getLatitud());
        cv.put(LONGITUD, parada.getLongitud());
        cv.put(FECHA, parada.getFecha());
        return cv;
    }

    private Parada leeParada(Cursor cursor) {
        Parada parada = new Parada(
                cursor.getString(cursor.getColumnIndexOrThrow(TITULO)),
                cursor.getString(cursor.getColumnIndexOrThrow(CATEGORIA)),
                cursor.getString(cursor.getColumnIndexOrThrow(NOTA)),
                cursor.getString(cursor.getColumnIndexOrThrow(FOTO)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(LATITUD)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(LONGITUD)),
                cursor.getLong(cursor.getColumnIndexOrThrow(FECHA))
        );
        parada.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ID)));
        return parada;
    }
}