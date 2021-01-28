var appVue = new Vue({
    el: '#app',
    data: {
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
        gpid: null,
        username: "",
        password: "",
    },
    filters: {
        dateFormatted: function (value) {
            if (!value) return ''
            return moment(value).format('DD/MM/YYYY, h:mm A')
        }
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
                var emailValidation = /^[a-zA-Z0-9.!#$%&*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
                if (emailValidation.test(appVue.username) != true) {
                    alert("Please enter a valid email")
                    appVue.password = ""
                } else {
                    $.post("/api/players", { username: appVue.username, password: appVue.password })
                        .done(function () {
                            alert("Your user was created!")
                            appVue.login(appVue.username)
                        })
                        .fail(function () {
                            alert("User already in use")
                            appVue.username = ""
                            appVue.password = ""
                        })
                }
            } else {
                alert("Missing data")
            }
        },
        createGame: function () {
            // creating a game if the user is logged in
            $.post("/api/games")
                .done(function (data) {
                    appVue.gpid = data.gpid;
                    console.log(data)
                    alert("The game was successfully created!")
                    window.open("/web/game.html?gp=" + appVue.gpid)
                })
        },
        joinGame: function (game) {
            var id = game.getAttribute("data-game");
            $.post("/api/game/"+ id +"/players")/api/game/1/players
                .done(function (data) {
                    appVue.gpid = data.gpid;
                    console.log(data)
                    window.open("/web/game.html?gp=" + appVue.gpid)
                })
        },
        logout: function () {
            $.post("/api/logout")
                .done(function () {
                    alert("You're successfully logged out")
                    location.reload()
                })
        },
        playersList: function () {
            // pushing all the players to appVue.player
            for (i = 0; i < appVue.games.length; i++) {
                for (x = 0; x < appVue.games[i].players.length; x++) {
                    appVue.player.push(appVue.games[i].players[x].player.email);
                }
            }
            // checking if a player is repeated and then pushing them to a new array appVue.players
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
                    for (var x = 0; x < appVue.games[i].players.length; x++) {
                        if (appVue.players.email[y] == appVue.games[i].players[x].player.email) {
                            totalScores += appVue.games[i].players[x].score;
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
                    for (var x = 0; x < appVue.games[i].players.length; x++) {
                        if (appVue.players.email[y] == appVue.games[i].players[x].player.email) {
                            if (appVue.games[i].players[x].score == 1) {
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
                    for (var x = 0; x < appVue.games[i].players.length; x++) {
                        if (appVue.players.email[y] == appVue.games[i].players[x].player.email) {
                            if (appVue.games[i].players[x].score == 0) {
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
                    for (var x = 0; x < appVue.games[i].players.length; x++) {
                        if (appVue.players.email[y] == appVue.games[i].players[x].player.email) {
                            if (appVue.games[i].players[x].score === 0.5) {
                                tiedScores++;
                            }
                        }
                    }
                }
                appVue.players.tied.push(tiedScores);
            }
        },
    }
});

fetch('/api/games')
    .then(function (res) {
        if (res.ok) {
            return res.json();
        }
        else {
            throw new error(res.status)
        }
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





