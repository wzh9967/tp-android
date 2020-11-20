let Storm3 = require("storm3");
let storm3 = new Storm3(initStorm3());
function initStorm3(){
    return new Storm3.providers.HttpProvider('http://101.200.174.239:7545');
}

//test 1 ： address    0x981d4bc976c221b3b42270be6dcab72d37d2e0cd    password     0x1a0ad31a04ed4dbcec91a8a54c0d87187b50ab60e03139f404533332e9b31917
//test 2 :  address    0xb4860ef01adae0f0714776d1c35e7ad4b0937b79   password      0x1642d3005ab9ce6efb682d2ba3f4d54deebd62bb5ba80ee802c3495e78d0eb84

async function sendansaction(params) {
    if(storm3){

    }
    let paramsjson = JSON.parse(params);
    let callid = paramsjson.callid;
    let address = paramsjson.address;
    let to = paramsjson.to;
    let secret = paramsjson.secret;
    let value = paramsjson.value
    let gasPrice = paramsjson.gasPrice
    let result = new Object();
    let txData = {
        "to": to,
        "from": address,
        "gasPrice": gasPrice,
        "gas": "21000",
        "value": value,
        "data": "",
        "gasLimit":21000
    }
    try {
        let tx = await storm3.fst.accounts.signTransaction(txData,secret)
        let raw = tx.rawTransaction
        let hash = await storm3.fst.sendSignedTransaction(raw)
        result.hash = hash;
        notifyClient(callid, 0, result);
    } catch (e){
        result.err = e;
        notifyClient(callid, 0, result);
    }
}
async function getbalance (params) {
    let paramsjson = JSON.parse(params);
    let callid = paramsjson.callid;
    let address = paramsjson.address;
    let balance = getBalance(address);
    var result = new Object();
    try {
        let balance = await storm3.fst.getBalance(address);
        result.balance = balance;
        notifyClient(callid, 0, result);
    } catch (UnhandledPromiseRejectionWarning){
        notifyClient(callid, -1, result);
    }
}
