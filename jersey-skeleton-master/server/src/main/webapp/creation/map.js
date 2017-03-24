var map;

var tabBalise = [];

var idxBalises = -1;

var poly;

var distanceTot = 0;

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

*/
function placeMarker(latLng, map) {
    idxBalises ++;

    var marker = new google.maps.Marker({
        position: latLng,
        title: '' + idxBalises,
        draggable: true,
        animation: google.maps.Animation.DROP,


        map: map
    });


    marker.addListener('dblclick', function() {
        map.setZoom(14);
        map.setCenter(marker.getPosition());
    });

    marker.addListener('click', function() {
        map.setZoom(14);
        map.setCenter(marker.getPosition());
    });

    marker.addListener('dragend', function(){
        console.log(calcDistance());
    });

    //map.panTo(latLng);
    tabBalise.push(marker);

    refaireLeTrace();

    if(idxBalises >= 1){
        
        console.log(calcDistance());
    }


}

// Sets the map on all markers in the array.
function setMapOnAll(map) {
  for (var i = 0; i < tabBalise.length; i++) {
    tabBalise[i].setMap(map);
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
        tabBalise[idxBalises].setVisible(false);
        idxBalises --;
    }
  tabBalise.pop();
  console.log(calcDistance());
}

//calculates distance between two points in m's
function calcDistance() {
    var res = 0;

    for(var i = 2; i <= tabBalise.length ; i++){
        //console.log(tabBalise[i -2].getPosition().toString());
        //console.log(tabBalise[i -1].getPosition().toString());
        res += google.maps.geometry.spherical.computeDistanceBetween(tabBalise[i -2].getPosition(), tabBalise[i -1].getPosition());
    }

    distanceTot = res;

    actualiserDistance();

    return res
}
/*
function addLatLng(event) {
  var path = poly.getPath();
  path.push(event.latLng);
}
*/
function refaireLeTrace(){
    
    var tmp =[];

    for (var i = 0; i < tabBalise.length; i++){

        console.log(tabBalise[i].getPosition().toString());

        tmp.pop(tabBalise[i].getPosition().toString());
    }

    
    poly = new google.maps.Polyline({
        path: tmp,
        geodesic: true,
        strokeColor: '#FF0000',
        strokeOpacity: 1.0,
        strokeWeight: 2
  });
}

function actualiserDistance(){

    var total = document.getElementById("total") ;
         
    total.innerHTML = "" + Math.round(distanceTot) ; 
}



function sendToServ(){
    //TODO envoyer au server les info des balises du tableau 
}
