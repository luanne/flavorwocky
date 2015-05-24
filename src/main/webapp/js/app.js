/**
 * Created by luanne on 29/06/14.
 */

(function () {
    var app = angular.module('flavors', ['ui.bootstrap']);

app.factory("SearchService", ['$rootScope','$http',function($rootScope,$http) {
  var currentIngredient=undefined;
  var trios = [];
  var freshAdditions = [];

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
         $http.get('/api/pairings/latest').success(function(data) {
              freshAdditions=data;
              $rootScope.$broadcast('freshAdditions:updated',freshAdditions);
         });


    },
    getTrios : function() {
       return trios;
    },
    getFreshAdditions: function() {
        return freshAdditions;
    }


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
                     jQuery.each(data, function(index,value) {
                        ingredientData.ingredients.push(value.name);
                     });
                     //ingredientData.ingredients=data;
                 });

      }]);


    app.controller('TrioController', ['$http', '$scope','SearchService',function($http,$scope,SearchService) {
        var flavors=this;
        flavors.trios=SearchService.getTrios();

        $scope.$on('trios:updated', function(event,data) {
         flavors.trios=data;
       });


    }]);

    app.controller('FreshAdditionsController', ['$http', '$scope','SearchService',function($http,$scope,SearchService) {
        var flavors=this;
        flavors.additions=[];

        $http.get('/api/pairings/latest').success(function(data) {
            flavors.additions=data;
        });

        $scope.loadLatestPairing = function(ingredient) {
             SearchService.search(ingredient);
        };

        $scope.$on('freshAdditions:updated', function(event,data) {
             flavors.additions=data;
        });


    }]);

    app.controller('PairingController', ['$http', 'SearchService', function($http,SearchService)  {
        var pairingData = this;
       pairingData.pairing={
           ingredient1: "",
           ingredient2: "",
           category1:"Dairy",
           category2:"Dairy",
           affinity: "TRIED_TESTED"
       };
       pairingData.ingredients=[];
       pairingData.categories=[];
       pairingData.ingredientNames = [];


        pairingData.addPairing =function() {
           console.log(pairingData.pairing.ingredient1);
           console.log(pairingData.pairing.ingredient2);
           console.log(pairingData.pairing.affinity);
           console.log(JSON.stringify(pairingData.pairing));
           $http.post("/api/pairing",JSON.stringify(this.pairing)).success(function(data) {
             $('#myModal').modal('hide');
             SearchService.search(pairingData.pairing.ingredient1);
             pairingData.pairing.ingredient1="";
             pairingData.pairing.ingredient2="";
           });

        };

      pairingData.showCategory =function($item, $model, $label,catNumber) {
           jQuery.each(pairingData.ingredients, function(index,value) {
                            if(value.name == $item) {
                                if(catNumber==1) {
                                    pairingData.pairing.category1 = value.category;
                                }
                            }
                      });
       };


        $http.get('/api/ingredients').success(function(data) {
           pairingData.ingredients=data;
           jQuery.each(data, function(index,value) {
                 pairingData.ingredientNames.push(value.name);
           });

       });
        $http.get('/api/categories').success(function(data) {
                  pairingData.categories=data;
        });
        SearchService.search('Bacon');
    }]);




})();
