/**
 * Created by luanne on 29/06/14.
 */

(function () {
    var app = angular.module('flavors', [ ]);
    app.controller('TrioController', function() {
        this.trios= [
            {
                name: "Bacon, cheese, onion"
            },
            {
                name: "Avocado, lime, bacon"
            },
            {
                name: "Chicken, mushrooms, onion"
            }

        ];
    });

    app.controller('FreshAdditionsController', function() {
        this.additions= [
            {
                name: "Bacon and cheese"
            },
            {
                name: "Basil and Pine nuts"
            }
        ];
    });

    app.controller('PairingController', function() {
       this.pairing={
           ingredient1: "",
           ingredient2: "",
           affinity: "Tried"
       };
        this.addPairing =function() {
           console.log(this.pairing.ingredient1);
        };
    });

})();
