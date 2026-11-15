package com.aditusstudios.bitacoralugares;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bitacora";
    public static final String TABLE_NAME = "paradas";
    public static final String TITULO = "titulo";
    public static final String CATEGORIA = "categoria";
    public static final String NOTA = "nota";
    public static final String FOTO = "foto";
    public static final String LATITUD = "latitud";
    public static final String LONGITUD = "longitud";
    public static final String FECHA = "fecha";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //"create table paradas (_id integer primary key autoincrement, titulo text not null,
        // categoria text not null, nota text, foto text, latitud real, longitud real, fecha integer);"
        db.execSQL("create table " + TABLE_NAME + " (_id integer primary key autoincrement, " +
                TITULO + " text not null, " + CATEGORIA + " text not null, " + NOTA + " text, " +
                FOTO + " text, " + LATITUD + " real, " + LONGITUD + " real, " + FECHA + " integer);");

        long ahora = System.currentTimeMillis();
        long unDia = 24 * 60 * 60 * 1000L;

        ContentValues cv = new ContentValues();
        cv.put(TITULO, "Atardecer en Cholula");
        cv.put(CATEGORIA, Parada.PAISAJE);
        cv.put(NOTA, "Subir a la pirámide como a las 6 pm. Con cielo despejado se ve el volcán.");
        cv.put(FOTO, "");
        cv.put(LATITUD, 19.05741);
        cv.put(LONGITUD, -98.30219);
        cv.put(FECHA, ahora - 12 * unDia);
        db.insert(TABLE_NAME, TITULO, cv);

        cv.put(TITULO, "El Patio y las Ranas");
        cv.put(CATEGORIA, Parada.COMIDA);
        cv.put(NOTA, "Taquería en la Av. 2 Poniente, en el centro. Más conocida como Las Ranas. Pedir los alambres.");
        cv.put(FOTO, "");
        cv.put(LATITUD, 19.04537);
        cv.put(LONGITUD, -98.19876);
        cv.put(FECHA, ahora - 5 * unDia);
        db.insert(TABLE_NAME, TITULO, cv);

        cv.put(TITULO, "Edificio de posgrados de la UPAEP");
        cv.put(CATEGORIA, Parada.ESCUELA);
        cv.put(NOTA, "Sobre la calle 17 Sur, cerca del campus central de la UPAEP.");
        cv.put(FOTO, "");
        cv.put(LATITUD, 19.04690);
        cv.put(LONGITUD, -98.21208);
        cv.put(FECHA, ahora - unDia);
        db.insert(TABLE_NAME, TITULO, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.w(TABLE_NAME, "-Actualizando la base de datos. Se destruirá la información anterior");
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public Parada obtenParada(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "_id = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Parada parada = null;
        if (cursor.moveToFirst()) {
            parada = new Parada(
                    cursor.getString(cursor.getColumnIndexOrThrow(TITULO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CATEGORIA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(NOTA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FOTO)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(LATITUD)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(LONGITUD)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(FECHA))
            );
            parada.setId(id);
        }
        cursor.close();
        return parada;
    }

    public int actualizaParada(Parada parada) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITULO, parada.getTitulo());
        cv.put(CATEGORIA, parada.getCategoria());
        cv.put(NOTA, parada.getNota());
        cv.put(FOTO, parada.getFoto());
        cv.put(LATITUD, parada.getLatitud());
        cv.put(LONGITUD, parada.getLongitud());
        cv.put(FECHA, parada.getFecha());
        return db.update(TABLE_NAME, cv, "_id = ?", new String[]{String.valueOf(parada.getId())});
    }

    public int borraParada(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, "_id = ?", new String[]{String.valueOf(id)});
    }
}
