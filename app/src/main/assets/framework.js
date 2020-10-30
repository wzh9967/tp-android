let storm3 = new Storm3(new Storm3.providers.HttpProvider('http://101.200.174.239:7545'));

async function getErc20Balance(params){
    var paramsjson = JSON.parse(params);
    let callid = paramsjson.callid;
    let contractAddress = paramsjson.contract;
    let currentAddress = paramsjson.address;
    let gasPrice = paramsjson.gasPrice;
    let result = new Object();
    try {
        let myContract = new storm3.fst.Contract(contractAbi, contractAddress, {
            from: currentAddress,
            gasPrice: gasPrice
        });
        let balance = await myContract.methods.balanceOf(currentAddress).call();
        result.balance = balance;
        notifyClient(callid, 0, result);
    } catch (e){
        result.err = e;
        notifyClient(callid, -1, result);
    }
}

async function sendErc20Transaction(params){
    var paramsjson = JSON.parse(params);
    let callid = paramsjson.callid;
    let contractAddress = paramsjson.contract;
    let currentAccount = paramsjson.address;
    let to = paramsjson.to;
    let secret = paramsjson.secret;
    let gasLimit = paramsjson.gasLimit
    let value = paramsjson.value
    let gasPrice = paramsjson.gasPrice
    console.log("gas price ="+gasPrice)
    let result = new Object();
    storm3.fst.getTransactionCount(currentAccount, storm3.fst.defaultBlock.pending).then(function(nonce){
        // 获取交易数据
        let txData = {
            nonce: storm3.utils.toHex(nonce++),
            gasLimit: storm3.utils.toHex(gasLimit),
            gasPrice: storm3.utils.toHex(gasPrice),
            // 注意这里是代币合约地址
            to: contractAddress,
            from: currentAccount,
            // 调用合约转账value这里留空
            value: '0x00',
            // data的组成，由：0x + 要调用的合约方法的function signature + 要传递的方法参数，每个参数都为64位(对transfer来说，第一个是接收人的地址去掉0x，第二个是代币数量的16进制表示，去掉前面0x，然后补齐为64位)
            data: '0x' + 'a9059cbb' + addPreZero(to.substr(2)) + addPreZero(storm3.utils.toHex(value).substr(2))
        }
        let tx = new Tx(txData);
        let privateKey = Buffer.from(secret.substr(2),'hex');
        tx.sign(privateKey);
        let serializedTx = tx.serialize().toString('hex');
        storm3.fst.sendSignedTransaction('0x' + serializedTx.toString('hex'), function(err, hash) {
            if (!err) {
                result.hash = hash;
                notifyClient(callid, 0, result);
            } else {
                result.err = err;
                console.log("err = "+err)
                notifyClient(callid, -1, result);
            }
        });
    });
}

async function sendTransaction(params) {
    var paramsjson = JSON.parse(params);
    let callid = paramsjson.callid;
    let address = paramsjson.address;
    let to = paramsjson.to;
    let secret = paramsjson.secret;
    let value = paramsjson.value
    let gasPrice = paramsjson.gasPrice
    let gasLimit = paramsjson.gasLimit
    let data = paramsjson.data
    let result = new Object();
    let txData = {
        "to": storm3.utils.toHex(to),
        "from": storm3.utils.toHex(address),
        "gasPrice": storm3.utils.toHex(gasPrice),
        "gas": storm3.utils.toHex(gasLimit),
        "value": storm3.utils.toHex(value),
        "data": storm3.utils.toHex(data),
    }
    try {
        let tx = await storm3.fst.accounts.signTransaction(txData,secret)
        let raw = tx.rawTransaction
        console.log(raw)
        storm3.fst.sendSignedTransaction(raw, function(err, hash) {
            if (!err) {
                result.hash = hash;
                console.log("hash = "+hash);
                notifyClient(callid, 0, result);
            } else {
                result.err = err;
                console.log("err = "+err)
                notifyClient(callid, -1, result);
            }
        });
    } catch (err){
        console.log("err = "+err)
        result.err = err;
        notifyClient(callid, -1, result);
    }
}
async function getBalance(params){
    var paramsjson = JSON.parse(params);
    let callid = paramsjson.callid;
    let address = paramsjson.address;
    var result = new Object();
    try {
        let balance = await storm3.fst.getBalance(address);
        result.balance = balance;
        notifyClient(callid, 0, result);
    } catch (UnhandledPromiseRejectionWarning){
        notifyClient(callid, -1, result);
    }
}
async function getGasPrice(params){
    var paramsjson = JSON.parse(params);
    let callid = paramsjson.callid;
    var result = new Object();
    try {
        let GasPrice = await storm3.fst.getGasPrice();
        result.GasPrice = GasPrice;
        notifyClient(callid, 0, result);
    } catch (UnhandledPromiseRejectionWarning){
        notifyClient(callid, -1, result);
    }
}

/*

 function getGasPrice(params){
    var paramsjson = JSON.parse(params);
    let callid = paramsjson.callid;
    var result = new Object();
    let fstUtil = new fst_transaction()
    fstUtil.init();
    fstUtil.getGasPrice().then((GasPrice,err) => {
            if(!err){
                result.GasPrice = GasPrice;
                notifyClient(callid, 0, result);
            }
            result.err = err;
            notifyClient(callid, -1, result);
        }
    )
}

                console.log("url = " + this.url);
                getNodeFromNative((result,err) =>{
                    if(err === 0){
                     this.url = result;
                    }
                });
                console.log("url = " + this.url);
*/
