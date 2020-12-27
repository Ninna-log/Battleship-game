package com.mindhub.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository) {
		return (args) -> {

			Player player1 = new Player("j.bauer@ctu.gov");
			Player player2 = new Player("c.obrian@ctu.gov");
			Player player3 = new Player("kim_bauer@gmail.com");
			Player player4 = new Player("t.almeida@ctu.gov");
			// saving a couple of players
			playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);

			Game game1 = new Game(LocalDateTime.now());
			Game game2 = new Game(LocalDateTime.now().plusHours(1));
			Game game3 = new Game(LocalDateTime.now().plusHours(2));
            Game game4 = new Game(LocalDateTime.now().plusHours(2));
			// saving a couple of games
            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);
            gameRepository.save(game4);

            // associating everything that has to be with game and player with gamePlayer @Many-to-many
			GamePlayer gamePlayer1 = new GamePlayer(player1, game1, LocalDateTime.now());
			GamePlayer gamePlayer2 = new GamePlayer(player2, game1, LocalDateTime.now());
			GamePlayer gamePlayer3 = new GamePlayer(player3, game2, LocalDateTime.now());
			GamePlayer gamePlayer4 = new GamePlayer(player4, game2, LocalDateTime.now());
			GamePlayer gamePlayer5 = new GamePlayer(player4, game2, LocalDateTime.now());

			// creating locations arrays
			List<String> locations1 = new ArrayList<>(Arrays.asList("B5", "B6", "B7", "B8", "B9"));
			List<String> locations2 = new ArrayList<>(Arrays.asList("C5","C6", "C7", "C8", "C9"));
			List<String> locations3 = new ArrayList<>(Arrays.asList("D5","D6", "D7", "D8", "D9"));
			List<String> locations4 = new ArrayList<>(Arrays.asList("G5","G6", "G7", "G8", "G9"));
			List<String> locations5 = new ArrayList<>(Arrays.asList("H5","H6", "H7", "H8", "H9"));
			List<String> locations6 = new ArrayList<>(Arrays.asList("F3","G3", "H3"));
			List<String> locations7 = new ArrayList<>(Arrays.asList("F4","G4", "H4"));
			List<String> locations8 = new ArrayList<>(Arrays.asList("F5","G5", "H5"));

			// creating ships with their locations as arrays
			Ship ship1 = new Ship("Carrier",locations1);
			Ship ship2 = new Ship("Battleship", locations2);
			Ship ship3 = new Ship("Submarine", locations3);
			Ship ship4 = new Ship("Destroyer", locations4);
			Ship ship5 = new Ship("Patrol Boat", locations5);
			Ship ship6 = new Ship("Submarine", locations6);
			Ship ship7 = new Ship("Destroyer", locations7);
			Ship ship8 = new Ship("Patrol Boat", locations8);

			//adding ships to every gamePlayer
			gamePlayer1.addShip(ship1);
			gamePlayer1.addShip(ship2);
			gamePlayer2.addShip(ship3);
			gamePlayer2.addShip(ship4);
			gamePlayer3.addShip(ship5);
			gamePlayer3.addShip(ship6);
			gamePlayer4.addShip(ship7);
			gamePlayer5.addShip(ship8);

			//saving a couple of gamePlayers
			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);
            gamePlayerRepository.save(gamePlayer4);
			gamePlayerRepository.save(gamePlayer5);

		};
	}

}
