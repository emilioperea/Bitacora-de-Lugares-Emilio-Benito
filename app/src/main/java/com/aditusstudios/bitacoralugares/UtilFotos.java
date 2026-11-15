package com.aditusstudios.bitacoralugares;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UtilFotos {

    public static String guardaFoto(Context context, Bitmap foto) {
        File archivo = new File(context.getFilesDir(), "parada_" + System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream salida = new FileOutputStream(archivo);
            foto.compress(Bitmap.CompressFormat.JPEG, 100, salida);
            salida.close();
            return archivo.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap cargaFoto(String ruta) {
        if (ruta == null || ruta.isEmpty())
            return null;
        return BitmapFactory.decodeFile(ruta);
    }

    public static void borraFoto(String ruta) {
        if (ruta != null && !ruta.isEmpty()) {
            File archivo = new File(ruta);
            if (archivo.exists())
                archivo.delete();
        }
    }
}
