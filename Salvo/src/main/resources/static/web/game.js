var appVue = new Vue({
    el: '#table',
    data: {
        columns: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        salvo: ['S'],
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        gameView: null,
        viewer: null,
        enemy: null,
        authentication: null
    },
    methods: {
        logout: function () {
            $.post("/api/logout")
                .done(function () {
                    alert("You're successfully logged out")
                    window.open("/web/games.html")
                })
                .fail(function () {
                   alert("There's been an error. Please, try again.)
                })
        },
        goBack: function () {
            window.open("/web/games.html")
        },
        drawingShips: function () {
            for (var i = 0; i < appVue.gameView.ships.length; i++) {
                for (var x = 0; x < appVue.gameView.ships[i].location.length; x++) {
                    document.getElementById(appVue.gameView.ships[i].location[x]).classList.add('shipColor');
                }
            }
        },
        drawingSalvoes: function () {
            for (var i = 0; i < appVue.gameView.salvoes.length; i++) {
                if (appVue.gameView.salvoes[i].player == appVue.gameView.id) {
                    for (var x = 0; x < appVue.gameView.salvoes[i].location.length; x++) {
                        document.getElementById("S" + appVue.gameView.salvoes[i].location[x]).classList.add('viewerSalvoColor');
                        document.getElementById("S" + appVue.gameView.salvoes[i].location[x]).innerHTML = appVue.gameView.salvoes[i].turn;
                    }
                } else {
                    for (var x = 0; x < appVue.gameView.salvoes[i].location.length; x++) {
                        for (var y = 0; y < appVue.gameView.ships.length; y++) {
                            for (var z = 0; z < appVue.gameView.ships[y].location.length; z++) {
                                if (appVue.gameView.ships[y].location[z] == appVue.gameView.salvoes[i].location[x]) {
                                    document.getElementById(appVue.gameView.salvoes[i].location[x]).classList.add('hits');
                                }
                                else {
                                    document.getElementById(appVue.gameView.salvoes[i].location[x]).classList.add('enemySalvoColor');
                                }
                            }
                        }
                        document.getElementById(appVue.gameView.salvoes[i].location[x]).innerHTML = appVue.gameView.salvoes[i].turn;
                    }
                }
            }
        },
        players: function () {
            for (var j = 0; j < appVue.gameView.gamePlayers.length; j++) {
                if (appVue.gameView.gamePlayers[j].gpid == appVue.gameView.id) {
                    appVue.viewer = appVue.gameView.gamePlayers[j].player;
                } else {
                    appVue.enemy = appVue.gameView.gamePlayers[j].player;
                }
            }
        }
    }
});

const urlParams = new URLSearchParams(window.location.search);
const gp = urlParams.get('gp');

fetch('/api/game_view/' + gp)
    .then(function (res) {
        if (res.ok) {
            return res.json();
        }
        else {
            throw new error(res.status)
        }
    })
    .then(function (json) {
        appVue.gameView = json;
        appVue.authentication = json.player;
        appVue.drawingShips();
        appVue.drawingSalvoes();
        appVue.players();
    })

