
function getNodeFromNative(callback){
     var tpCallbackFun = _getCallbackName();
        window[tpCallbackFun] = function (result) {
            result = result.replace(/\r/ig, "").replace(/\n/ig, "");
            try {
                let res = JSON.parse(result);
                callback(res.node,0)
            } catch (err) {
                callback(res.node,err)
                console.log(err);
            }
        }
     _sendTpRequest('sendNodeToJs', '', tpCallbackFun);
}
var _getCallbackName = function () {
    var ramdom = parseInt(Math.random() * 100000);
    return 'tp_callback_' + new Date().getTime() + ramdom;
}

var _sendTpRequest = function (methodName, params, callback) {
    if (window.client) {
        window.client.callHandler(methodName, params, callback);
    }
}

var _getTypeByStr = function (typeStr) {
    var reTrim = /^\s+|\s+$/g;
    typeStr += '';
    typeStr = typeStr.replace(reTrim, '').toLowerCase();
    return TYPE_MAP[typeStr] || typeStr;
}

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
