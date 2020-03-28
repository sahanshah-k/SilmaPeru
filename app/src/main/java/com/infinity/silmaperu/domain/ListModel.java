package com.infinity.silmaperu.domain;

public class ListModel {
    private int level;
    private int total;
    private int totalDone;

    public ListModel(int level, int total, int totalDone) {
        this.level = level;
        this.total = total;
        this.totalDone = totalDone;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalDone() {
        return totalDone;
    }

    public void setTotalDone(int totalDone) {
        this.totalDone = totalDone;
    }
}
