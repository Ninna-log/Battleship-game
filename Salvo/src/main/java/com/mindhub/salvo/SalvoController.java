package com.mindhub.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController // It's a controller specialization in order to build an API Rest that listens to petitions and send the required petitions
@RequestMapping("/api") // With the @RequestMapping annotation the URL rest/games is copied or Mapped to the URL /api/games // /api it´s the path where the controller is gonna be found
public class SalvoController {

    @Autowired // Dependency Injection with Spring
    private GameRepository gameRepository;

    @Autowired // Dependency Injection with Spring
    private SalvoRepository salvoRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(path = "/api/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String userName, @RequestParam String password) {

        if (userName.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(userName) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(userName, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @RequestMapping(value = "/games", method = RequestMethod.GET)
    private Object currentPlayer(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("player", makePlayerDTO(playerRepository.findByUserName(authentication.getName())));
        dto.put("games", getGames());
        return dto;
    }

    @RequestMapping("/games")
    public List<Object> getGames() {

      return gameRepository.findAll().stream().map(game -> makeGameDTO(game)).collect(Collectors.toList());
   }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String,Object> getGameView(@PathVariable Long gamePlayerId) {

        return toGameViewDTO(gamePlayerRepository.findById(gamePlayerId).get());
    }

    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        dto.put("id", game.getId());
        dto.put("date", game.getDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(gamePlayer -> makeGamePlayerDTO(gamePlayer)).collect(Collectors.toList()));
        return dto;
    }

    private Object makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        dto.put("score", gamePlayer.getScore() != null ? gamePlayer.getScore().getScore():null);
        return dto;
    }

    private Map<String, Object> makePlayerDTO(Player player) {  // DTO = Data Transfer Object
        Map<String, Object> dto = new LinkedHashMap<String, Object>(); //
        dto.put("id", player.getId());
        dto.put("name", player.getUserName());
        return dto;
    }

    private Map<String, Object> makeShipDTO(Ship ship) {  // DTO = Data Transfer Object
        Map<String, Object> dto = new LinkedHashMap<String, Object>(); //
        dto.put("type", ship.getType());
        dto.put("location", ship.getLocations());
        return dto;
    }

    private Map<String, Object> makeSalvoDTO(Salvo salvo) {  // DTO = Data Transfer Object
        Map<String, Object> dto = new LinkedHashMap<String, Object>(); //
        dto.put("turn", salvo.getTurn());
        dto.put("player", salvo.getGamePlayer().getPlayer().getId());
        dto.put("location", salvo.getLocations());
        return dto;
    }

    private Map<String, Object> toGameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("date", gamePlayer.getDate());
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers().stream().map(gamePlayer1 -> makeGamePlayerDTO(gamePlayer1)).collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips().stream().map(ship -> makeShipDTO(ship)).collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(gamePlayer1 -> gamePlayer1.getSalvos().stream()).map(salvo -> makeSalvoDTO(salvo)).collect(Collectors.toList()));
        return dto;
    }

}
