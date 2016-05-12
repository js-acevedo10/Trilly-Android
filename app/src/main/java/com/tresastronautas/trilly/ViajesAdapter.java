package com.tresastronautas.trilly;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by juansantiagoacev on 5/5/16.
 */
public class ViajesAdapter extends RecyclerView.Adapter<ViajesAdapter.MyViewHolder> {
    public Context mContext;
    private List<Viaje> viajesList;

    public ViajesAdapter(List<Viaje> viajeList) {
        this.viajesList = viajeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_viaje, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Viaje viaje = viajesList.get(position);
        String encodedPoly = PolyUtil.encode(viaje.getPath());
        String url = "https://maps.googleapis.com/maps/api/staticmap?size=1200x920&path=weight:5%7Ccolor:0x00C466%7Cenc:" + encodedPoly;
        Picasso.with(mContext).
                load(url)
                .fit()
                .into(holder.mapa);
        holder.distancia.setText(viaje.getKm() + "");
        holder.duracion.setText(viaje.getTiempo() + "");
        holder.fecha.setText(viaje.getFecha());
    }

    @Override
    public int getItemCount() {
        return viajesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView fecha, duracion, distancia;
        public ImageView mapa;

        public MyViewHolder(View view) {
            super(view);
            mapa = (ImageView) view.findViewById(R.id.viajelist_map);
            fecha = (TextView) view.findViewById(R.id.viajelist_fecha_dynamic);
            duracion = (TextView) view.findViewById(R.id.viajelist_duracion_dynamic);
            distancia = (TextView) view.findViewById(R.id.viajelist_distancia_dynamic);
            mContext = view.getContext();
        }
    }
}
