const Storm3 = require("storm3");
let storm3 = new Storm3(new Storm3.providers.HttpProvider('http://101.200.174.239:7545'));
const contractAbi = require("./contract_abi")
const Tx = require('ethereumjs-tx')

//test 1 ： address    0x981d4bc976c221b3b42270be6dcab72d37d2e0cd    password     0x1a0ad31a04ed4dbcec91a8a54c0d87187b50ab60e03139f404533332e9b31917
//test 2 :  address    0xb4860ef01adae0f0714776d1c35e7ad4b0937b79   password      0x1642d3005ab9ce6efb682d2ba3f4d54deebd62bb5ba80ee802c3495e78d0eb84

async function getBalance(params){
    let paramsjson = JSON.parse(params);
    let callid = paramsjson.callid;
    let contractAddress = paramsjson.contractAddress;
    let currentAddress = paramsjson.currentAddress;
    let gasPrice = paramsjson.gasPrice;
    let result = new Object();
    try {
        let myContract = new storm3.fst.Contract(contractAbi, contractAddress, {
            from: currentAddress,
            gasPrice: gasPrice
        });
        let balance = await myContract.methods.balanceOf(contractAddress).call();
        result.balance = balance;
        notifyClient(callid, 0, result);
    } catch (e){
        result.err = e;
        notifyClient(callid, -1, result);
    }
}

async function getTransactionCount(params){
    let paramsjson = JSON.parse(params);
    let callid = paramsjson.callid;
    let contractAddress = paramsjson.contractAddress;
    let currentAddress = paramsjson.currentAddress;
    let to = paramsjson.to;
    let secret = paramsjson.secret;
    let gasLimit = paramsjson.gasLimit
    let value = paramsjson.value
    let gasPrice = paramsjson.gasPrice
    let result = new Object();
    let toAddress = to.substring(2);
    storm3.fst.getTransactionCount(currentAddress, storm3.fst.defaultBlock.pending).then(function(nonce){
        // 获取交易数据
        let txData = {
            nonce: storm3.utils.toHex(nonce++),
            gasLimit: storm3.utils.toHex(gasLimit),
            gasPrice: storm3.utils.toHex(gasPrice),
            // 注意这里是代币合约地址
            to: contractAddress,
            from: currentAddress,
            // 调用合约转账value这里留空
            value: '0x00',
            // data的组成，由：0x + 要调用的合约方法的function signature + 要传递的方法参数，每个参数都为64位(对transfer来说，第一个是接收人的地址去掉0x，第二个是代币数量的16进制表示，去掉前面0x，然后补齐为64位)
            data: '0x' + 'a9059cbb' + addPreZero(toAddress) + addPreZero(storm3.utils.toHex(value).substr(2))
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
                notifyClient(callid, -1, result);
            }
        });
    });
}




/*
/*
function toJsonString(obj) {
    if (obj == undefined) {
        return "{}";
    } else {
        return JSON.stringify(obj);
    }
}
async function sendErc20Transaction(params){
    let paramsjson = JSON.parse(params);
    let callid = paramsjson.callid;
    let contractAddress = paramsjson.contractAddress;
    let currentAddress = paramsjson.currentAddress;



    let result = new Object();
    result.callid = callid;
    notifyClient(callid, 0, result);
}

// 合约地址
let contractAddress = "0xba753eb6cc555c867e4e7a554f3e13018a9c075b";
// 账号
let currentAccount = "0x981d4bc976c221b3b42270be6dcab72d37d2e0cd";
let currentAccount1 = "0xb4860ef01adae0f0714776d1c35e7ad4b0937b79";
// 定义合约
let myContract = new storm3.fst.Contract(contractAbi, contractAddress, {
    from: currentAccount,
    gasPrice: '10000000000'
});

// 查询币余额
//storm3.fst.getBalance(currentAccount).then(console.log);

// 查看某个账号的代币余额
myContract.methods.balanceOf(currentAccount1).call().then(data =>{
    console.log("erc20 balance "+data);
});

myContract.methods.name().call({from: currentAccount}, function(error, result){
    if(!error) {
        console.log("token name :"+result);
    } else {
        console.log(error);
    }
});

// 获取代币符号
myContract.methods.symbol().call({from: currentAccount}, function(error, result){
    if(!error) {
        console.log("token flag :"+result);
    } else {
        console.log(error);
    }
});

// 获取代币总量
myContract.methods.totalSupply().call({from: currentAccount1}, function(error, result){
    if(!error) {
        console.log("token amount "+result);
    } else {
        console.log(error);
    }
});



storm3.fst.getTransactionCount(currentAccount, storm3.fst.defaultBlock.pending).then(function(nonce){
    // 获取交易数据
    let txData = {
        nonce: storm3.utils.toHex(nonce++),
        gasLimit: storm3.utils.toHex(99000),
        gasPrice: storm3.utils.toHex(10e9),
        // 注意这里是代币合约地址
        to: contractAddress,
        from: currentAccount,
        // 调用合约转账value这里留空
        value: '0x00',
        // data的组成，由：0x + 要调用的合约方法的function signature + 要传递的方法参数，每个参数都为64位(对transfer来说，第一个是接收人的地址去掉0x，第二个是代币数量的16进制表示，去掉前面0x，然后补齐为64位)
        data: '0x' + 'a9059cbb' + addPreZero('b4860ef01adae0f0714776d1c35e7ad4b0937b79') + addPreZero(storm3.utils.toHex(100000000).substr(2))
    }
    let tx = new Tx(txData);
    let privateKey = Buffer.from('1a0ad31a04ed4dbcec91a8a54c0d87187b50ab60e03139f404533332e9b31917','hex');
    tx.sign(privateKey);
    let serializedTx = tx.serialize().toString('hex');
    storm3.fst.sendSignedTransaction('0x' + serializedTx.toString('hex'), function(err, hash) {
        if (!err) {
            console.log(hash);
        } else {
            console.error(err);
        }
    });
});
 */