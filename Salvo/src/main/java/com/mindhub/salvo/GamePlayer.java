package com.mindhub.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
public class GamePlayer {

    @Id // every @Entity/object from the class GamePlayer which is an arrow --->
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native") // will be identified by a unique ID
    @GenericGenerator(name = "native", strategy = "native") // generated by Spring
    private long id;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER) // relation of Many(GamePlayer) to Many(Player)
    @JoinColumn(name="player_id") // Player is identified by a unique player ID in a column in the GamePlayerRepository
    private Player player;                      // GamePlayer class will be receiving two objects: Player and Game

    @ManyToOne(fetch = FetchType.EAGER) // relation of Many(GamePlayer) to Many(Game)
    @JoinColumn(name="game_id") // Game is identified by a unique game ID in a column in the GamePlayerRepository
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL) // relation que hay con Ship a través de la propiedad "gamePlayer"
    private Set<Ship> ships = new HashSet<>();

    public GamePlayer() { } // empty constructor

    public GamePlayer(Player player, Game game, LocalDateTime date) {
        this.player = player;    // Non-empty constructor which will be receiving when the GamePlayer object is created
        this.game = game;          // a Player, a Game, a Date and a Ship
        this.date = date;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public long getId() { // An ID is received

        return id;
    }

    public void setId(long id) { // An ID is modified

        this.id = id;
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

}