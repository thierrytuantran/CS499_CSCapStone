package com.zybooks.thierrytran_eventtrackingapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DataDisplayActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        // Set up toolbar with the title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("T3 Event Tracking");

        databaseHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this);
        recyclerView.setAdapter(eventAdapter);

        // Get user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("EventTrackingApp", MODE_PRIVATE);
        userId = sharedPreferences.getLong("user_id", -1);

        // Load events for the user
        if (userId != -1) {
            loadEvents();
        } else {
            Toast.makeText(this, "Error loading user events", Toast.LENGTH_SHORT).show();
        }

        Button addEventButton = findViewById(R.id.add_event_button);
        addEventButton.setOnClickListener(v -> showAddEventDialog());
    }

    private void loadEvents() {
        eventList.clear();
        Cursor cursor = databaseHelper.getAllEvents(userId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long eventId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_ID));
                String eventName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_NAME));
                eventList.add(new Event(eventId, eventName));
            } while (cursor.moveToNext());
            cursor.close();
        }
        eventAdapter.notifyDataSetChanged();
    }

    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Event");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = viewInflated.findViewById(R.id.input_event_name);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            String eventName = input.getText().toString();
            addEvent(eventName);
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void showUpdateEventDialog(Event event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Event");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = viewInflated.findViewById(R.id.input_event_name);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(event.getName());
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            String eventName = input.getText().toString();
            updateEvent(event, eventName, position);
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addEvent(String eventName) {
        if (!eventName.isEmpty()) {
            long eventId = databaseHelper.addEvent(eventName, userId);
            if (eventId != -1) {
                eventList.add(new Event(eventId, eventName));
                eventAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error adding event", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Event name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEvent(Event event, String eventName, int position) {
        if (!eventName.isEmpty()) {
            int rowsUpdated = databaseHelper.updateEvent(event.getId(), eventName);
            if (rowsUpdated > 0) {
                eventAdapter.updateEvent(position, eventName);
                Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error updating event", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Event name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
