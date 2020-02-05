package com.infinity.silmaperu.domain;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class MovieData extends RealmObject {

    private String levelId;

    private String movieId;

    private String guessedName;

    @PrimaryKey
    @Required
    private String movieName;

    private String shuffledName;

    private String status;

    private String wikiContent;

    private RealmList<String> mappingMap;


    public MovieData() {
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getGuessedName() {
        return guessedName;
    }

    public void setGuessedName(String guessedName) {
        this.guessedName = guessedName;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getShuffledName() {
        return shuffledName;
    }

    public void setShuffledName(String shuffledName) {
        this.shuffledName = shuffledName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWikiContent() {
        return wikiContent;
    }

    public void setWikiContent(String wikiContent) {
        this.wikiContent = wikiContent;
    }

    public RealmList<String> getMappingMap() {
        return mappingMap;
    }

    public void setMappingMap(RealmList<String> mappingMap) {
        this.mappingMap = mappingMap;
    }
}
