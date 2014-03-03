var devListReq;
if (navigator.appName == "Microsoft Internet Explorer") {
    devListReq = new ActiveXObject("Microsoft.XMLHTTP");
} else {
    devListReq = new XMLHttpRequest();
}

var devURL = "/listmicaztext";
var infoURL = [  // List of URLs we are interested in for each device
        "status", // This gets us battery info, uptime etc.
        "temp",  // temp readings
		"hum",
		"light"
];

var deviceObj = []; // cumulative list of device objects

function deviceSortFunction(a, b) {
    if (a.id < b.id) {
        return -1;
    } else if (a.id == b.id) {
        return 0
    } else {
        return 1;
    }
}

function findDevice(devArray, devId) {
    for (var i = 0; i < devArray.length; i++) {
        if (devArray[i].id == devId) {
            return devArray[i];
        }
    }

    return null;
}

var respHandler = function f(dev, url, responseText) {
    
    // XXX: Look up the device and update its information
    // Need to perform lots of sanity checks
    		
	var response = eval("(" + responseText + ")");
		
    if (url == "status") {
		dev.setStatus(response);
    } else if (url == "temp") {
        dev.setTemp(response);
    } else if (url == "leds") {
        dev.setLED(response);
    } else if (url == "hum") {
		dev.setHum(response);
    } else if (url == "light") {
        dev.setLight(response);
    }else {
        alert("URL desconocida: " + url);
    }
}

function initModel() {
    //alert(getCompactCurrentTimestamp() + "Initialized model")
}

function updateModel() {
    //alert(getCompactCurrentTimestamp() + "Updated model");
    if (!AUTO_REFRESH) return;
    
    // Update the list of devices and information on each device
    if (devListReq && devListReq.readyState != 4) {
        //alert("Aborting devlistrequest");
        devListReq.abort();
    }


    devListReq.open("GET", devURL);
    devListReq.onreadystatechange=function() {
	        if (devListReq.readyState == 4) {
            // XXX: keep track of errors ...
			
            if (devListReq.status == 200) {
						
                var list = eval("(" + devListReq.responseText + ")");
				updateDevices(list);

            }
        }
    }
    devListReq.send(null);
}

function updateDevices(currentDevList) {
    var devObj;
    var added = false;
	
    for (var i = 0; i < currentDevList.length; i++) {
        // Check if the device is already on the cumulative list
		if (findDevice(deviceObj, currentDevList[i].id) == null) {
            // add it if it isn't there ...
          //  alert("Creating a new device ...");
            devObj = new device(currentDevList[i].id);
        //    alert("Created device \n" + devObj.id)
            deviceObj.push(devObj);
            added = true;
        }
    }

    if (added) { // we added at least one device, sort array
        deviceObj.sort(deviceSortFunction);
    }
	
    
    // We only send requests to devices that are still on the Gateway's
    // list of known devices.
    var delayBetweenAccesses = (MODEL_REFRESH_PERIOD) / (currentDevList.length * infoURL.length);
    var cnt = 0;
    for (var j = 0; j < currentDevList.length; j++) {
        for (var k = 0; k < infoURL.length; k++) {
//            updateDeviceInfo(currentDevList[j], infoURL[k], respHandler);
     //      alert("Do this: updateDeviceInfo('" + currentDevList[j] + "', '" +
     //           [k] + "', respHandler) after " + (cnt * delayBetweenAccesses));
            setTimeout("updateDeviceInfo('" + currentDevList[j].id + "', '" +
                infoURL[k] + "', respHandler)", (cnt * delayBetweenAccesses));
            cnt++;
        }
    }
}

function updateDeviceInfo(devId, url, infoHandler) {
    var infoReq;
    var dev = findDevice(deviceObj, devId);

    if (dev == null) {
     //   alert("Ningun dispositivo encontrado para " + devId );
        return;
    }

    if (navigator.appName == "Microsoft Internet Explorer") {
        infoReq = new ActiveXObject("Microsoft.XMLHTTP");
    } else {
        infoReq = new XMLHttpRequest();
    }

    infoReq.open("GET", "/gw-d205/" + devId + "/sensor/" + url);
    infoReq.onreadystatechange = function() {
        if (infoReq.readyState == 4) {
            // XXX: Keep track of other status codes ...
            if (infoReq.status == 200) {
                
				var now = new Date();
          //  alert("Handling " + devId + "/" + url)
                dev.successfulRequests++;
                dev.setLastHeard(now.getTime()); // updated on a successful response
                infoHandler(dev, url, infoReq.responseText);
			
            } else {
                
				
				dev.failedRequests++;
				
            }
        }
    }
    infoReq.send(null);
}

function updateModelRefreshPeriod(ms) {
    updateStatus("El periodo de refresco del modelo es " + ms + " ms.");
    MODEL_REFRESH_PERIOD = ms;
    DELAY_THRESHOLD = 3 * MODEL_REFRESH_PERIOD;
}
