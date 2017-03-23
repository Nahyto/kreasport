Parcours JSON example
=====================

This is what /v1/parcours should look like
------------------------------------------

<pre>[{"baliseList":[{"latitude":50.613588,"longitude":3.137106,"parcours":1,"id":0,"title":"IUT entrance"},{"latitude":50.614174,"longitude":3.137404,"parcours":1,"id":0,"title":"4A20"}],"description":"This one should be just at the IUT\u0027s entrance","id":1,"title":"Parcours n° 1"},{"baliseList":[{"latitude":50.613346,"longitude":3.13808,"parcours":1,"id":0,"title":"parking stairs"},{"latitude":50.614294,"longitude":3.138434,"parcours":1,"id":0,"title":"parking exit/entrance"}],"description":"This one should be just on the parking\u0027s stairs","id":2,"title":"Parcours n° 2"}]<code>

Or properly formatted :
-----------------------

<pre>[
    {
        "baliseList": [
            {
                "latitude": 50.613586,
                "longitude": 3.137106,
                "parcours": 1,
                "id": 0,
                "title": "IUT entrance"
            },
            {
                "latitude": 50.614174,
                "longitude": 3.137404,
                "parcours": 1,
                "id": 0,
                "title": "4A20"
            }
        ],
        "description": "This one should be just at the IUT's entrance",
        "id": 1,
        "title": "Parcours n° 1"
    },
    {
        "baliseList": [
            {
                "latitude": 50.613346,
                "longitude": 3.13808,
                "parcours": 1,
                "id": 0,
                "title": "parking stairs"
            },
            {
                "latitude": 50.614292,
                "longitude": 3.138434,
                "parcours": 1,
                "id": 0,
                "title": "parking exit/entrance"
            }
        ],
        "description": "This one should be just on the parking's stairs",
        "id": 2,
        "title": "Parcours n° 2"
    }
]<code>