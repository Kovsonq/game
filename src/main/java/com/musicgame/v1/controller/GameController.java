package com.musicgame.v1.controller;

import com.musicgame.v1.model.Team;
import com.musicgame.v1.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/teams/name")
    public String setTeams(@RequestParam Integer teamsNumber, Model model) {
        gameService.restartGame();
        List<Team> teams = gameService.createTeams(teamsNumber);
        model.addAttribute("teams", teams);
        return "teams-name";
    }

    @PostMapping("/game/content")
    public String setTeamsName(@RequestParam List<String> sourceTeamsName) {
        List<Team> teams = gameService.setTeamsName(gameService.getTeams(), sourceTeamsName);
        if (teams == null) {
            gameService.restartGame();
            return "index";
        }
        return "game-content";
    }

    @PostMapping("/game/overview")
    public String gameOverview(@RequestParam String sourceText, Model model) {
        List<Team> teams = gameService.separateSongListByTeams(gameService.getTeams(), sourceText);
        model.addAttribute("teams", teams);
        return "game-overview";
    }

    @GetMapping("/game/progress")
    public String game(Model model) {
        List<Team> teams = gameService.getTeams();
        for (Team team : teams) {
            if (!team.getSongList().isEmpty()) {
                model.addAttribute("team", team);
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
        model.addAttribute("teams", gameService.getTeams());
        model.addAttribute("winner", gameService.getWinner());
        return "game-result";
    }

    @PostMapping("/game/answer")
    public String answerRight(@RequestParam String teamName,
                              @RequestParam String songName,
                              @RequestParam(required = false) Boolean answer) {
        Team team = gameService.getTeamByName(teamName);
        gameService.processAnswer(team, songName, answer);
        return "redirect:/game/progress";
    }

    @GetMapping("/game/restart")
    public String restartGame() {
        gameService.restartGame();
        return "index";
    }

}
