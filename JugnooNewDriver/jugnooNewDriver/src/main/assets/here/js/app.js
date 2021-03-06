
const CONFIG = {
	// Configure your XYZ Token + space here
	// YOUR_ACCESS_TOKEN = "<TOKEN>",
	// YOUR_SPACE_ID = "<SPACE_ID>",
	YOUR_ACCESS_TOKEN: "AU2S5zlMHzBKTFfMJcVbIgw",
	YOUR_SPACE_ID: "tFRXwS5L",

	JUG_APP_ID: "<here_auth_identifier>",  //"Y8pzxL8Y52FBp0qtaYVO", //YOUR_SPACE_ID
	JUG_APP_CODE: "<here_auth_secret>",     //"Am5XvFppRInSeFpAtijKeg", //YOUR_ACCESS_TOKEN

	// Configure the space with the base places + a corresponding access token:
	BASE_PLACES_ACCESS_TOKEN: "AaZ0BoklkXtuQdihOtfP9hk",
	BASE_PLACES_SPACE_ID: "here-place",
};

// const getPlaceName = (feature) => {
//     if(
//         feature.properties && 
//         feature.properties.names && 
//         feature.properties.names[0] && feature.properties.names[0].name
//     ){
//         return feature.properties.names[0].name.length >= 15 ? feature.properties.names[0].name.substr(0,13) + '...' : feature.properties.names[0].name;
//     }
//     return "";
// };

const imageLayer = new here.xyz.maps.layers.TileLayer({
	name: 'Image Layer',
	min: 1,
	max: 20,
	provider: new here.xyz.maps.providers.ImageProvider({
		name: 'Live Map',
		url: 'https://{SUBDOMAIN_INT_1_4}.mapcreator.tilehub.api.here.com/tilehub/wv_livemap_bc/png/sat/256/{QUADKEY}?access_token=' + CONFIG.JUG_APP_CODE,
//		url: 'https://{SUBDOMAIN_INT_1_4}.base.maps.api.here.com/maptile/2.1/maptile/newest/normal.day/{Z}/{X}/{Y}/256/png8?app_id={'+CONFIG.JUG_APP_ID+'}+&app_code={'+CONFIG.JUG_APP_CODE+'}'
	})
});

const herePlacesLayer = new here.xyz.maps.layers.TileLayer({
	name: 'HERE Places Layer',
	min: 16,
	max: 20,
	provider: new here.xyz.maps.providers.SpaceProvider({
		name: 'HERE Places',
		space: CONFIG.BASE_PLACES_SPACE_ID,
		environment: "prd",
		level: 16,
		credentials: {
			access_token: CONFIG.BASE_PLACES_ACCESS_TOKEN
		}
	}),
	style: {
		styleGroups: {
			common: [
				{ zIndex: 0, type: "Circle", "fill": "#ffffff", radius: 6 },
				{ zIndex: 1, type: "Circle", "fill": "#39A0FF", radius: 4 },
			],
			common_hovered: [
				{ zIndex: 0, type: "Circle", "fill": "#ffffff", radius: 6 },
				{ zIndex: 1, type: "Circle", "fill": "#39A0FF", radius: 4 },
				{ zIndex: 2, type: "Text", textRef: "properties.names[0].name", /*text: getPlaceName,*/ stroke: '#666', strokeWidth: 3, fill: "#fff", offsetY: -15, font: "bold 13px arial" }
			]
		},
		assign: function (feature) {
			return "common";
		}
	}
});

const customPlacesLayer = new here.xyz.maps.layers.TileLayer({
	name: 'Custom Places Layer',
	min: 16,
	max: 20,
	provider: new here.xyz.maps.providers.SpaceProvider({
		name: 'Custom Places',
		space: CONFIG.JUG_APP_ID,
		environment: "prd",
		level: 16,
		credentials: {
			access_token: CONFIG.JUG_APP_CODE
		}
	}),
	style: {
		styleGroups: {
			common: [
				{ zIndex: 0, type: "Circle", "fill": "#ffffff", radius: 6 },
				{ zIndex: 1, type: "Circle", "fill": "rgb(42, 208, 33)", radius: 4 },
			],
			common_hovered: [
				{ zIndex: 0, type: "Circle", "fill": "#ffffff", radius: 6 },
				{ zIndex: 1, type: "Circle", "fill": "rgb(42, 208, 33)", radius: 4 },
				{ zIndex: 2, type: "Text", textRef: "properties.names[0].name", /*text: getPlaceName,*/ stroke: '#666', strokeWidth: 3, fill: "#fff", offsetY: -15, font: "bold 13px arial" }
			]
		},
		assign: function (feature) {
			return "common";
		}
	}
});

