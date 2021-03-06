package com.mindhub.salvo.controller;

import com.mindhub.salvo.model.*;
import com.mindhub.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
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

    @Autowired
    private ShipRepository shipRepository;

    @PostMapping("/players")
    public ResponseEntity<Object> register(@RequestParam String username, @RequestParam String password) {

        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(AppMessages.MSG_MISSING_DATA, HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(username) != null) {
            return new ResponseEntity<>(AppMessages.MSG_NAME_ALREADY_USED, HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/games") // adding the ability to create a game
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.NO_ACCESS), HttpStatus.UNAUTHORIZED);
            // gets the current user with the authentication object, and if there is none, it is sent an Unauthorized response
        } else {
            Game game = gameRepository.save(new Game(LocalDateTime.now()));
            Player player = playerRepository.findByUserName(authentication.getName());
            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(player, game, LocalDateTime.now()));
            // creates and saves a new game, and then saves a new gamePlayer for this game and the current user
            return new ResponseEntity<>(makeMap(AppMessages.GPID, gamePlayer.getId()), HttpStatus.CREATED);
            // and then sends a Created response, with json containing the new gamePlayer id, e.g, {"gpid": 32}
        }
    }

    @PostMapping("/game/{gameId}/players") // adding the ability to create a game
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {
        if (isGuest(authentication)) { // is a guest
            return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR,AppMessages.NO_ACCESS), HttpStatus.UNAUTHORIZED);
            // gets the current user with the authentication object, and if there is none, it is sent an Unauthorized response
        } else { // if it isn't a guest
            Optional<Game> game = gameRepository.findById(gameId); // gets the game with that ID. An can be optional, can be null or non-null
            if (game.isEmpty()) { // if there is none, it sends a forbidden response with descriptive text, such as "No such game"
                return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.NOT_FOUND), HttpStatus.NOT_FOUND);
            } else if (game.get().getPlayers().size() == 2) { // And if it is exists, checks that the game has only one player
                return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.GAME_FULL), HttpStatus.FORBIDDEN);
            } else { // On the contrary, game has only one player
                Player player = playerRepository.findByUserName(authentication.getName());
                if (game.get().getPlayers().stream().findAny().get().getId() == player.getId()) {
                    return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.NOT_ALLOWED), HttpStatus.FORBIDDEN);
                }else{
                    // creates and saves a new game player, with this game and the current user
                   GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(player, game.get(), LocalDateTime.now()));
                    return new ResponseEntity<>(makeMap(AppMessages.GPID, gamePlayer.getId()), HttpStatus.CREATED);
                }
            }
        }
    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> placingShips(@PathVariable Long gamePlayerId, @RequestBody List<Ship>ships, Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.NO_ACCESS), HttpStatus.UNAUTHORIZED);
        }else{
            Optional<GamePlayer> gamePlayerOptional = gamePlayerRepository.findById(gamePlayerId);
            if(gamePlayerOptional.isEmpty()){
                return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.NOT_FOUND), HttpStatus.NOT_FOUND);
            }else{
                Player player = playerRepository.findByUserName(authentication.getName());
                // a gamePlayer variable is declared to get rid of the .get()
                GamePlayer gamePlayer = gamePlayerOptional.get();

                if(gamePlayer.getPlayer().getId() != player.getId()){
                    // and if the gamePlayer doesn't match with the current user forbidden response is sent
                    return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.NOT_ALLOWED), HttpStatus.FORBIDDEN);
                }else if (gamePlayer.getShips().size() > 0){
                    return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.SHIPS_PLACED), HttpStatus.FORBIDDEN);
                }else if(ships.size() != 5) {
                    return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.ADD_SHIPS), HttpStatus.FORBIDDEN);
                }else{
                    ships.forEach((ship)-> {
                        gamePlayer.addShip(ship);
                        gamePlayerRepository.save(gamePlayer);
                    });

                    //Otherwise, the ships should be added to the game player and saved, and a Created response should be sent.
                    return new ResponseEntity<>(makeMap(AppMessages.GPID, AppMessages.SHIPS_ADDED), HttpStatus.CREATED);
                }
            }
        }
    }

    @PostMapping("/games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String, Object>> firingSalvoes(Authentication authentication, @PathVariable Long gamePlayerId, @RequestBody Salvo salvo) {
        if (isGuest(authentication)) { // there is no current user logged in
            return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.NO_ACCESS), HttpStatus.UNAUTHORIZED);
        }else{
            Optional<GamePlayer> gamePlayerOptional = gamePlayerRepository.findById(gamePlayerId);
            if(gamePlayerOptional.isEmpty()) { // there is no game player with the given ID
                return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.NOT_FOUND), HttpStatus.NOT_FOUND);
            }else {
                Player player = playerRepository.findByUserName(authentication.getName());
                // a gamePlayer variable is declared to get rid of the .get()
                GamePlayer gamePlayer = gamePlayerOptional.get();
                Optional<GamePlayer> gamePlayerOptional2 = gamePlayer.getGame().getGamePlayers().stream().filter(gamePlayer1 -> gamePlayer1.getId() != gamePlayer.getId()).findFirst();

                if (gamePlayer.getPlayer().getId() != player.getId()) { // the current user isn't the gamePlayer the ID references
                    return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.NOT_ALLOWED), HttpStatus.FORBIDDEN);
                } else if (salvo.getLocations().size() != 5) {  // salvo has Turn, Player and Location
                    return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.ADD_SHIPS), HttpStatus.FORBIDDEN);
                } else if (salvo.getTurn() - 1 != gamePlayer.getSalvos().size()) {
                    // e.g, if turn is = 1 and size is = 1, turn 1 -1 is 0, then 0 != 1 it's true, user hasn´t submitted a salvo in the same turn
                    // otherwise, if turn is 2 and size = 1, turn 2 -1 is 1, then 1 != 1 it's false
                    // it's only true when salvo.getTurn & gameplayer,getSalvos.size have the same numbers
                    return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.NOT_VALID_TURN), HttpStatus.FORBIDDEN);
                    // A Forbidden response should be sent if the user already has submitted a salvo for the turn listed.
                } else if (gamePlayerOptional2.isPresent() && salvo.getTurn() - 1 > gamePlayerOptional2.get().getSalvos().size()) {
                    return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.WAIT_ENEMY), HttpStatus.FORBIDDEN);
                } else if (gamePlayerOptional2.isEmpty() && salvo.getTurn() == 1) {
                    return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.WAIT_ENEMY), HttpStatus.FORBIDDEN);
                } else if (gamePlayer.gameStateManagement() != GameStatus.FIRE) {
                    return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.CANT_FIRE), HttpStatus.FORBIDDEN);
                } else {
                    salvo.setGamePlayer(gamePlayer);
                    gamePlayer.addSalvo(salvo);
                    salvoRepository.save(salvo);

                if(gamePlayer.gameStateManagement() == GameStatus.WIN){
                    scoreRepository.save(new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), 1, LocalDateTime.now()));
                    scoreRepository.save(new Score(gamePlayer.getEnemy().get().getGame(), gamePlayer.getEnemy().get().getPlayer(), 0 , LocalDateTime.now()));
                }else if (gamePlayer.gameStateManagement() == GameStatus.LOST) {
                    scoreRepository.save(new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), 0, LocalDateTime.now()));
                    scoreRepository.save(new Score(gamePlayer.getEnemy().get().getGame(), gamePlayer.getEnemy().get().getPlayer(), 1 , LocalDateTime.now()));
                }else if (gamePlayer.gameStateManagement() == GameStatus.TIE) {
                    scoreRepository.save(new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), 0.5, LocalDateTime.now()));
                    scoreRepository.save(new Score(gamePlayer.getEnemy().get().getGame(), gamePlayer.getEnemy().get().getPlayer(), 0.5, LocalDateTime.now()));
                }

                    return new ResponseEntity<>(makeMap(AppMessages.KEY_SUCCESS, AppMessages.SALVOES_ADDED), HttpStatus.CREATED);
                    //Otherwise, the ships should be added to the game player and saved, and a Created response should be sent.
                }
            }
        }
    }

    @RequestMapping("/games")
    private Map<String,Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if(isGuest(authentication)){
            dto.put("player", null);
        }
        else{
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", makePlayerDTO(player));
        }
        dto.put("games", gameRepository.findAll().stream().map(game -> makeGameDTO(game)).collect(Collectors.toList()));
        return dto;
   }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable Long gamePlayerId, Authentication authentication) {
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).get();
        if (gamePlayer.getPlayer().getId() == player.getId()) {
            return new ResponseEntity<>(toGameViewDTO(gamePlayerRepository.findById(gamePlayerId).get()), HttpStatus.ACCEPTED);
        }
        else {
            return new ResponseEntity<>(makeMap(AppMessages.KEY_ERROR, AppMessages.NO_ACCESS), HttpStatus.UNAUTHORIZED);
        }
     }


    /** Data Transfer Objects **/

    private Map<String, Object> toGameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("date", gamePlayer.getDate());
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers().stream().map(gamePlayer1 -> makeGamePlayerDTO(gamePlayer1)).collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips().stream().map(ship -> makeShipDTO(ship)).collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(gamePlayer1 -> gamePlayer1.getSalvos().stream()).map(salvo -> makeSalvoDTO(salvo)).collect(Collectors.toList()));
        dto.put("hits", gamePlayer.getSalvos().stream().map(Salvo::hitsDTO));
        dto.put("sinks", gamePlayer.getSalvos().stream().map(Salvo::sunkenDTO));

        Optional<GamePlayer> enemy = gamePlayer.getEnemy();

        if(enemy.isPresent()) {
            dto.put("enemyHits", enemy.get().getSalvos().stream().map(Salvo::hitsDTO));
            dto.put("enemySunken", enemy.get().getSalvos().stream().map(Salvo::salvoSunkenDTO));
        } else {
            dto.put("enemyHits", new ArrayList<>());
            dto.put("enemySunken", new ArrayList<>());
        }
        dto.put("gameStatus", gamePlayer.gameStateManagement());
        return dto;
    }

    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        dto.put("id", game.getId());
        dto.put("date", game.getDate());
        dto.put("players", game.getGamePlayers().stream().map(gamePlayer -> makeGamePlayerDTO(gamePlayer)).collect(Collectors.toList()));
        return dto;
    }

    private Object makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gpid", gamePlayer.getId());
        dto.put("id", gamePlayer.getPlayer().getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        dto.put("score", gamePlayer.getScore() != null ? gamePlayer.getScore().getScore():null);
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

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

}
