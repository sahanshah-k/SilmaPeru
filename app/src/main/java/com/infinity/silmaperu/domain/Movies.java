package com.infinity.silmaperu.domain;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Movies extends RealmObject {

    private RealmList<MovieData> movieDataRealmList;

    public Movies() {
    }

    public Movies(RealmList<MovieData> movieDataRealmList) {
        this.movieDataRealmList = movieDataRealmList;
    }

    public RealmList<MovieData> getMovieDataRealmList() {
        return movieDataRealmList;
    }

    public void setMovieDataRealmList(RealmList<MovieData> movieDataRealmList) {
        this.movieDataRealmList = movieDataRealmList;
    }
}
