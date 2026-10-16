package com.example.production.Utils;

import com.example.production.Domain.Article;
import com.example.production.Domain.Workstation;

/**
 * Represents an event in the production simulation.
 * An event can be the start or finish of processing an article at a workstation.
 */
public class Event implements Comparable<Event> {
    private final int time;
    private final Workstation workstation;
    private final Article article;
    private final String operation;
    private final boolean isStart;

    public Event(int time, Workstation workstation, Article article, String operation, boolean isStart) {
        this.time = time;
        this.workstation = workstation;
        this.article = article;
        this.operation = operation;
        this.isStart = isStart;
    }

    public int getTime() {
        return time;
    }

    public Workstation getWorkstation() {
        return workstation;
    }

    public Article getArticle() {
        return article;
    }

    public String getOperation() {
        return operation;
    }

    public boolean isStart() {
        return isStart;
    }

    @Override
    public int compareTo(Event other) {
        return Integer.compare(this.time, other.time);
    }
}
