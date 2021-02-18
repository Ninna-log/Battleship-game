package com.mindhub.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Id;
import javax.persistence.FetchType;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turn;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    private List<String> locations;

    public Salvo() { }

    public Salvo(int turn, List<String> locations){
        this.turn = turn;
        this.locations = locations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<String> getHits() {  // viewer

        Optional<GamePlayer> enemy = this.gamePlayer.getEnemy(); // getEnemy() is defined in gamePlayer
        // Optional, can be present or non-present
        if(enemy.isPresent()){ // if enemy is present

            List<String> viewerLocations = this.locations; // viewer locations
            List<String> enemyLocations = new ArrayList<>();
            Set<Ship> enemyShips = enemy.get().getShips(); // enemy locations

            enemyShips.forEach(ship -> enemyLocations.addAll(ship.getLocations()));

            return viewerLocations.stream().filter(enemyLocations::contains)
                    .collect(Collectors.toList());
        }
        else{
            return new ArrayList<>();
        }
    }

    public List<Ship> getSunkenShips() {  // enemy
        Optional<GamePlayer> enemy = this.gamePlayer.getEnemy();

        if(enemy.isPresent()){
            List<String> allShots = new ArrayList<>();

            Set<Salvo> viewerSalvoes = this.gamePlayer.getSalvos().stream()
                    .filter(salvo -> salvo.getTurn() <= this.turn).collect(Collectors.toSet());

            Set<Ship> enemyShips = enemy.get().getShips();

            viewerSalvoes.forEach(salvo -> allShots.addAll(salvo.getLocations()));

            return enemyShips.stream().filter(ship -> allShots.containsAll(ship.getLocations()))
                    .collect(Collectors.toList());
        }
        else{
            return new ArrayList<>();
        }
    }

    public Map<String, Object> hitsDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turn);
        dto.put("hits", this.getHits());
        return dto;
    }

    public Map<String, Object> sunkenDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turn);
        dto.put("sunken", this.getSunkenShips().stream().map(Ship::shipDTO));
        return dto;
    }

    public Map<String, Object> salvoHitDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turn);
        dto.put("hits", this.getHits());
        return dto;
    }

    public Map<String, Object> salvoSunkenDTO() {

        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("turn", this.turn);
        dto.put("sunken", this.getSunkenShips().stream().map(Ship::shipDTO));
        return dto;
    }
}
