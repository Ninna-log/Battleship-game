var appVue = new Vue({
    el: '#app',
    data: {
        data: {},
        games: {},
        authentication: null,
        player: [],
        players: {
            email: [],
            total: [],
            won: [],
            lost: [],
            tied: [],
        },
        username: "",
        password: "",
    },
    methods: {
        login: function () {
            if (appVue.username.length != 0 && appVue.password.length != 0) {
                $.post("/api/login", { username: appVue.username, password: appVue.password })
                    .done(function () {
                        alert("You're successfully logged in!")
                        location.reload()
                    })
                    .fail(function () {
                        alert("Incorrect data")
                        location.reload()
                    })
            } else {
                alert("Missing data")
            }
        },
        logout: function () {
            $.post("/api/logout")
                .done(function () {
                    alert("You're successfully logged out")
                    location.reload()
                })
        },
        register: function () {
            if (appVue.username.length != 0 && appVue.password.length != 0) {
                $.post("/api/players", { username: appVue.username, password: appVue.password })
                    .done(function () {
                        alert("Your user was created!")
                        location.reload()
                    })
                    .fail(function () {
                        alert("Incorrect data")
                        location.reload()
                    })
            } else {
                alert("Missing data")
            }
        },
        playersList: function () {
            // pushing all the players to appVue.player
            for (i = 0; i < appVue.games.length; i++) {
                for (x = 0; x < appVue.games[i].gamePlayers.length; x++) {
                    appVue.player.push(appVue.games[i].gamePlayers[x].player.email);
                }
            }
            // checking if a player is repeated and pushing them to a new array appVue.players
            for (var j = 0; j < appVue.player.length; j++) {
                for (var z = j + 1; z < appVue.player.length; z++) {
                    if (appVue.player[j] == appVue.player[z]) {
                        appVue.players.email.push(appVue.player[j]);
                    }
                }
            }
        },
        totalScores: function () {
            for (var y = 0; y < appVue.players.email.length; y++) {
                var totalScores = 0;
                for (var i = 0; i < appVue.games.length; i++) {
                    for (var x = 0; x < appVue.games[i].gamePlayers.length; x++) {
                        if (appVue.players.email[y] == appVue.games[i].gamePlayers[x].player.email) {
                            totalScores += appVue.games[i].gamePlayers[x].score;
                        }
                    }

                }
                appVue.players.total.push(totalScores);
            }
        },
        wonScores: function () {
            for (var y = 0; y < appVue.players.email.length; y++) {
                var wonScores = 0;
                for (var i = 0; i < appVue.games.length; i++) {
                    for (var x = 0; x < appVue.games[i].gamePlayers.length; x++) {
                        if (appVue.players.email[y] == appVue.games[i].gamePlayers[x].player.email) {
                            if (appVue.games[i].gamePlayers[x].score == 1) {
                                wonScores++;
                            }
                        }
                    }
                }
                appVue.players.won.push(wonScores);
            }
        },
        lostScores: function () {
            for (var y = 0; y < appVue.players.email.length; y++) {
                var lostScores = 0;
                for (var i = 0; i < appVue.games.length; i++) {
                    for (var x = 0; x < appVue.games[i].gamePlayers.length; x++) {
                        if (appVue.players.email[y] == appVue.games[i].gamePlayers[x].player.email) {
                            if (appVue.games[i].gamePlayers[x].score == 0) {
                                lostScores++;
                            }
                        }
                    }
                }
                appVue.players.lost.push(lostScores);
            }
        },
        tiedScores: function () {
            for (var y = 0; y < appVue.players.email.length; y++) {
                var tiedScores = 0;
                for (var i = 0; i < appVue.games.length; i++) {
                    for (var x = 0; x < appVue.games[i].gamePlayers.length; x++) {
                        if (appVue.players.email[y] == appVue.games[i].gamePlayers[x].player.email) {
                            if (appVue.games[i].gamePlayers[x].score === 0.5) {
                                tiedScores++;
                            }
                        }
                    }
                }
                appVue.players.tied.push(tiedScores);
            }
        }
    }
});

fetch('/api/games')
    .then(function (res) {
        return res.json();
    })
    .then(function (json) {
        appVue.games = json.games;
        appVue.authentication = json.player;
        appVue.playersList();
        appVue.totalScores();
        appVue.wonScores();
        appVue.lostScores();
        appVue.tiedScores();
    })





