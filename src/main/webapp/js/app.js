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
    search : function(ing) {
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
                SearchService.search($item);
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

    app.controller('PairingController', ['$http', 'SearchService', function($http,SearchService)  {
        var pairingData = this;
       pairingData.pairing={
           ingredient1: "",
           ingredient2: "",
           affinity: "Tried"
       };
       pairingData.ingredients=[];


        pairingData.addPairing =function() {
           console.log(pairingData.pairing.ingredient1);
           console.log(pairingData.pairing.ingredient2);
           console.log(pairingData.pairing.affinity);
           console.log(JSON.stringify(pairingData.pairing));
           $http.post("/api/pairing",JSON.stringify(this.pairing)).success(function(data) {
            alert("pairing saved");
           });

        };

        $http.get('/api/ingredients').success(function(data) {
           pairingData.ingredients=data;
       });
    }]);




})();
