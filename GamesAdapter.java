package com.example.dissertation_tester;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GameViewHolder> {
    private final List<String> games;
    private final OnItemClickListener listener;

    // Interface for click handling
    public interface OnItemClickListener {
        void onItemClick(String gameKey);
    }

    // Constructor with null safety
    public GamesAdapter(List<String> games, OnItemClickListener listener) {
        // Defensive copy and null filtering
        this.games = games != null ? new ArrayList<>(games) : new ArrayList<>();
        this.listener = listener;

        // Remove any null values that might have slipped through
        this.games.removeIf(Objects::isNull);
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_item_layouot, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        String gameKey = games.get(position);
        holder.bind(gameKey, listener);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    // ViewHolder class with improved null safety
    static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView gameTitle;
        TextView gameDescription;
        ImageView gameIcon;
        ImageView playButton;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameTitle = itemView.findViewById(R.id.gameTitle);
            gameDescription = itemView.findViewById(R.id.gameDescription);
            gameIcon = itemView.findViewById(R.id.gameIcon);
            playButton = itemView.findViewById(R.id.playButton);
        }

        public void bind(final String gameKey, final OnItemClickListener listener) {
            // IMPROVED: Null safety for game key
            String displayTitle = gameKey != null && !gameKey.trim().isEmpty()
                    ? gameKey
                    : "Unknown Game";
            gameTitle.setText(displayTitle);

            gameDescription.setText("Tap to play!");
            gameIcon.setImageResource(R.drawable.ic_game_controller);

            // Only set click listeners if we have valid data
            if (gameKey != null && !gameKey.trim().isEmpty() && listener != null) {
                itemView.setOnClickListener(v -> listener.onItemClick(gameKey));
                playButton.setOnClickListener(v -> listener.onItemClick(gameKey));
            } else {
                // Clear any existing listeners for safety
                itemView.setOnClickListener(null);
                playButton.setOnClickListener(null);
            }
        }
    }
}

