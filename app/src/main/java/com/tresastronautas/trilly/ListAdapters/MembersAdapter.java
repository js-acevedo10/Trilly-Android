package com.tresastronautas.trilly.ListAdapters;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tresastronautas.trilly.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JuanSantiagoAcev on 15/05/16!
 */
public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder> {

    public Context mContext;
    public List<Member> memberList;

    public MembersAdapter(List<Member> memberList) {
        this.memberList = memberList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_member, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.memberlist_layout_member.setBackgroundResource(R.drawable.memberlist_imagen_placeholderclaro);
        } else {
            holder.memberlist_layout_member.setBackgroundResource(R.drawable.memberlist_imagen_placeholderoscuro);
        }

        holder.memberlist_text_nombre.setText(position + 1 + ". " + memberList.get(position).getNombre());
        holder.memberlist_text_arboles_dynamic.setText(mContext.getString(R.string.memberlist_arboles_dynamic, Math.floor(memberList.get(position).getArboles())));
        if (position == 0) {
            holder.memberlist_imagen_corona.setVisibility(View.VISIBLE);
        }
        Picasso.with(mContext)
                .load(memberList.get(position).getPictureUrl())
                .placeholder(R.drawable.ajustes_imagen_perfilstock)
                .error(R.drawable.ajustes_imagen_perfilstock)
                .into(holder.memberlist_imagen_perfil);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView memberlist_text_nombre, memberlist_text_arboles_dynamic, memberlist_text_arboles_static;
        public ImageView memberlist_imagen_corona;
        public PercentRelativeLayout memberlist_layout_member;
        public CircleImageView memberlist_imagen_perfil;

        public MyViewHolder(View view) {
            super(view);
            memberlist_layout_member = (PercentRelativeLayout) view.findViewById(R.id.memberlist_layout_member);
            memberlist_text_nombre = (TextView) view.findViewById(R.id.memberlist_text_nombre);
            memberlist_text_arboles_dynamic = (TextView) view.findViewById(R.id.memberlist_text_arboles_dynamic);
            memberlist_text_arboles_static = (TextView) view.findViewById(R.id.Memberlist_text_arboles_static);
            memberlist_imagen_corona = (ImageView) view.findViewById(R.id.memberlist_imagen_corona);
            memberlist_imagen_perfil = (CircleImageView) view.findViewById(R.id.memberlist_imagen_perfil);
            mContext = view.getContext();
        }
    }
}
