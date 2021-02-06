var appVue = new Vue({
    el: '#table',
    data: {
        columns: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        salvo: ['S'],
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        gameView: null,
        viewer: null,
        enemy: null,
        authentication: null,
        showShipControls: true,
        ships: [],
        shipSelected: "",
        position: "",
        row: null,
        col: null,
        cells: 0
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
                    alert("Ships added ");
                    location.reload()
                })
                .fail(function (error) {
                    alert(JSON.parse(error.responseText).error);
                })
        },
        newShip: function (row, col) {
            if (appVue.shipSelected == "" || appVue.position == "") {  // if the type of ship or position hasn´t been selected
                alert("Please select ship and position");
            }
            else {    // if ship and position has already been selected
                appVue.row = row;
                appVue.col = col;
                // then verifies that the ship selected isn't the same that previously was chosen
                if ((appVue.ships.findIndex(ship => ship.type === appVue.shipSelected) != -1)) {
                    appVue.eraseShips();
                    appVue.drawShips();
                }
                else {
                    appVue.drawShips();  // otherwise, if findIndex() returns -1, the ship wasn't found
                }
            }
        },
        shipsTypes: function () {
            switch (appVue.shipSelected){
            case "Carrier":
                appVue.cells = 5;
                break;
            case "Battleship":
                appVue.cells = 4;
                break;
            case "submarine":
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
            appVue.shipsTypes();
            if (appVue.position == "horizontal") { // if it's horizontal
                // if the ship selected is found in ship.type then every index location of that ship
                var loc = appVue.ships.findIndex(navecita => navecita.type === appVue.shipSelected); // is looped through
                for (var i = 0; i < appVue.ships[loc].locations.length; i++) {
                    document.getElementById(appVue.ships[loc].locations[i]).classList.remove('shipColor'); // in order to remove that color class
                }
                appVue.ships.splice(loc); // eliminates all the elements given by loc which is where findIndex() found the same ship
            }
            if (appVue.position == "vertical") { // if it's vertical
                var loc = appVue.ships.findIndex(ship => ship.type === appVue.shipSelected);
                for (var i = 0; i < appVue.ships[loc].locations.length; i++) {
                    document.getElementById(appVue.ships[loc].locations[i]).classList.remove('shipColor');
                }
                appVue.ships.splice(loc);
            }
        },
        drawShips: function () {
            appVue.shipsTypes();
            if (appVue.position == "horizontal") {
                var ship = { "type": appVue.shipSelected, "locations": [] };
                if (appVue.col + appVue.cells <= 11) {     // sees if column chosen plus number of the ship's length doesn't exceeds number of cells
                    for (var i = 0; i < appVue.cells; i++) {  // if doesn't exceeds makes a loop through the ship's length
                        ship.locations.push(appVue.row + (appVue.col + i)); // in order to to add 1 to column and then concatenate row with column
                    }     // then it checks if there's another ship on the same location
                    var index = appVue.ships.findIndex(shipp => shipp.locations.findIndex(loc => ship.locations.includes(loc)) >= 0)
                    console.log(index);    // findIndex() returns 0 if the element was found, and on the contrary returns -1
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
                    console.log(index);
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
        appVue.drawingShips();
        appVue.drawingSalvoes();
        appVue.players();
        appVue.shipControls();
    })

