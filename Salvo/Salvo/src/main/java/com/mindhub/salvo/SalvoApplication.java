package com.mindhub.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalDateTime;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository) {
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
			// saving a couple of games
            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);

            // associating everything that has to be with game and player with gamePlayer
			gamePlayerRepository.save(new GamePlayer(player1, game1, LocalDateTime.now()));
			gamePlayerRepository.save(new GamePlayer(player2, game2, LocalDateTime.now()));
			gamePlayerRepository.save(new GamePlayer(player3, game3, LocalDateTime.now()));
		};
	}

}
