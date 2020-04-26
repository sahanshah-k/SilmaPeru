package com.infinity.silmaperu.domain;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class LevelMetadata extends RealmObject {

    @PrimaryKey
    @Required
    private String id;

    private int count;

    public LevelMetadata() {
    }

    public LevelMetadata(String id, int count) {
        this.id = id;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
