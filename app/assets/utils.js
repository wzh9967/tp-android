let storm3 = new Storm3(new Storm3.providers.HttpProvider('http://101.200.174.239:7545'));
async function checkTransaction(hash){
    var receipt = await storm3.fst.getTransactionReceipt(hash)
    console.log(receipt);
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
