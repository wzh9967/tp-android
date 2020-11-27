function notifyClient(ret, extra,responseCallback) {
    let result = new Object();
    result.ret = ret;
    result.extra = extra;
    let resultStr = toJsonString(result);
    responseCallback(resultStr);
}


function toJsonString(obj) {
    if (obj == undefined) {
        return "{}";
    } else {
        return JSON.stringify(obj);
    }
}

function addPreZero(num){
    var t = (num+'').length,
        s = '';
    for(var i=0; i<64-t; i++){
        s += '0';
    }
    return s+num;
}

function outputObj(obj) {
    var description = "";
    for (var i in obj) {
        description += i + " = " + (obj[i]) + "\n";
    }
    console.log(description);
}

function printAccount(account) {
    var description = "";
    for (var i in account) {
        description += i + " = " + account[i] + "\n";
    }
    console.log(description);
}

function testJson() {
    var result = new Object();
    result.ret = -1;
    alert(toJsonString(result));
}
