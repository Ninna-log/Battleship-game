package com.mindhub.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class GamePlayer {

    @Id // every @Entity/object from the class GamePlayer which is an arrow --->
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native") // will be identified by a unique ID
    @GenericGenerator(name = "native", strategy = "native") // generated by Spring
    private long id;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER) // relation of Many(GamePlayer) to Many(Player)
    @JoinColumn(name = "player_id")
    // Player is identified by a unique player ID in a column in the GamePlayerRepository
    private Player player;                      // GamePlayer class will be receiving two objects: Player and Game

    @ManyToOne(fetch = FetchType.EAGER) // relation of Many(GamePlayer) to Many(Game)
    @JoinColumn(name = "game_id") // Game is identified by a unique game ID in a column in the GamePlayerRepository
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    // relation que hay con Ship a través de la propiedad "gamePlayer"
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    // relation que hay con Salvo a través de la propiedad "gamePlayer"
    private Set<Salvo> salvos = new HashSet<>();

    public GamePlayer() {
    } // empty constructor

    public GamePlayer(Player player, Game game, LocalDateTime date) {
        this.player = player;    // Non-empty constructor which will be receiving when the GamePlayer object is created
        this.game = game;          // a Player, a Game, a Date, a Ship and a score
        this.date = date;
    }

    public Score getScore() {
        return player.getScore(game);
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public long getId() { // An ID is received

        return id;
    }

    public Player getPlayer() { // A Player is received

        return player;
    }

    public void setPlayer(Player player) { // A Player is modified

        this.player = player;
    }

    public Game getGame() {  // A Game is received

        return game;
    }

    public void setGame(Game game) { // A Game is modified

        this.game = game;
    }

    public LocalDateTime getDate() { // A Date is received

        return date;
    }

    public void setDate(LocalDateTime date) { // A Date is modified

        this.date = date;
    }

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvos.add(salvo);
    }

    public Optional<GamePlayer> getEnemy() {
        return this.game.getGamePlayers().stream().filter(gp -> gp.getId() != this.id).findFirst();
        // returns the gamePlayer which is different from the gamePlayer viewer
    }


    public GameStatus gameStateManagement() {

        Optional<GamePlayer> enemy = this.getEnemy();
        if (this.getShips().isEmpty()) { // if viewer hasn't placed any ship and enemy exists
            return GameStatus.PLACE_SHIPS;
        } else if (enemy.isEmpty()) { // if enemy doesn't exists yet
            return GameStatus.WAIT_FOR_ENEMY;
        } else {  // salvos porque se quiere recorrer el arreglo de salvos, y getSalvos() porque se quiere obtener el size directamente
            int turnViewer = 0;
            int sunksViewer = 0;    // if none of the previous scenarios fit with the case, then the case is validated in other scenarios
            int turnEnemy = 0;
            int sunksEnemy = 0;
            Optional<Salvo> gpTurn = this.salvos.stream().filter(salvo -> salvo.getTurn() == this.getSalvos().size()).findFirst(); // last turn played
            Optional<Salvo> enemyTurn = enemy.get().salvos.stream().filter(salvo -> salvo.getTurn() == enemy.get().getSalvos().size()).findFirst();

            // before validating other scenarios, variables must be validated in order to see if they exist
            if (gpTurn.isPresent()) { // vieweeerrr
                turnViewer = gpTurn.get().getTurn(); // gets last turn played by the viewer
                sunksViewer = gpTurn.get().getSunkenShips().size(); // gets sunken ships from the last turn
            }
            if (enemyTurn.isPresent()) { // enemy
                turnEnemy = enemyTurn.get().getTurn(); // gets last turn played by the enemy
                sunksEnemy = enemyTurn.get().getSunkenShips().size(); // gets sunken ships from the last turn
            }

            // States of the game when has already begun
            if (turnViewer < turnEnemy) {
                return GameStatus.FIRE;
            } else if (turnViewer > turnEnemy) {
                return GameStatus.WAIT;
            }else { // When turnViewer == turnEnemy starts to validate other scenarios
                if (sunksViewer < 5 && sunksEnemy == 5) {
                    return GameStatus.LOST;
                } else if (sunksViewer == 5 && sunksEnemy < 5) {
                    return GameStatus.WON;
                } else if (sunksViewer == 5 && sunksEnemy == 5) {
                    return GameStatus.TIE;
                }else {
                    if (this.id < enemy.get().getId()){
                        return GameStatus.FIRE;
                    }else {
                        return GameStatus.WAIT;
                    }
                }
            }
        }
    }
}

