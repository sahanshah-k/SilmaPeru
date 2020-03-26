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

    private RealmList<MappingMap> mappingMap;


    public MovieData() {
    }

    public MovieData(String levelId, String movieId, String guessedName, String movieName, String shuffledName, String status, String wikiContent, RealmList<MappingMap> mappingMap) {
        this.levelId = levelId;
        this.movieId = movieId;
        this.guessedName = guessedName;
        this.movieName = movieName;
        this.shuffledName = shuffledName;
        this.status = status;
        this.wikiContent = wikiContent;
        this.mappingMap = mappingMap;
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

    public RealmList<MappingMap> getMappingMap() {
        return mappingMap;
    }

    public void setMappingMap(RealmList<MappingMap> mappingMap) {
        this.mappingMap = mappingMap;
    }
}
