function Test(){
    console.log("do test")
     var tpCallbackFun = _getCallbackName();
        window[tpCallbackFun] = function (result) {
            result = result.replace(/\r/ig, "").replace(/\n/ig, "");
            try {
                var res = JSON.parse(result);
                console.log("最后的结果"+res)
            } catch (e) {
                console.log(e);
            }
        }
     _sendTpRequest('test', '', tpCallbackFun);
}



var _getCallbackName = function () {
    var ramdom = parseInt(Math.random() * 100000);
    return 'tp_callback_' + new Date().getTime() + ramdom;
}

var _sendTpRequest = function (methodName, params, callback) {
    if (window.client) {
        console.log("call native");
        window.client.callHandler(methodName, params, callback);
    }
    // ios
    if (window.webkit) {
        window.webkit.messageHandlers[methodName].postMessage({
            body: {
                'params': params,
                'callback': callback
            }
        });
    }
}

var _getTypeByStr = function (typeStr) {
    var reTrim = /^\s+|\s+$/g;
    typeStr += '';
    typeStr = typeStr.replace(reTrim, '').toLowerCase();
    return TYPE_MAP[typeStr] || typeStr;
}