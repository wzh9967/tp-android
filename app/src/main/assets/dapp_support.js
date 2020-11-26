//获取app版本
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

//获取钱包列表
function getWalletList(callback){
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
    _sendTpRequest('getWalletList', '', tpCallbackFun);
}

//获取设备信息
function getDeviceId(callback){
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
    _sendTpRequest('getDeviceId', '', tpCallbackFun);
}

//分享
function shareToSNS(callback){
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
    _sendTpRequest('shareToSNS', '', tpCallbackFun);
}

//开启扫描
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

//获取当前交易钱包
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

//交易签名
function sign(callback){
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
    _sendTpRequest('sign', '', tpCallbackFun);
}

//从浏览器返回
function back(callback){
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
    _sendTpRequest('back', '', tpCallbackFun);
}

//关闭浏览器
function close(callback){
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
    _sendTpRequest('close', '', tpCallbackFun);
}

//浏览器全屏
function fullScreen(callback){
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
    _sendTpRequest('fullScreen', '', tpCallbackFun);
}

//跳转到钱包导入页面
function importWallet(callback){
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
    _sendTpRequest('importWallet', '', tpCallbackFun);
}

//导航条可见
function setMenubar(callback){
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
    _sendTpRequest('setMenubar', '', tpCallbackFun);
}


//保存图片
function saveImage(callback){
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
    _sendTpRequest('saveImage', '', tpCallbackFun);
}


//横屏
function rollHorizontal(callback){
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
    _sendTpRequest('rollHorizontal', '', tpCallbackFun);
}

//禁止iOS自带的左滑手势返回，对安卓无影响
function popGestureRecognizerEnable(callback){
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
    _sendTpRequest('popGestureRecognizerEnable', '', tpCallbackFun);
}



//禁止webview自带的左滑手势触发goback
function forwardNavigationGesturesEnable(callback){
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
    _sendTpRequest('forwardNavigationGesturesEnable', '', tpCallbackFun);
}

//获取当前节点
function getNodeUrl(callback){
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
    _sendTpRequest('getNodeUrl', '', tpCallbackFun);
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
