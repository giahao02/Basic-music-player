package com.example.musicplay;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaylistActivity extends AppCompatActivity {
    private PlaylistDatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // Khởi tạo đối tượng PlaylistDatabaseHelper
        databaseHelper = new PlaylistDatabaseHelper(this);

        // Lấy danh sách playlist từ cơ sở dữ liệu
        List<Song> playlist = databaseHelper.getAllSongs();

        // Tìm RecyclerView trong layout
        recyclerView = findViewById(R.id.playlist_recycler_view);

        // Thiết lập LayoutManager cho RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Tạo Adapter cho RecyclerView và thiết lập dữ liệu playlist
        playlistAdapter = new PlaylistAdapter(playlist);
        recyclerView.setAdapter(playlistAdapter);

        // Thiết lập sự kiện cho nút Thêm Playlist
        Button addPlaylistButton = findViewById(R.id.add_playlist_button);
        addPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị dialog để thêm playlist mới
                showAddPlaylistDialog();
            }
        });
    }

    private void showAddPlaylistDialog() {
        // Tạo dialog để thêm playlist mới
        // Có thể sử dụng AlertDialog hoặc DialogFragment để tạo dialog

        // Ví dụ sử dụng AlertDialog
        // Tạo AlertDialog.Builder và thiết lập các thuộc tính
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Playlist");

        // Tạo layout cho dialog
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);

        // Thêm EditText để người dùng nhập tiêu đề playlist mới
        EditText titleEditText = new EditText(this);
        titleEditText.setHint("Tiêu đề");
        dialogLayout.addView(titleEditText);

        // Thiết lập layout cho dialog
        builder.setView(dialogLayout);

        // Thiết lập nút Thêm và xử lý sự kiện khi nút được nhấn
        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            private void displayPlaylist() {
                List<Song> playlist = databaseHelper.getAllSongs();
                if (playlist.isEmpty()) {
                    // Hiển thị thông báo không có playlist
                    TextView noSongsText = findViewById(R.id.no_songs_text);
                    noSongsText.setVisibility(View.VISIBLE);
                } else {
                    // Hiển thị danh sách playlist trong RecyclerView
                    playlistAdapter = new PlaylistAdapter(playlist);
                    recyclerView.setAdapter(playlistAdapter);
                }
            }
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Lấy tiêu đề playlist từ EditText
                String title = titleEditText.getText().toString().trim();

                // Thêm playlist vào cơ sở dữ liệu và cập nhật giao diện
                databaseHelper.addSong(title);
                displayPlaylist();
            }
        });

        // Thiết lập nút Hủy và xử lý sự kiện khi nút được nhấn
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng dialog
                dialog.dismiss();
            }
        });



        // Hiển thị dialog
        builder.show();
    }

    private class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
        private List<Song> playlist;

        public PlaylistAdapter(List<Song> playlist) {
            this.playlist = playlist;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Song song = playlist.get(position);
            holder.titleTextView.setText(song.getTitle());
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    // Hiển thị dialog để sửa playlist
                    showEditPlaylistDialog(context, song);
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xóa playlist khỏi cơ sở dữ liệu và cập nhật giao diện
                    databaseHelper.deleteSong(song);
                    playlist.remove(song);
                    notifyDataSetChanged();
                }
            });
        }


        private void showEditPlaylistDialog(Context context, Song song) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Sửa playlist");

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(40, 40, 40, 40);

            EditText titleEditText = new EditText(context);
            titleEditText.setText(song.getTitle());
            layout.addView(titleEditText);

            builder.setView(layout);

            builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newTitle = titleEditText.getText().toString().trim();

                    song.setTitle(newTitle);

                    databaseHelper.updateSong(song);

                    playlistAdapter.notifyDataSetChanged();
                }
            });

            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }

        @Override
        public int getItemCount() {
            return playlist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView titleTextView;
            public Button editButton;
            public Button deleteButton;

            public ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.playlist_item_title);
                editButton = itemView.findViewById(R.id.playlist_item_edit);
                deleteButton = itemView.findViewById(R.id.playlist_item_delete);
            }
        }
    }
}
