/**
 * Created by luanne on 29/06/14.
 */

(function () {
    var app = angular.module('flavors', ['ui.bootstrap']);

app.factory("SearchService", ['$rootScope','$http',function($rootScope,$http) {
  var currentIngredient=undefined;
  var trios = [];

  return {
    getIngredient: function() {
      return currentIngredient;
    },
    setIngredient : function(ing) {
        currentIngredient = ing;
        flavorTreeSearch(ing);
        $http.get('/api/trios/' + ing).success(function(data) {
              trios=data;
              $rootScope.$broadcast('trios:updated',trios);
           });

    },
    getTrios : function() {
       return trios;
    },


  };
}]);


     app.controller('SearchController', ['$http', 'SearchService', function($http,SearchService)  {
          var ingredientData = this;
          ingredientData.ingredient=SearchService.getIngredient();
          ingredientData.ingredients = [];
          ingredientData.trios =[];

          this.search =function($item, $model, $label) {
                SearchService.setIngredient($item);
           };

          $http.get('/api/ingredients').success(function(data) {
                     ingredientData.ingredients=data;
                 });

      }]);


    app.controller('TrioController', ['$http', '$scope','SearchService',function($http,$scope,SearchService) {
        var flavors=this;
        flavors.trios=SearchService.getTrios();

        $scope.$on('trios:updated', function(event,data) {
         flavors.trios=data;
       });


    }]);

    app.controller('FreshAdditionsController', ['$http',function($http) {
        var flavors=this;
        flavors.additions=[];

        $http.get('/api/pairing/latest').success(function(data) {
            flavors.additions=data;
        });


    }]);

    app.controller('PairingController', function() {
       this.pairing={
           ingredient1: "",
           ingredient2: "",
           affinity: "Tried"
       };

        this.addPairing =function() {
           console.log(this.pairing.ingredient1);
           console.log(this.pairing.ingredient2);
           console.log(this.pairing.affinity);
           console.log(JSON.stringify(this.pairing));
          /* $http.post("/api/pairing",JSON.stringify(this.pairing)).success(function(data) {

                  });*/

        };
    });




})();
