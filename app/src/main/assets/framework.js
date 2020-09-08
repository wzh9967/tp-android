//moc
function createMocWallet () {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;



    var accountInfo = new Object();
    notifyClient(callid, 0, accountInfo);
}

function isValidAddress(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var address = paramsjson.address;


    //var isAddressValid = jingtum.Wallet.isValidAddress(data);



    var checkAddress = new Object();
    checkAddress.isAddressValid = isAddressValid;
    checkAddress.address = wallet.address;
    notifyClient(callid, 0, checkAddress);
}

function importWalletBySecret () {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var secret = paramsjson.secret;
    //    var wallet = jingtum.Wallet.fromSecret(privateKey);



    var accountInfo = new Object();
    notifyClient(callid, 0, accountInfo);

}

function SingTx(params){
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;



    var accountInfo = new Object();
    notifyClient(callid, 0, accountInfo);
}







function createJtWallet(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    //var wallet = jingtum.Wallet.generate();
    var accountInfo = new Object();
    let wallet = {"secret":"saj9auE499uFZYQupLKxMiPu8pWaY","address":"jL5nRP7dF4fDby93sZpDKsegH13zU8FbUz"}
    accountInfo.blockType = paramsjson.blockType;
    accountInfo.privatekey = wallet.secret;
    accountInfo.address = wallet.address;
    notifyClient(callid, 0, accountInfo);
}
/*
function retrieveWalletFromPk(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var privateKey = paramsjson.privateKey;
    var wallet = jingtum.Wallet.fromSecret(privateKey);
    var accountInfo = new Object();
    accountInfo.blockType = paramsjson.blockType;
    accountInfo.privatekey = wallet.secret;
    accountInfo.address = wallet.address;
    notifyClient(callid, 0, accountInfo);
}



function jtSingtx(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var tx = {};
    tx.Fee = paramsjson.fee;
    tx.Flags = 0;
    tx.Sequence = paramsjson.sequence;//通过balance接口拿到这个值
    tx.TransactionType = 'Payment';
    tx.Account = paramsjson.account;
    tx.Amount = {"value": paramsjson.value, "currency": paramsjson.currency, "issuer": paramsjson.issuer};
    tx.Destination = paramsjson.destination;
    var sign = jingtumLib.jingtum_sign_tx(tx, {"seed": paramsjson.seed});
    var callbackData = new Object();
    var signedTransaction = new Object();
    signedTransaction.rawTransaction = sign;
    callbackData.signedTransaction = signedTransaction;
    notifyClient(callid, 0, callbackData);
}
*/
//end jt


    //JS调用原生的部分的入口    JS(here)  ->  原生  ->JS
function notifyClient(callid, ret, extra) {
    var result = new Object();
    result.ret = ret;
    result.callid = callid;
    result.extra = extra;
    result.methodName = "notifyWeb3Result";
    var resultStr = toJsonString(result);
    window.client.notifyWeb3Result(resultStr);
}


function toJsonString(obj) {
    if (obj == undefined) {
        return "{}";
    } else {
        return JSON.stringify(obj);
    }
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


