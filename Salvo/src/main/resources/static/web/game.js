var appVue = new Vue({
  el: '#table',
  data: {
    columns: [1,2,3,4,5,6,7,8,9,10],
    rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
    gameView: null,
  },
  methods: {
    drawingShips: function(){
        for(var i=0; i < appVue.gameView.ships.length; i++){
            for(var x=0; x < appVue.gameView.ships[i].location.length; x++){
                document.getElementById(appVue.gameView.ships[i].location[x]).classList.add('shipColor');
            }
        }
    }
  }
});

const urlParams = new URLSearchParams(window.location.search);
const gp = urlParams.get('gp');

fetch('/api/game_view/' + gp)
    .then(function (res) {
      return res.json();
    })
    .then(function(json){
      appVue.gameView = json;
      appVue.drawingShips();
    })


