var map;

var tabBalise = [];

var idxBalises = -1;

function initMap() {

    var Arras = {lat: 50.283333, lng: 2.783333};


    map = new google.maps.Map(document.getElementById('map'), {
        center: Arras,
        zoom: 12
    });

    /*var marker = new google.maps.Marker({
        position: myLatlng,
        map: map,
        title: 'Click to zoom'
    });*/

    /*map.addListener('center_changed', function() {
        // 3 seconds after the center of the map has changed, pan back to the
        // marker.
        window.setTimeout(function() {
            map.panTo(marker.getPosition());
        }, 3000);
    });*/

    map.addListener('click', function(e) {
        placeMarkerAndPanTo(e.latLng, map);
    });
}

/**

*/
function placeMarkerAndPanTo(latLng, map) {
    idxBalises ++;

    var marker = new google.maps.Marker({
        position: latLng,
        title: idxBalises +1,

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

    //map.panTo(latLng);
    tabBalise.push(marker);
    


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
  
  tabBalise[idxBalises].setVisible(false);
  idxBalises --;

  tabBalise.pop();


}
