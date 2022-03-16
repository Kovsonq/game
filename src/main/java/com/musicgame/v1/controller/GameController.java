package com.musicgame.v1.controller;

import com.musicgame.v1.exception.SongListCreatingException;
import com.musicgame.v1.exception.TeamNotFoundException;
import com.musicgame.v1.exception.TeamsCreatingException;
import com.musicgame.v1.exception.WinnerNotFoundException;
import com.musicgame.v1.model.Team;
import com.musicgame.v1.service.GameService;
import com.musicgame.v1.utils.DefaultSongLists;
import com.musicgame.v1.utils.StatisticUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Collections;
import java.util.List;

@Controller
public class GameController {

    private final StatisticUtil statisticUtil;
    private final GameService gameService;

    @Autowired
    public GameController(StatisticUtil statisticUtil, GameService gameService) {
        this.statisticUtil = statisticUtil;
        this.gameService = gameService;
    }

    @PostMapping("/teams/name")
    public String setTeams(@RequestParam Integer teamsNumber, Model model) {
        gameService.restartGame();
        try {
            List<Team> teams = gameService.createTeams(teamsNumber);
            model.addAttribute("teams", teams);
        } catch (TeamsCreatingException e) {
            model.addAttribute("error", e.getMessage());
            return "index";
        }
        return "teams-name";
    }

    @PostMapping("/game/content")
    public String setTeamsName(@RequestParam List<String> sourceTeamsName, Model model) {
        List<Team> teams = gameService.getTeams();
        if (teams == null || teams.isEmpty()) {
            return "index";
        }
        try {
            gameService.setTeamsName(teams, sourceTeamsName);
        } catch (TeamsCreatingException e) {

            model.addAttribute("teams", teams);
            model.addAttribute("error", e.getMessage());
            return "teams-name";
        }
        return "game-content";
    }

    @PostMapping("/game/overview")
    public String gameOverviewCustom(@RequestParam(required = false) String sourceText, Model model) {
        try {
            List<Team> teams;
            if (sourceText == null || sourceText.isBlank()) {
                teams = gameService.separateSongListByTeams(gameService.getTeams(), DefaultSongLists.russianSongsOne);
            } else {
                teams = gameService.separateSongListByTeams(gameService.getTeams(), sourceText);
            }
            model.addAttribute("teams", teams);
        } catch (SongListCreatingException e) {
            model.addAttribute("error", e.getMessage());
            return "game-content";
        }
        return "game-overview";
    }

    @GetMapping("/game/progress")
    public String game(Model model) {
        List<Team> teams = gameService.getTeams();
        for (Team team : teams) {
            if (!team.getSongList().isEmpty()) {
                model.addAttribute("team", team);
                model.addAttribute("rightAnswer", team.getRightSongList().size());
                model.addAttribute("wrongAnswer", team.getFalseSongList().size());
                model.addAttribute("songs", team.getSongList().size());
                model.addAttribute("song", team.getSong());
                return "game-progress";
            }
            if (team.getIndex() != teams.size() && !team.isFinished()) {
                team.setFinished(true);
                model.addAttribute("nextTeam", teams.get(team.getIndex()));
                model.addAttribute("changeTeam", true);
                return "game-progress";
            }
        }

        try {
            model.addAttribute("winner", gameService.getWinner());
        } catch (WinnerNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "game-result";
        }
        Collections.sort(gameService.getTeams());
        model.addAttribute("teams", gameService.getTeams());

        return "game-result";
    }

    @PostMapping("/game/answer")
    public String answerRight(@RequestParam String teamName,
                              @RequestParam String songName,
                              @RequestParam(required = false) Boolean answer,
                              Model model) {
        try {
            Team team = gameService.getTeamByName(teamName);
            gameService.processAnswer(team, songName, answer);
        } catch (TeamNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "index";
        }
        return "redirect:/game/progress";
    }

    @GetMapping("/")
    public String restartGame() {
        statisticUtil.addVisitor(RequestContextHolder.currentRequestAttributes().getSessionId());
        gameService.restartGame();
        return "index";
    }

    @GetMapping("/visitors")
    public String showVisitors(Model model) {
        model.addAttribute("visitorsNumber", statisticUtil.viewVisitors());
        model.addAttribute("visitors", statisticUtil.getVisitors());
        return "statistic-visitors";
    }

}
