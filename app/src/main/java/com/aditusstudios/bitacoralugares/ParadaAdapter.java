package com.aditusstudios.bitacoralugares;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ParadaAdapter extends ArrayAdapter<Parada> {

    public ParadaAdapter(Context context, ArrayList<Parada> paradas) {
        super(context, R.layout.row_parada, paradas);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_parada, parent, false);
        }

        Parada parada = getItem(position);

        ImageView imgMiniatura = convertView.findViewById(R.id.imgMiniatura);
        TextView txtTituloParada = convertView.findViewById(R.id.txtTituloParada);
        TextView txtFechaParada = convertView.findViewById(R.id.txtFechaParada);
        TextView txtCategoriaParada = convertView.findViewById(R.id.txtCategoriaParada);

        txtTituloParada.setText(parada.getTitulo());
        txtFechaParada.setText(parada.getFechaTexto());
        txtCategoriaParada.setText(parada.getCategoria());

        int colorFondo = ContextCompat.getColor(getContext(), parada.getColorFondo());
        int colorTexto = ContextCompat.getColor(getContext(), parada.getColorTexto());
        txtCategoriaParada.setBackgroundTintList(ColorStateList.valueOf(colorFondo));
        txtCategoriaParada.setTextColor(colorTexto);

        imgMiniatura.setClipToOutline(true);
        imgMiniatura.setBackgroundTintList(ColorStateList.valueOf(colorFondo));
        Bitmap miniatura = UtilFotos.cargaFoto(parada.getFoto(), 200);
        if (miniatura != null) {
            imgMiniatura.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgMiniatura.setImageTintList(null);
            imgMiniatura.setImageBitmap(miniatura);
        } else {
            imgMiniatura.setScaleType(ImageView.ScaleType.CENTER);
            imgMiniatura.setImageResource(R.drawable.ic_foto);
            imgMiniatura.setImageTintList(ColorStateList.valueOf(colorTexto));
        }

        return convertView;
    }
}