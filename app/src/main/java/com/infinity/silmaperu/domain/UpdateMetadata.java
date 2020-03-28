package com.infinity.silmaperu.domain;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class UpdateMetadata extends RealmObject {

    @PrimaryKey
    @Required
    private String id;

    private String key;

    public UpdateMetadata() {
    }

    public UpdateMetadata(String id, String key) {
        this.id = id;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
