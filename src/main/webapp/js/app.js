/**
 * Created by luanne on 29/06/14.
 */

(function () {
    var app = angular.module('flavors', [ ]);

    app.controller('TrioController', ['$http',function($http) {
    var flavors=this;
    flavors.trios=[];

    $http.get('/api/trios/i1').success(function(data) {
        flavors.trios=data;
    });

    }]);

    app.controller('FreshAdditionsController', ['$http',function($http) {
        var flavors=this;
        flavors.additions=[];

        $http.get('/api/latestPairings').success(function(data) {
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
        };
    });

})();
