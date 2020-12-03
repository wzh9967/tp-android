function notifyClient(callid, ret, extra) {
    let result = new Object();
    result.ret = ret;
    result.callid = callid;
    result.extra = extra;
    let resultStr = toJsonString(result);
    window.client.notifyWeb3Result(resultStr);
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
