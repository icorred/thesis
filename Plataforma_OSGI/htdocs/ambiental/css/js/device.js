
function device(id) {
    this.id = id;
    this.successfulRequests = 0;
    this.failedRequests = 0;    
    this.lastHeard = null;
    this.name = "";
    this.battery = null;
    this.isCharging = false;
    this.uptime = 0;
    this.isEndNode = false;

    this.temp = new Object();
	
    this.temp.value = [];
    for (var i = 0; i < TEMP_READING_COUNT; i++) {
        this.temp.value[i] = null;
    }
	
	this.hum = new Object();
	this.hum.value = [];
	for (var i = 0; i < HUM_READING_COUNT; i++) {
        this.hum.value[i] = null;
    }
	
	this.light = new Object();
	this.light.value = [];
	for (var i = 0; i < HUM_READING_COUNT; i++) {
        this.light.value[i] = null;
    }
	

    this.led = new Object();
    this.led.r = null;
    this.led.g = null;
    this.led.b = null;
    this.led.timestamp = 0;
}

device.prototype.setStatus = function(statusObj) {
    var intVal;

    if (statusObj != null) {
        if (statusObj.n != undefined) {
            this.name = statusObj.n;
        }

        if (statusObj.b != undefined) {
            intVal = parseInt(statusObj.b);
            if (!isNaN(intVal) && (intVal >= 0) && (intVal <= 200)) {
                this.battery = intVal;
            }
        }

        if (statusObj.c != undefined) {
            this.isCharging = (statusObj.c == "y");
        }

        if (statusObj.u != undefined) {
            intVal = parseInt(statusObj.u);
            if (!isNaN(intVal)) {
                this.uptime = intVal;
            }
        }

        if (statusObj.e != undefined) {
            this.isEndNode = (statusObj.e == "y");
        }
    }
}

device.prototype.setLight = function(lightObj) {
    var intVal;

    if (lightObj != null) {
        //alert("Obj=" + lightObj + ", Got light val: " + lightObj.l);

        if (lightObj.light != undefined) {
            intVal = parseInt(lightObj.t);
            if (!isNaN(intVal)) {
                this.light.timestamp = intVal;
            }
                
        } else {
            // The lightObj could be a single integer ...
            intVal = parseInt(lightObj);
            if (!isNaN(intVal)) {
                this.light.value.push(intVal); // append element
                this.light.value.shift(); // remove first element
            }
        }
    }
}

device.prototype.setTemp = function(tempObj) {
    var intVal;

    if (tempObj != null) {
        //alert("Obj=" + lightObj + ", Got light val: " + lightObj.l);

        if (tempObj.temp != undefined) {
            intVal = parseInt(tempObj.t);
            if (!isNaN(intVal)) {
                this.temp.timestamp = intVal;
            }
                
        } else {
            // The lightObj could be a single integer ...
            intVal = parseInt(tempObj);
            if (!isNaN(intVal)) {
                this.temp.value.push(intVal); // append element
                this.temp.value.shift(); // remove first element
            }
        }
    }
}

device.prototype.setHum = function(humObj) {
    var intVal;

    if (humObj != null) {
        //alert("Obj=" + lightObj + ", Got light val: " + lightObj.l);

        if (humObj.hum != undefined) {
            intVal = parseInt(humObj.t);
            if (!isNaN(intVal)) {
                this.hum.timestamp = intVal;
            }
        
        } else {
            // The lightObj could be a single integer ...
            intVal = parseInt(humObj);
            if (!isNaN(intVal)) {
                this.hum.value.push(intVal); // append element
                this.hum.value.shift(); // remove first element
            }
        }
    }
}

