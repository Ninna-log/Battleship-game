package com.mindhub.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import java.util.stream.Collectors;

@RestController // It's a controller specialization in order to build an API Rest that listens to petitions and send the required petitions
@RequestMapping("/api") // With the @RequestMapping annotation the URL rest/games is copied or Mapped to the URL /api/games // /api it´s the path where the controller is gonna be found
public class SalvoController {

    @Autowired // Dependency Injection with Spring
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

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
        dto.put("gamePlayers", game.getPlayers().stream().map(player -> makePlayerDTO(player)).collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> makePlayerDTO(Player player) {  // DTO = Data Transfer Object
        Map<String, Object> dto = new LinkedHashMap<String, Object>(); //
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }

    private Map<String, Object> makeShipDTO(Ship ship) {  // DTO = Data Transfer Object
        Map<String, Object> dto = new LinkedHashMap<String, Object>(); //
        dto.put("type", ship.getType());
        dto.put("location", ship.locations);
        return dto;
    }

    private Map<String, Object> toGameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("date", gamePlayer.getDate());
        dto.put("gamePlayers", gamePlayer.getGame().getPlayers().stream().map(player -> makePlayerDTO(player)).collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips().stream().map(ship -> makeShipDTO(ship)).collect(Collectors.toList()));
        return dto;
    }
}
