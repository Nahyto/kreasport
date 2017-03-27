var map;
var tabBalise = [];
var idxBalises = -1;
var poly;
var distanceTot = 0;

/**
Initialise la map
*/
function initMap() {

    var Arras = {lat: 50.283333, lng: 2.783333};

    map = new google.maps.Map(document.getElementById('map'), {
        center: Arras,
        zoom: 12
    });

    poly = new google.maps.Polyline({
        strokeColor: '#000000',
        strokeOpacity: 1.0,
        strokeWeight: 3
    });
    
    poly.setMap(map);

    map.addListener('click', function(e) {
        placeMarker(e.latLng, map);
    });
    //map.addListener('click', addLatLng);
}

/**
Place un marqueur sur la carte et créé un super objet balise et le stock dans un tableau de balises
*/
function placeMarker(latLng, map) {
    idxBalises ++;


//superobjet qui implemente l'oblet balise de Maps et des champs de questions/reponse
    var balise ={
        marker : new google.maps.Marker({
            position: latLng,
            title: '' + idxBalises,
            draggable: true,
            animation: google.maps.Animation.DROP,
            map: map
        }),
        question: prompt("La question ?", "Quelle est la réponse à la grande question de la vie, l'Univers et le reste ?"),
        reponses:[],
        bonneReponse : 0,

        setReponses : function() {          
            for(var i = 1; i <= 4; i++)
                this.reponses.pop(prompt("Réponse n° " + i, ""));       
        },

        defineGoodAnswer: function() {
            this.bonneReponse = prompt("La bonne reponse est la n°", "");
        }
    }

    balise.setReponses();
    balise.defineGoodAnswer();


    balise.marker.addListener('dblclick', function() {
        map.setZoom(14);
        map.setCenter(marker.getPosition());
    });

    balise.marker.addListener('click', function() {
        map.setZoom(14);
        map.setCenter(marker.getPosition());
    });

    balise.marker.addListener('dragend', function(){
        console.log(calcDistance());
    });

    //map.panTo(latLng);
    tabBalise.push(balise);

    if(idxBalises >= 1){
        
        console.log(calcDistance());
    }


}

// Sets the map on all markers in the array.
function setMapOnAll(map) {
  for (var i = 0; i < tabBalise.length; i++) {
    tabBalise[i].marker.setMap(map);
  }
}

// Removes the markers from the map, but keeps them in the array.
function clearMarkers() {
  setMapOnAll(null);
}

// Shows any markers currently in the array.
function showMarkers() {
  setMapOnAll(map);
}

// Deletes all markers in the array by removing references to them.
function deleteMarkers() {
  clearMarkers();
  tabBalise = [];
}

function deleteLastMarker() {
  
    if(idxBalises -1 >= -1){
        tabBalise[idxBalises].marker.setVisible(false);
        idxBalises --;
    }
  tabBalise.pop();
  console.log(calcDistance());
}

/**
Calcule la distance totale du parcours et la stock dans la variable distanceTot, actualise l'affichage et la retourne
*/
function calcDistance() {
    var res = 0;

    for(var i = 2; i <= tabBalise.length ; i++){
        //console.log(tabBalise[i -2].getPosition().toString());
        //console.log(tabBalise[i -1].getPosition().toString());
        res += google.maps.geometry.spherical.computeDistanceBetween(tabBalise[i -2].marker.getPosition(), tabBalise[i -1].marker.getPosition());
    }

    distanceTot = res;

    actualiserDistance();

    return res
}

/**
Actualise la distance affichée sur la page HTML
*/
function actualiserDistance(){

    var total = document.getElementById("total") ;
         
    total.innerHTML = "" + Math.round(distanceTot) ; 
}


/**
Fonction qui envoie au server les balises et le parcours
*/
function sendToServ(){
    

//post un parcours
    $.ajax({
        // The URL for the request
        url: "/v1/balise",
        // The data to send (will be converted to a query string)
        data: "name=" + prompt("Donnez un nom au parcours", "") + "&key=" + Math.random() * (100000000-1) + 1,
        //data: "longitude=" 
        // Whether this is a POST/GET/UPDATE/DELETE request
        type: "POST",
        // The type of data we expect back
        dataType : "json",
        // Code to run if the request succeeds;
        // the response is passed to the function
        success: function( json ) {
            alert("success");
        },
        // Code to run if the request fails; the raw request and
        // status codes are passed to the function
        error: function( xhr, status, errorThrown ) {
            alert( "Sorry, there was a problem!" );
            console.log( "Error: " + errorThrown );
            console.log( "Status: " + status );
            console.dir( xhr );
        },
        
        // Code to run regardless of success or failure
        complete: function( xhr, status ) {
            alert( "The request is complete!" );
        }
    });


/*
//post des balises
    $.ajax({
        // The URL for the request
        url: "/v1/parcours",
        // The data to send (will be converted to a query string)
        data: "title="+$("#ajoutTitre").val()+"&author="+$("#ajoutAuteur").val(),
        // Whether this is a POST/GET/UPDATE/DELETE request
        type: "POST",
        // The type of data we expect back
        dataType : "json",
        // Code to run if the request succeeds;
        // the response is passed to the function
        success: function( json ) {
            $( "<h1>" ).text( json.title ).appendTo( "body" );
            $( "<div class=\"content\">").html( json.html ).appendTo( "body" );
        },
        // Code to run if the request fails; the raw request and
        // status codes are passed to the function
        error: function( xhr, status, errorThrown ) {
            alert( "Sorry, there was a problem!" );
            console.log( "Error: " + errorThrown );
            console.log( "Status: " + status );
            console.dir( xhr );
        },
        // Code to run regardless of success or failure
        complete: function( xhr, status ) {
            alert( "The request is complete!" );
        }
    });*/
}
