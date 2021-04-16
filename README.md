# Battleship-game
Web-based multi-player game application

In this proyect I visualized myself as a developer who's been contacted by a board game company looking to use their brand recognition to market online games with a retro touch, asking for a multi-player online version of a Salvo-like game engine. Taking into account that Salvo was a pencil and paper game that was the basis for the popular Battleship game. The basic idea involves guessing where other players have hidden objects. 

I created a front-end web application that game players interact with, and a back-end game server to manage the games, scoring, and player profiles. Using JavaScript and Vue.js for the front-end client, and the Spring Boot framework for the Java-based RESTful web server.

📌 The core architecture is divided as follows:

- A small Java back-end server that stores Salvo game data, and then sends that data to client apps via a RESTful API.
- A front-end browser-based game interface that graphically shows players the state of the game, including ships they've placed, damage sustained, and scores.

🕹 The game play business' logic is as follows:

- Players can create new games and join games that others have created.
- When a game has both players, players can place their ships on their grids.
- When ships have been placed, players can begin trading salvos (shots) and seeing the results (hits, sinks, and misses).
- When all of a player's ships have been sunk, the game ends and the winner is added to the leaderboard.

🔧 Technologies used:

- Project developed on Java.
- Spring 2.1.4
- Gradle 7.0
- H2 as a Database and Java Persistence Api (JPA)
- MySQL
- Front-end developed on HTML5, CSS3, Bootstrap v4.1.3, jQuery, JavaScript and Vue.js.

❗ Important subjects to take into account in order to compile the project:

- Download and install JAVA 15
- Download and install Java SE [JDK]
- Download and install H2
- Download and install Gradle 7.0
- Run on console, from the root: 
  1.gradle wrapper
  2. gradle bootRun

If everything was successfully installed and compiled you'll be able to access the project from:

🌐 http://localhost:8080/web/games.html

Or you can also access to the project on Heroku:

🌐 https://ninna-battleship.herokuapp.com/web/games.html


👀<strong>Interfaces Salvo Battleship<strong>
  
<img align="center" src="https://github.com/Ninna-log/Battleship-game/blob/master/img/interface1.jpg" alt="interface1" height="400" width="1800" />
<br>
<br>

<img align="center" src="https://github.com/Ninna-log/Battleship-game/blob/master/img/interface2.jpg" alt="interface2" height="400" width="1800" />
<br>
<br>

<img align="center" src="https://github.com/Ninna-log/Battleship-game/blob/master/img/interface3.jpg" alt="interface2" height="400" width="1800" />

