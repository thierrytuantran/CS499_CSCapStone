package com.zybooks.thierrytran_eventtrackingapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventNameTextView.setText(event.getName());

        holder.deleteButton.setOnClickListener(v -> {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            int rowsDeleted = databaseHelper.deleteEvent(event.getId());
            if (rowsDeleted > 0) {
                eventList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error deleting event", Toast.LENGTH_SHORT).show();
            }
        });

        holder.eventNameTextView.setOnClickListener(v -> {
            ((DataDisplayActivity) context).showUpdateEventDialog(event, position);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView eventNameTextView;
        Button deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_name_text_view);
            deleteButton = itemView.findViewById(R.id.delete_event_button);
        }
    }

    public void sortEvents(Comparator<Event> comparator) {
        executor.execute(() -> {
            Collections.sort(eventList, comparator);
            mainHandler.post(this::notifyDataSetChanged);
        });
    }
    public void updateEvent(int position, String eventName) {
        Event event = eventList.get(position);
        event.setName(eventName);
        notifyItemChanged(position);
    }

}
