
function device(id) {
    this.id = id;
    this.successfulRequests = 0;
    this.failedRequests = 0;    
    this.lastHeard = null;
    this.name = "";
    this.battery = 0;
    this.isCharging = false;
    this.uptime = 0;
    this.isEndNode = false;

    this.heart = new Object();
    this.heart.value = [];
    for (var i = 0; i < HEART_READING_COUNT; i++) {
        this.heart.value[i] = null;
    }
	
	this.bodyTemp = new Object();
    this.bodyTemp.value = [];
    for (var i = 0; i < TEMP_READING_COUNT; i++) {
        this.bodyTemp.value[i] = null;
    }
	
	this.posture = new Object();
    this.posture.value = [];
    for (var i = 0; i < TEMP_READING_COUNT; i++) {
        this.posture.value[i] = null;
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

device.prototype.setHeart = function(heartObj) {
    var intVal;

    if (heartObj != null) {
        //alert("Obj=" + lightObj + ", Got light val: " + lightObj.l);

        if (heartObj.t != undefined) {
            intVal = parseInt(heartObj.t);
            if (!isNaN(intVal)) {
                this.heart.timestamp = intVal;
            }
        }

        if (heartObj.l != undefined) {
            intVal = parseInt(heartObj.l);
            if (!isNaN(intVal)) {
//                this.light.value = intVal;
                this.heart.value.push(intVal); // append element
                this.heart.value.shift(); // remove first element
            }
        } else {
            // The lightObj could be a single integer ...
            intVal = parseInt(heartObj);
            if (!isNaN(intVal)) {
                this.heart.value.push(intVal); // append element
                this.heart.value.shift(); // remove first element
            }
        }
    }
}

device.prototype.setPosture = function(postureObj) {
    var intVal;

    if (postureObj != null) {
        //alert("Obj=" + lightObj + ", Got light val: " + lightObj.l);

        if (postureObj.t != undefined) {
            intVal = parseInt(postureObj.t);
            if (!isNaN(intVal)) {
                this.posture.timestamp = intVal;
            }
        }

        if (postureObj.l != undefined) {
            intVal = parseInt(postureObj.l);
            if (!isNaN(intVal)) {
//                this.light.value = intVal;
                this.posture.value.push(intVal); // append element
                this.posture.value.shift(); // remove first element
            }
        } else {
            // The lightObj could be a single integer ...
            intVal = parseInt(postureObj);
            if (!isNaN(intVal)) {
                this.posture.value.push(intVal); // append element
                this.posture.value.shift(); // remove first element
            }
        }
    }
}

device.prototype.setBodyTemp = function(tempObj) {
    var intVal;

    if (tempObj != null) {
        //alert("Obj=" + lightObj + ", Got light val: " + lightObj.l);

        if (tempObj.t != undefined) {
            intVal = parseInt(tempObj.t);
            if (!isNaN(intVal)) {
                this.light.timestamp = intVal;
            }
        }

        if (tempObj.l != undefined) {
            intVal = parseInt(tempObj.l);
            if (!isNaN(intVal)) {
//                this.light.value = intVal;
                this.bodyTemp.value.push(intVal); // append element
                this.bodyTemp.value.shift(); // remove first element
            }
        } else {
            // The lightObj could be a single integer ...
            intVal = parseInt(tempObj);
            if (!isNaN(intVal)) {
                this.bodyTemp.value.push(intVal); // append element
                this.bodyTemp.value.shift(); // remove first element
            }
        }
    }
}


device.prototype.setBat = function(batObj) {

var intVal;

    if (batObj != null) {
	
        intVal = parseInt(batObj);
        if (!isNaN(intVal) && (intVal >= 0) && (intVal <= 200)) {
            this.battery = intVal;
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