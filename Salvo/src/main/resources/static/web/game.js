var appVue = new Vue({
    el: '#table',
    data: {
        columns: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        salvo: ['S'],
        salvoesViewer: ["s"],
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        gameView: null,
        viewer: null,
        enemy: null,
        authentication: null,
        showShipControls: true,
        ships: [],
        salvoes: { "turn": 0, "locations": [] },
        shipSelected: "",
        position: "",
        row: null,
        col: null,
        cells: 0,
        rowss: null,
        cols: null
    },
    methods: {
        logout: function () {
            $.post("/api/logout")
                .done(function () {
                    alert("You're successfully logged out")
                    window.open("/web/games.html")
                })
                .fail(function () {
                    alert("There's been an error. Please, try again")
                })
        },
        goBack: function () {
            window.open("/web/games.html")
        },
        addingShips: function (gpid) {
            $.post({
                url: "/api/games/players/" + gpid + "/ships",
                data: JSON.stringify(appVue.ships),
                dataType: "text",
                contentType: "application/json",
            })
                .done(function (response) {
                    alert("Ships added");
                    location.reload()
                })
                .fail(function (error) {
                    alert(JSON.parse(error.responseText).error);
                })
        },
        addingSalvoes: function (gpid) {
            appVue.turn();
            $.post({
                url: "/api/games/players/" + gpid + "/salvos",
                data: JSON.stringify(appVue.salvoes),
                dataType: "text",
                contentType: "application/json",
            })
                .done(function (response) {
                    alert('Salvoes added');
                    location.reload();
                })
                .fail(function (error) {
                    alert(JSON.parse(error.responseText).error);
                    location.reload();
                })
        },
        newSalvoes: function (row, column) {
            appVue.rowss = row;
            appVue.cols = column;

            var index = appVue.salvoes.locations.indexOf(row + column); // indexOf() checks if row+column exists in the array salvoes
                      // and, if exists, returns the index, if doesn't exist returns -1
            var playerSalvoes = appVue.gameView.salvoes.filter(salvo => salvo.player == appVue.viewer.id);
                     // filters all the salvoes that belong to the viewer
            if (index >= 0) {
                document.getElementById("s" + row + column).classList.remove('newViewerSalvo');
                appVue.salvoes.locations.splice(index, 1);
            }
            else if (playerSalvoes.findIndex(salvo => salvo.salvoLocations == row + column) != -1) {
                alert("You already have a shot there")
            }
            else if (appVue.salvoes.locations.length < 5) {
                document.getElementById('s' + row + column).classList.add('newViewerSalvo');
                appVue.salvoes.locations.push(row + column);
            }
            else {
                alert("You must fire only 5 shots")
            }
        },
        turn: function () {
            var playerSalvoes = appVue.gameView.salvoes.filter(salvo => salvo.player == appVue.viewer.id);
            appVue.salvoes.turn = playerSalvoes.length + 1;
        },
        drawingSalvoes: function () {
            var salvoes = [];
            for (var i = 0; i < appVue.gameView.salvoes.length; i++) {
                for (var t = 0; t < appVue.gameView.salvoes[i].location.length; t++) {
                    if (appVue.gameView.salvoes[i].player != appVue.viewer.id) {        //enemy
                        if (appVue.shipsHit(appVue.gameView.salvoes[i].location[t])) {
                            document.getElementById(appVue.gameView.salvoes[i].location[t]).classList.add('hits');
                            document.getElementById(appVue.gameView.salvoes[i].location[t]).innerHTML = appVue.gameView.salvoes[i].turn;
                        }
                        else {
                            document.getElementById(appVue.gameView.salvoes[i].location[t]).classList.add('enemySalvoColor');
                        }
                    }
                    else {
                        salvoes.push(appVue.gameView.salvoes[i]);   // viewer
                        document.getElementById('s' + appVue.gameView.salvoes[i].location[t]).classList.add('oldViewerSalvo');
                        document.getElementById("s" + appVue.gameView.salvoes[i].location[t]).innerHTML = appVue.gameView.salvoes[i].turn;
                    }
                }
            }
            var newSalvoes = salvoes.sort((a,b) => parseInt(b.turn) - parseInt(a.turn))[0];
            newSalvoes.location.forEach((salvo) => {
                          document.getElementById('s' + salvo).classList.add('newViewerSalvo');
            });
        },
        shipsHit: function (location) {
            var hit = false;
            for (var y = 0; y < appVue.gameView.ships.length; y++) {
                for (var q = 0; q < appVue.gameView.ships[y].location.length; q++) {
                    if (location == appVue.gameView.ships[y].location[q]) {
                        hit = true;
                    }
                }
            }
            return hit;
        },
        newShip: function (row, col) {
            if (appVue.shipSelected == "" || appVue.position == "") {  // if the type of ship or position hasn´t been selected
                alert("Please select ship and position");
            }
            else {    // if ship and position has already been selected
                appVue.row = row;
                appVue.col = col;

                appVue.shipsTypes();
                if (appVue.ships.findIndex(ship => ship.type === appVue.shipSelected) != -1) {
                    appVue.eraseShips();
                    appVue.drawShips();
                } else if (appVue.ships.findIndex(ship => ship.type === appVue.shipSelected) == -1) {
                    appVue.drawShips();  // otherwise, if findIndex() returns -1, the ship wasn't found
                }
            }
        },
        shipsTypes: function () {
            switch (appVue.shipSelected) {
                case "Carrier":
                    appVue.cells = 5;
                    break;
                case "Battleship":
                    appVue.cells = 4;
                    break;
                case "Submarine":
                    appVue.cells = 3;
                    break;
                case "Destroyer":
                    appVue.cells = 3;
                    break;
                case "Patroal Boat":
                    appVue.cells = 2;
                    break;
            }
        },
        eraseShips: function () {
            if (appVue.position == "horizontal" && appVue.col + appVue.cells <= 11) { // if it's horizontal
                // if the ship selected is found in ship.type then every index location of that ship
                var loc = appVue.ships.findIndex(navecita => navecita.type === appVue.shipSelected); // is looped through
                for (var i = 0; i < appVue.ships[loc].locations.length; i++) {
                    document.getElementById(appVue.ships[loc].locations[i]).classList.remove('shipColor'); // in order to remove that color class
                }
                appVue.ships.splice(loc, 1); // eliminates all the elements given by loc which is where findIndex() found the same ship
            }
            var index = appVue.rows.indexOf(appVue.row);
            if (appVue.position == "vertical" && ((index + 1) + appVue.cells <= 11)) { // if it's vertical
                var loc = appVue.ships.findIndex(ship => ship.type === appVue.shipSelected);
                for (var i = 0; i < appVue.ships[loc].locations.length; i++) {
                    document.getElementById(appVue.ships[loc].locations[i]).classList.remove('shipColor');
                }
                appVue.ships.splice(loc, 1);
            }
        },
        drawShips: function () {
            if (appVue.position == "horizontal") {
                var ship = { "type": appVue.shipSelected, "locations": [] };
                if (appVue.col + appVue.cells <= 11) {     // sees if column chosen plus number of the ship's length doesn't exceeds number of cells
                    for (var i = 0; i < appVue.cells; i++) {  // if doesn't exceeds makes a loop through the ship's length
                        ship.locations.push(appVue.row + (appVue.col + i)); // in order to to add 1 to column and then concatenate row with column
                    }     // then it checks if there's another ship on the same location
                    var index = appVue.ships.findIndex(shipp => shipp.locations.findIndex(loc => ship.locations.includes(loc)) >= 0)
                    // findIndex() returns 0 if the element was found, and on the contrary returns -1
                    if (index != -1) {
                        alert("Nope, my dude");
                    }
                    else {
                        for (var i = 0; i < ship.locations.length; i++) {
                            document.getElementById(ship.locations[i]).classList.add('shipColor');
                        }
                        appVue.ships.push(ship);
                    }
                }
                else {
                    alert("Do the math, ship doesn't fit")
                }
            }
            else {   // position it's vertical
                var ship = { "type": appVue.shipSelected, "locations": [] };
                var index = appVue.rows.indexOf(appVue.row);
                if ((index + 1) + appVue.cells <= 11) {
                    for (var i = 0; i < appVue.cells; i++) {
                        ship.locations.push((String.fromCharCode(appVue.row.charCodeAt(0) + i)) + appVue.col);
                    }                   // findIndex() returns 0 if the element was found, and on the contrary returns -1
                    var index = appVue.ships.findIndex(navecita => navecita.locations.findIndex(loc => ship.locations.includes(loc)) >= 0)
                    if (index != -1) { // if ship is on top of another ship
                        alert("Nope, my dude");
                    }
                    else {
                        for (var i = 0; i < ship.locations.length; i++) {
                            document.getElementById(ship.locations[i]).classList.add('shipColor');
                        }
                        appVue.ships.push(ship);
                    }
                }
                else {
                    alert("Do the math, ship doesn't fit")
                }
            }
        },
        drawingShips: function () {
            for (var i = 0; i < appVue.gameView.ships.length; i++) {
                for (var x = 0; x < appVue.gameView.ships[i].location.length; x++) {
                    document.getElementById(appVue.gameView.ships[i].location[x]).classList.add('shipColor');
                }
            }
        },
        players: function () {
            for (var j = 0; j < appVue.gameView.gamePlayers.length; j++) {
                if (appVue.gameView.gamePlayers[j].gpid == appVue.gameView.id) {
                    appVue.viewer = appVue.gameView.gamePlayers[j];
                } else {
                    appVue.enemy = appVue.gameView.gamePlayers[j];
                }
            }
        },
        shipControls: function () {
            appVue.showShipControls = appVue.gameView != null && appVue.gameView.ships.length != 5;
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
        appVue.players();
        appVue.drawingShips();
        appVue.drawingSalvoes();
        appVue.shipsHit();
        appVue.shipControls();
    })

