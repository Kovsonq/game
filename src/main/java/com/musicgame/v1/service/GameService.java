package com.musicgame.v1.service;

import com.musicgame.v1.model.Team;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GameService {

    private final List<Team> teams = new ArrayList<>();

    public List<Team> createTeams(int teamsNumber) {
        if (teamsNumber <= 0 || teamsNumber > 10) {
            return null;
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

    public List<Team> setTeamsName(List<Team> teams, List<String> teamsName) {
        if (teams.size() == teamsName.size()) {
            for (Team team : teams) {
                team.setName(teamsName.get(team.getIndex() - 1));
            }
            return teams;
        } else return null;
    }

    public Team getTeamByName(String teamName) {
        return this.teams.stream()
                .filter(team -> team.getName().equals(teamName))
                .findFirst()
                .orElse(null);
    }

    public List<Team> separateSongListByTeams(final List<Team> teams, final String sourceText) {
        List<String> sourceList = Arrays.stream(sourceText.split("\\r?\\n"))
                .collect(Collectors.toList());
        List<String> clearList = sourceList.stream()
                .filter(song -> !song.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        List<String> processedSongs = checkAndProcessSongList(clearList, teams.size());
        if (processedSongs == null) {
            return null;
        }

        int songsPerTeam = processedSongs.size() / teams.size();
        for (Team team : teams) {
            processedSongs.stream().limit(songsPerTeam).forEach(team::addSong);
            processedSongs.removeAll(team.getSongList());
        }
        return teams;
    }

    private List<String> checkAndProcessSongList(final List<String> songs, int teamsNumber) {
        if (songs == null || songs.isEmpty() || songs.size() < teamsNumber) {
            return null;
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

    public String getWinner() {
        String possibleWinnerName = this.teams.stream()
                .sorted()
                .findFirst()
                .map(Team::getName)
                .orElse(null);

        Team possibleWinner = getTeamByName(possibleWinnerName);
        int rightAnswerWinner = possibleWinner.getRightSongList().size();
        int wrongAnswerWinner = possibleWinner.getFalseSongList().size();

        List<Team> rightAnswerWinners = this.teams.stream()
                .filter(team -> team.getRightSongList().size() == rightAnswerWinner)
                .collect(Collectors.toList());

        if (rightAnswerWinners.size() > 1) {
            List<Team> wrongAnswerWinners = rightAnswerWinners.stream()
                    .filter(team -> team.getFalseSongList().size() == wrongAnswerWinner)
                    .collect(Collectors.toList());

            if (wrongAnswerWinners.size() > 1){
                return "DRAW";
            } else {
                return wrongAnswerWinners.stream()
                        .min(Comparator.comparingInt(team -> team.getFalseSongList().size()))
                        .map(Team::getName)
                        .orElse(null);
            }
        } else {
            return possibleWinnerName;
        }
    }

    public List<Team> getTeams() {
        return teams;
    }
}
