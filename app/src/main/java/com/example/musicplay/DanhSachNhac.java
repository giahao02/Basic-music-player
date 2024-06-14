package com.example.musicplay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DanhSachNhac extends RecyclerView.Adapter<DanhSachNhac.ViewHolder>{
    ArrayList<ThongTinNhac> songsList;
    Context context;
    boolean flag=true;

    public DanhSachNhac(ArrayList<ThongTinNhac> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.danhsach,parent,false);
        return new DanhSachNhac.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DanhSachNhac.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int vitri= holder.getAdapterPosition();
        ThongTinNhac songData = songsList.get(vitri);
        holder.titleTextView.setText(songData.getTitle());

        if(KiemTra.currentIndex==vitri){
            holder.titleTextView.setTextColor(Color.parseColor("#FF0000"));
        }else{
            holder.titleTextView.setTextColor(Color.parseColor("#000000"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to another acitivty

                    KiemTra.getInstance().reset();
                    KiemTra.currentIndex = vitri-1;
                    Intent intent = new Intent(context, PhatNhac.class);
                    intent.putExtra("LIST", songsList);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

            }
        });
        holder.addPlaylistImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Thêm vào Playlist");

                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_playlist, null);
                builder.setView(dialogView);

                RecyclerView playlistRecyclerView = dialogView.findViewById(R.id.playlist_recyclerview);
                EditText playlistNameEditText = dialogView.findViewById(R.id.playlist_name_edittext);
                Button addPlaylistButton = dialogView.findViewById(R.id.add_playlist_button);

                playlistRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                // playlistRecyclerView.setAdapter(new PlaylistAdapter(playlistList));

                addPlaylistButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String playlistName = playlistNameEditText.getText().toString();

                        //     AlertDialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        ImageView iconImageView;
        ImageView addPlaylistImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.music_title_text);
            iconImageView = itemView.findViewById(R.id.icon_view);
            addPlaylistImageView = itemView.findViewById(R.id.addplaylist);
        }
    }
}
