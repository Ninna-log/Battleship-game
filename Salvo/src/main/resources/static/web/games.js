var appVue = new Vue({
  el: '#app',
  data: {
    games: {},
  }
})

fetch('/api/games')
    .then(function (res) {
      return res.json();
    })
    .then(function(json){
      appVue.games = json;
    })