device.prototype.setLED = function(ledObj) {
    var intVal = 0;

    
    if (ledObj != null) {
        //alert("ledObj=" + ledObj + "Got led val: " + ledObj.r + ", " + ledObj.g + ", " + ledObj.b);
        if ((ledObj instanceof Array) && (ledObj.length == 3)) {
            for (var i = 0; i < ledObj.length; i++) {
                intVal = parseInt(ledObj[i]);
                if (!isNaN(intVal) && (intVal > -1) && (intVal < 256)) {
                    switch (i) {
                        case 0:
                            this.led.r = intVal;
                            break;
                        case 1:
                            this.led.g = intVal;
                            break;
                        case 2:
                            this.led.b = intVal;
                            break;
                    }
                }
            }
            return;
        }

        if (ledObj.t != undefined) {
            intVal = parseInt(ledObj.t);
            if (!isNaN(intVal)) {
                this.led.timestamp = intVal;
            }
        }

        if (ledObj.r != undefined) { // Explicitly check for undefined, otherwise 0 is treated as false
            intVal = parseInt(ledObj.r);
            if (!isNaN(intVal) && (intVal > -1) && (intVal < 256)) {
                this.led.r = intVal;
            }
//            alert("Dev " + this.id + ", intVal=" + intVal +
//                ", r got=" + ledObj.r + ", r set=" + this.led.r);
        }

        if (ledObj.g != undefined) {
            intVal = parseInt(ledObj.g);
            if (!isNaN(intVal) && (intVal > -1) && (intVal < 256)) {
                this.led.g = intVal;
            }
//            alert("Dev " + this.id + ", intVal=" + intVal +
//                ", g got=" + ledObj.g + ", g set=" + this.led.g);
        }

        if (ledObj.b != undefined) {
            intVal = parseInt(ledObj.b);
            if (!isNaN(intVal) && (intVal > -1) && (intVal < 256)) {
                this.led.b = intVal;
            }
//            alert("Dev " + this.id + ", intVal=" + intVal +
//                ", b got=" + ledObj.b + ", b set=" + this.led.b);
        }
    }
}

device.prototype.toString = function() {
    var string = "";

    string += "name: " + this.name + "\n";
    string += "id: " + this.id + "\n";
    string += "battery: " + this.battery + (this.isCharging ? " (charging)" : "") + "\n";
    string += "uptime: " + millisToString(this.uptime) + "\n";
    string += "light: [";
    for (var i = 0; i < LIGHT_READING_COUNT; i++) {
        string += this.light.value[i];
        if (i < LIGHT_READING_COUNT - 1) string += ", ";
    }
    string += "]\n";
    
    string += "led: (" + this.led.r + "," + this.led.g + "," + this.led.b + 
        ") " + getCompactTimestamp(this.light.timestamp) + "\n";

    return string;
}

device.prototype.setLastHeard = function(ts) {
    var intVal;

    intVal = parseInt(ts);
    if (!isNaN(intVal) && (intVal >= 0)) {
        this.lastHeard = intVal;
    }    
}

function sendCommand(devId, cmd, url, data) {
    var httpreq;

    updateStatus("Comunicando con " + devId + " ... ");
    if (navigator.appName == "Microsoft Internet Explorer") {
        httpreq = new ActiveXObject("Microsoft.XMLHTTP");
    } else {
        httpreq = new XMLHttpRequest();
    }

    httpreq.open(cmd, url);
	httpreq.onreadystatechange = function() {
        if (httpreq.readyState == 4) {
            // XXX: Keep track of other status codes ...
            if (httpreq.status != 200) {
                updateStatus("Comunicando con " + devId + " ... " +
                    httpreq.responseText);
            } else {
                updateStatus("Comunicando con " + devId + " ... " + "Hecho!");
            }
        }
    }

    httpreq.send(data);
}

function blink(devId) {
    sendCommand(devId, "POST", "/" + devId + "/blink", "255,0,0");
}

function changeColor(devId) {
    var rgb = prompt("Introduzca un color para " + devId + ", e.g. [255,255,0]");
    if (rgb) {
        //alert("Changing color for " + devId + " to " + rgb);
        sendCommand(devId, "PUT", "/gw-d205/" + devId + "/actuator/leds", rgb);
    } else {
        //alert("Cancelled!");
    }
}

function changeName(devId) {
    var name = prompt("Enter new name for " + devId);
    if (name) {
        //alert("Changing name for " + devId + " to " + name);
        sendCommand(devId, "POST", "/" + devId + "/props", "spot.name:" + name);
    } else {
        //alert("Cancelled!");
    }
}

function makeEndNode(devId) {
    sendCommand(devId, "POST", "/" + devId + "/props", "spot.mesh.routing.enable:ENDNODE");
}

function updateStatus(statusString) {
    document.getElementById("dashboardstatusbar").innerHTML = statusString;
}