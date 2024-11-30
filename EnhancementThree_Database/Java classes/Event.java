package com.zybooks.thierrytran_eventtrackingapp;

public class Event implements Comparable<Event> {
    private long id;
    private String name;

    public Event(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Event other) {
        return this.name.compareToIgnoreCase(other.name);
    }
}
