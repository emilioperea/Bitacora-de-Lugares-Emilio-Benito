package com.aditusstudios.bitacoralugares;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UtilFotos {

    public static Bitmap cargaFoto(String ruta, int ladoMaximo) {
        if (ruta == null || ruta.isEmpty())
            return null;

        BitmapFactory.Options opciones = new BitmapFactory.Options();
        opciones.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(ruta, opciones);
        if (opciones.outWidth <= 0)
            return null;

        int escala = 1;
        int ladoMayor = Math.max(opciones.outWidth, opciones.outHeight);
        while (ladoMayor / (escala * 2) >= ladoMaximo) {
            escala = escala * 2;
        }

        opciones.inJustDecodeBounds = false;
        opciones.inSampleSize = escala;
        Bitmap bitmap = BitmapFactory.decodeFile(ruta, opciones);
        if (bitmap == null)
            return null;

        int grados = leeRotacion(ruta);
        if (grados != 0) {
            Matrix matriz = new Matrix();
            matriz.postRotate(grados);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matriz, true);
        }
        return bitmap;
    }

    public static boolean reduceFoto(String ruta, int ladoMaximo) {
        Bitmap bitmap = cargaFoto(ruta, ladoMaximo);
        if (bitmap == null)
            return false;

        try {
            FileOutputStream salida = new FileOutputStream(ruta);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, salida);
            salida.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void borraFoto(String ruta) {
        if (ruta != null && !ruta.isEmpty()) {
            File archivo = new File(ruta);
            if (archivo.exists())
                archivo.delete();
        }
    }

    private static int leeRotacion(String ruta) {
        try {
            ExifInterface exif = new ExifInterface(ruta);
            int orientacion = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            if (orientacion == ExifInterface.ORIENTATION_ROTATE_90)
                return 90;
            else if (orientacion == ExifInterface.ORIENTATION_ROTATE_180)
                return 180;
            else if (orientacion == ExifInterface.ORIENTATION_ROTATE_270)
                return 270;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}