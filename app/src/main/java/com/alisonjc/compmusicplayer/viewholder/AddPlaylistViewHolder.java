package com.alisonjc.compmusicplayer.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alisonjc.compmusicplayer.R;
import com.alisonjc.compmusicplayer.adapter.PlaylistAdapter;
import com.alisonjc.compmusicplayer.spotify.spotify_model.PlaylistModel.Item;
import com.squareup.picasso.Picasso;


public class AddPlaylistViewHolder extends RecyclerView.ViewHolder {
    public View itemView;
    public EditText addPlaylistEditText;
    public Button createButton;
    private String mPlaylistTitle;

    public AddPlaylistViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        addPlaylistEditText = (EditText) itemView.findViewById(R.id.add_a_playlist_header_text);
        createButton = (Button) itemView.findViewById(R.id.create_button);

        addPlaylistEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPlaylistTitle = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void bind(PlaylistAdapter.onCreateClickListener listener) {
        createButton.setOnClickListener(view ->
                listener.onCreateClick(mPlaylistTitle));
    }
}
