package com.musicgame.v1.service;

import com.musicgame.v1.exception.SongListCreatingException;
import com.musicgame.v1.exception.TeamNotFoundException;
import com.musicgame.v1.exception.TeamsCreatingException;
import com.musicgame.v1.exception.WinnerNotFoundException;
import com.musicgame.v1.model.Team;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@SessionScope
public class GameService {

    private final List<Team> teams = new ArrayList<>();

    public List<Team> createTeams(int teamsNumber) throws TeamsCreatingException {
        if (teamsNumber <= 0 || teamsNumber > 10) {
            throw new TeamsCreatingException("Wrong number of teams entered. It should be in the range 1-10.");
        }
        return Stream.generate(this::createTeam)
                .limit(teamsNumber)
                .collect(Collectors.toList());
    }

    private Team createTeam() {
        Team team = new Team();
        team.setIndex(teams.size() + 1);
        this.teams.add(team);
        return team;
    }

    public void setTeamsName(List<Team> teams, List<String> teamsName) throws TeamsCreatingException {
        if (!isNamesCorrect(teamsName)) {
            throw new TeamsCreatingException("All team names should be different and not blank try again.");
        }

        if (teams.size() != teamsName.size()) {
            throw new TeamsCreatingException("Teams number and team names number is different, try again");
        }
        teams.forEach(team -> team.setName(teamsName.get(team.getIndex() - 1)));
    }

    private boolean isNamesCorrect(List<String> teamsName) {
        Set<String> names = new HashSet<>();
        for (String teamName : teamsName) {
            if (!teamName.isBlank()) {
                names.add(teamName);
            }
        }
        return names.size() == teamsName.size();
    }

    public Team getTeamByName(String teamName) throws TeamNotFoundException {
        return this.teams.stream()
                .filter(team -> team.getName().equals(teamName))
                .findFirst()
                .orElseThrow(() -> new TeamNotFoundException("Can't find team by name."));
    }

    public List<Team> separateSongListByTeams(final List<Team> teams, final String sourceText) throws SongListCreatingException {
        List<String> sourceSongList = Arrays.stream(sourceText.split("\\r?\\n"))
                .collect(Collectors.toList());
        List<String> clearSongList = sourceSongList.stream()
                .filter(song -> !song.isBlank())
                .distinct()
                .collect(Collectors.toList());

        List<String> processedSongList = checkAndProcessSongList(clearSongList, teams.size());
        if (processedSongList == null) {
            throw new SongListCreatingException("Song list should contain more than teams number songs. " +
                    "Song shouldn't be blank.");
        }

        int songsPerTeam = processedSongList.size() / teams.size();
        for (Team team : teams) {
            processedSongList.stream()
                    .limit(songsPerTeam)
                    .forEach(team::addSong);
            processedSongList.removeAll(team.getSongList());
        }
        return teams;
    }

    private List<String> checkAndProcessSongList(final List<String> songs, int teamsNumber) throws SongListCreatingException {
        if (songs == null || songs.isEmpty() || songs.size() < teamsNumber) {
            throw new SongListCreatingException("Song list is empty or has less than teams number songs. Try again.");
        }
        return songs.stream()
                .limit(songs.size() - songs.size() % teamsNumber)
                .collect(
                        Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new),
                                list -> {
                                    Collections.shuffle(list);
                                    return list;
                                }
                        )
                );
    }

    public void processAnswer(Team team, String songName, Boolean answer) {
        if (Boolean.TRUE.equals(answer)) {
            team.rightAnswer(songName);
        } else {
            team.falseAnswer(songName);
        }
    }

    public void restartGame() {
        this.teams.clear();
    }

    public String getWinner() throws WinnerNotFoundException {
        Team possibleWinner = this.teams.stream()
                .sorted()
                .findFirst()
                .orElseThrow(() -> new WinnerNotFoundException("Winner not found. Program error."));

        int rightAnswerWinner = possibleWinner.getRightSongList().size();
        List<Team> rightAnswerWinners = this.teams.stream()
                .filter(team -> team.getRightSongList().size() == rightAnswerWinner)
                .collect(Collectors.toList());

        if (rightAnswerWinners.size() > 1) {
            return "DRAW";
        } else {
            return possibleWinner.getName();
        }
    }

    public List<Team> getTeams() {
        return teams;
    }

}
