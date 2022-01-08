package com.musicgame.v1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GameController {

    static List<String> firstMusicList = new ArrayList<>();
    static List<String> secondMusicList = new ArrayList<>();
    static Integer listSize = 0;

    static boolean firstPlayerDone = false;

    static Integer firstPlayerRight = 0;
    static Integer firstPlayerFalse = 0;
    static Integer secondPlayerRight = 0;
    static Integer secondPlayerFalse = 0;

    @PostMapping("/start-game")
    public String startGame(@RequestParam String sourceText, Model model) {

        if (!sourceText.isEmpty() || !sourceText.isBlank()) {
            List<String> sourceList = Arrays.stream(sourceText.split("\\r?\\n")).collect(Collectors.toList());

            if (sourceList.size() > 2) {
                listSize = sourceList.size();

                for (int i = 0; i < sourceList.size(); i++) {

                    if (i < sourceList.size() / 2) {
                        firstMusicList.add(sourceList.get(i));
                    } else {
                        secondMusicList.add(sourceList.get(i));
                    }
                }
            } else {
                model.addAttribute("error", "Should be more than 2 songs!!!\n Please try again!");
            }
        } else {
            model.addAttribute("error", "Should be not empty and blank!!!\n Please try again!");
        }

        return "game-page";
    }

    @PostMapping("/make-answer-right")
    public String makeAnswerRight(Model model) {
        if (secondMusicList.isEmpty()) {
            model.addAttribute("firstPlayerRight", firstPlayerRight);
            model.addAttribute("firstPlayerFalse", firstPlayerFalse);
            model.addAttribute("secondPlayerRight", secondPlayerRight);
            model.addAttribute("secondPlayerFalse", secondPlayerFalse);

            String winner;
            if (firstPlayerRight.equals(secondPlayerRight)) {
                winner = "Draw";
            } else if (firstPlayerRight > secondPlayerRight) {
                winner = "FIRST TEAM WON!";
            } else {
                winner = "SECOND TEAM WON!";
            }
            model.addAttribute("winner", winner);

            return "result-page";
        }

        if (!firstPlayerDone) {
            int randomMusic = (int) (Math.random() * firstMusicList.size());
            model.addAttribute("player", "FIRST PLAYER");
            model.addAttribute("lastSongs", firstMusicList.size());
            model.addAttribute("nextSong", firstMusicList.get(randomMusic));
            firstMusicList.remove(randomMusic);
            if (firstMusicList.isEmpty()) {
                firstPlayerDone = true;
            }
            firstPlayerRight++;
        } else {
            int randomMusic = (int) (Math.random() * secondMusicList.size());
            model.addAttribute("player", "SECOND PLAYER");
            model.addAttribute("lastSongs", secondMusicList.size());
            model.addAttribute("nextSong", secondMusicList.get(randomMusic));
            secondMusicList.remove(randomMusic);
            secondPlayerRight++;
        }

        return "game-page";
    }

    @PostMapping("/make-answer-false")
    public String makeAnswerFalse(Model model) {
        if (secondMusicList.isEmpty()) {
            model.addAttribute("firstPlayerRight", firstPlayerRight);
            model.addAttribute("firstPlayerFalse", firstPlayerFalse);
            model.addAttribute("secondPlayerRight", secondPlayerRight);
            model.addAttribute("secondPlayerFalse", secondPlayerFalse);

            String winner;
            if (firstPlayerRight.equals(secondPlayerRight)) {
                winner = "Draw";
            } else if (firstPlayerRight > secondPlayerRight) {
                winner = "FIRST TEAM WON!";
            } else {
                winner = "SECOND TEAM WON!";
            }
            model.addAttribute("winner", winner);

            return "result-page";
        }

        if (!firstPlayerDone) {
            int randomMusic = (int) (Math.random() * firstMusicList.size());
            model.addAttribute("player", "FIRST PLAYER");
            model.addAttribute("lastSongs", firstMusicList.size());
            model.addAttribute("nextSong", firstMusicList.get(randomMusic));
            firstMusicList.remove(randomMusic);
            if (firstMusicList.isEmpty()) {
                firstPlayerDone = true;
            }
            firstPlayerFalse++;
        } else {
            int randomMusic = (int) (Math.random() * secondMusicList.size());
            model.addAttribute("player", "SECOND PLAYER");
            model.addAttribute("lastSongs", secondMusicList.size());
            model.addAttribute("nextSong", secondMusicList.get(randomMusic));
            secondMusicList.remove(randomMusic);
            secondPlayerFalse++;
        }

        return "game-page";
    }

    @GetMapping("/restart-game")
    public String restartGame() {
        firstMusicList.clear();
        secondMusicList.clear();
        listSize = 0;
        firstPlayerRight = 0;
        firstPlayerFalse = 0;
        secondPlayerRight = 0;
        secondPlayerFalse = 0;
        firstPlayerDone = false;
        return "index";
    }

}
