package com.infinity.silmaperu.domain;

public class ListModel {
    private int level;
    private int total;
    private int totalDone;
    private boolean lockStatus;
    private int toUnlock;

    public ListModel(int level, int total, int totalDone, boolean lockStatus, int toUnlock) {
        this.level = level;
        this.total = total;
        this.totalDone = totalDone;
        this.lockStatus = lockStatus;
        this.toUnlock = toUnlock;
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

    public boolean isLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(boolean lockStatus) {
        this.lockStatus = lockStatus;
    }

    public int getToUnlock() {
        return toUnlock;
    }

    public void setToUnlock(int toUnlock) {
        this.toUnlock = toUnlock;
    }

}
