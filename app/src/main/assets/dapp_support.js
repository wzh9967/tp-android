function getAppInfo(callback){
    var tpCallbackFun = _getCallbackName();
    window[tpCallbackFun] = function (result) {
        result = result.replace(/\r/ig, "").replace(/\n/ig, "");
        try {
            let res = JSON.parse(result);
            console.log(res)
            callback(1,res)
        } catch (err) {
            callback(0,err)
        }
    }
    _sendTpRequest('getAppInfo', '', tpCallbackFun);
}

function getCurrentWallet(callback){
    var tpCallbackFun = _getCallbackName();
    window[tpCallbackFun] = function (result) {
        result = result.replace(/\r/ig, "").replace(/\n/ig, "");
        try {
            let res = JSON.parse(result);
            console.log(res)
            callback(1,res)
        } catch (err) {
            callback(0,err)
        }
    }
    _sendTpRequest('getCurrentWallet', '', tpCallbackFun);
}

function invokeQRScanner(callback){
    var tpCallbackFun = _getCallbackName();
    window[tpCallbackFun] = function (result) {
        result = result.replace(/\r/ig, "").replace(/\n/ig, "");
        try {
            let res = JSON.parse(result);
            console.log(res)
            callback(1,res)
        } catch (err) {
            callback(0,err)
        }
    }
    _sendTpRequest('invokeQRScanner', '', tpCallbackFun);
}

function getAppInfo(callback){
    var tpCallbackFun = _getCallbackName();
    window[tpCallbackFun] = function (result) {
        result = result.replace(/\r/ig, "").replace(/\n/ig, "");
        try {
            let res = JSON.parse(result);
            console.log(res)
            callback(1,res)
        } catch (err) {
            callback(0,err)
        }
    }
    _sendTpRequest('getAppInfo', '', tpCallbackFun);
}

function _getCallbackName() {
    var ramdom = parseInt(Math.random() * 100000);
    return 'tp_callback_' + new Date().getTime() + ramdom;
}

function _sendTpRequest (methodName, params, callback) {
    if (window.JsNativeBridge) {

        window.JsNativeBridge.callHandler(methodName, params, callback);
    }
}

function _getTypeByStr (typeStr) {
    var reTrim = /^\s+|\s+$/g;
    typeStr += '';
    typeStr = typeStr.replace(reTrim, '').toLowerCase();
    return TYPE_MAP[typeStr] || typeStr;
}