// setup the Map Display
const display = new here.xyz.maps.Map(document.getElementById("map"), {
	zoomLevel: 18,
	center: {
		longitude: <gps_longitude>, latitude: <gps_latitude>
	},

	// add layers to display
	layers: [imageLayer]
	//here places and custom places layers commented
	//herePlacesLayer, customPlacesLayer
});
//longitude: <gps_longitude>, latitude: <gps_latitude>    ,    longitude: 100.4888, latitude: 13.7554



// Register events on the map display
const onPointerEnter = (e) => {
	if (
		e.data && 
		e.data.layer && 
		(e.data.layer === herePlacesLayer || e.data.layer === customPlacesLayer)
	) {
		herePlacesLayer.setStyleGroup(e.target, herePlacesLayer.getStyle().styleGroups["common_hovered"]);
	}
}

const onPointerLeave = (e) => {
	if (
		e.data && 
		e.data.layer && 
		(e.data.layer === herePlacesLayer || e.data.layer === customPlacesLayer)
	) {
		// reset the style after pointerleave
		herePlacesLayer.setStyleGroup(e.target);
	}
}

display.addEventListener('pointerup', (e) => {
	console.log('pointerup', e)
});
display.addEventListener('pointerenter', onPointerEnter);
display.addEventListener('pointerleave', onPointerLeave);
display.addEventListener('pointerdown', onPointerEnter);
display.addEventListener('pointerup', onPointerLeave);


const generateFeature = (coord) => {

	// GeoJson in HERE Map Object format with minimum properties.
	const featureGeoJson = {
		geometry: {
			coordinates: [coord.longitude, coord.latitude, 0],
			type: "Point"
		},
		properties: {
			names: [
				{name: "Test Place", languageCode: "wen"}
			],
			categories: ["100-1000-0000"] // Restaurant
		},
		type: "Feature"
	}

	return featureGeoJson;
}


const handleAddPlace = () => {
	document.querySelector('body').classList.add('modeAddPlace');

	// Guarantee zoomLevel of at least 16
	if(display.getZoomlevel() < 16)
		display.setZoomlevel(16);
}

const handleSavePlace = () => {
	document.querySelector('body').classList.remove('modeAddPlace');
	const provider = customPlacesLayer.getProvider();


	// Create feature at the center position of the map
	const feature = generateFeature(display.getCenter())

	// Add the feature to the map (so it's visualized to the user immidiatly).
	provider.addFeature(feature);

	// Commit new feature into the customPlaces space.
	// For reference on this important step, see:
	// https://xyz.api.here.com/maps/latest/documentation/here.xyz.maps.providers.SpaceProvider.html#commit
	provider.commit(
		{put: feature},
		(createdData) => console.log('provider.commit successful', createdData),
		(error) => console.log('provider.commit failed', error)
	);

}


document.querySelector('#addPlace').addEventListener('pointerup', () => handleAddPlace());
document.querySelector('#savePlace').addEventListener('pointerup', () => handleSavePlace());

// Resize map display, when the browser is resized.
window.addEventListener('resize', () => {
	display.resize();
})

var a = ""

const getCenter = () => {
    const feature = generateFeature(display.getCenter())
//    console.log("hello feature="+feature.geometry.coordinates)
    a = feature.geometry.coordinates[1]+","+feature.geometry.coordinates[0]
//    console.log("hello a="+a)
	return a;
}

handleAddPlace();

function restrictMap(map){

  var bounds = new H.geo.Rect(<ne_lat>, <ne_lng>, <sw_lat>, <sw_lng>);

  map.getViewModel().addEventListener('sync', function() {
    var center = map.getCenter();

    if (!bounds.containsPoint(center)) {
      if (center.lat > bounds.getTop()) {
        center.lat = bounds.getTop();
      } else if (center.lat < bounds.getBottom()) {
        center.lat = bounds.getBottom();
      }
      if (center.lng < bounds.getLeft()) {
        center.lng = bounds.getLeft();
      } else if (center.lng > bounds.getRight()) {
        center.lng = bounds.getRight();
      }
      map.setCenter(center);
    }
  });

  //Debug code to visualize where your restriction is
  map.addObject(new H.map.Rect(bounds, {
    style: {
        fillColor: 'rgba(55, 85, 170, 0.1)',
        strokeColor: 'rgba(55, 85, 170, 0.6)',
        lineWidth: 8
      }
    }
  ));
}

restrictMap(display)