let storm3 = new Storm3(new Storm3.providers.HttpProvider('http://101.200.174.239:7545'));

    function sendTransaction(params){
        let paramsjson = JSON.parse(params);
        let url = paramsjson.url;
        let secret = paramsjson.secret;
        let callid = paramsjson.callid;
        let result = new Object();
        let fstUtils = new transaction.fst_transaction(url);
        fstUtils.init();
        let txData = fstUtils.SignTransaction(params);
        fstUtils.sendtransaction(txData,secret).then((hash) =>{
            result.hash = hash;
            notifyClient(callid, 0, result);
        }).catch((err) =>{
            result.err = err;
            notifyClient(callid, 0, result);
        })
    }

    function getGasPrice(params){
        let paramsjson = JSON.parse(params);
        let callid = paramsjson.callid;
        let url = paramsjson.url;
        var result = new Object();
        let fstUtil = new transaction.fst_transaction(url)
        fstUtil.init();
        fstUtil.getGasPrice().then((GasPrice ,err) =>{
            if(!err){
                result.GasPrice = GasPrice;
                notifyClient(callid, 0, result);
            } else {
                result.err = err;
                notifyClient(callid, -1, result);
            }
        })
    }

    function getBalance(params){
        let paramsjson = JSON.parse(params);
        let callid = paramsjson.callid;
        let address = paramsjson.address;
        let url = paramsjson.url;
        let result = new Object();
        let fstUtil = new transaction.fst_transaction(url)
        fstUtil.init();
        fstUtil.getBalance(address).then((balance) =>{
                console.log(balance);
                result.balance = balance;
                notifyClient(callid, 0, result);
        }).catch((err) =>{
            console.log(err);
            result.err = err;
            notifyClient(callid, -1, result);
        })
    }

    function getErc20Balance(params){
        let paramsjson = JSON.parse(params);
        let callid = paramsjson.callid;
        let contract = paramsjson.contract;
        let address = paramsjson.address;
        let url = paramsjson.url;
        let result = new Object();
        let fstUtils = new transaction.fst_erc20_transaction(url,contract,address);
        fstUtils.getErc20Balance(address).then((balance) =>{
            result.balance = balance;
            notifyClient(callid, 0, result);
        }).catch((err) =>{
            result.err = err;
            notifyClient(callid, -1, result);
        })
    }

    function sendErc20Transaction(params){
        let paramsjson = JSON.parse(params);
        let secret = paramsjson.secret;
        let callid = paramsjson.callid;
        let url = paramsjson.url;
        let contract = paramsjson.contract;
        let address = paramsjson.address;
        let result = new Object();
        let fstUtils = new transaction.fst_erc20_transaction(url,contract,address);
        fstUtils.SignErc20Transaction(params).then((txData) =>{
            fstUtils.sendErc20Transaction(txData,secret).then((hash) =>{
                result.hash = hash;
                notifyClient(callid, 0, result);
            }).catch((err)=>{
                result.err = err;
                notifyClient(callid, -1, result);
            })
        }).catch((err)=>{
            result.err = err;
            console.log(err);
            notifyClient(callid, -2, result);
        })
    }


    /*
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
    */