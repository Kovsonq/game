package com.musicgame.v1.model;

import java.util.ArrayList;
import java.util.List;

public class Team implements Comparable<Team> {

    private String name;
    private int index;
    private boolean isFinished = false;
    private final List<String> rightSongList = new ArrayList<>();
    private final List<String> falseSongList = new ArrayList<>();
    private final List<String> songList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<String> getRightSongList() {
        return rightSongList;
    }

    public List<String> getFalseSongList() {
        return falseSongList;
    }

    public List<String> getSongList() {
        return songList;
    }

    public void rightAnswer(String songName){
        this.songList.remove(songName);
        this.rightSongList.add(songName);
    }

    public void falseAnswer(String songName){
        this.songList.remove(songName);
        this.falseSongList.add(songName);
    }

    public String getSong(){
        if (this.songList.isEmpty()) {
            return null;
        }
        return this.songList.get(0);
    }

    public void addSong(String songName){
        this.songList.add(songName);
    }

    public void removeSong(int songIndex){
        this.songList.remove(songIndex);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    @Override
    public int compareTo(Team o) {
        return o.getRightSongList().size() - this.getRightSongList().size();
    }
}
