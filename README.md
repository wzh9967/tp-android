# 关于联盟链钱包

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

This is a tp-android based Android ink fst Wallet tool. We can use it to manage the fst Wallet, view the balance and transaction records, transfer, and erc20 transfer, support node settings, and switch more convenient nodes.

这是一个基于TP-Android 的Android 墨客联盟链钱包工具。我们可以用它来管理联盟链钱包，查看余额和交易记录，转账，以及erc20转账，支持节点设置，和切换更便捷的联盟节点。

------

### 测试钱包

项目基于社区链创建，关于社区链相关操作均可以直接在项目中进行。

```
社区链测试钱包一  ：  0x981d4bc976c221b3b42270be6dcab72d37d2e0cd
密钥       ：  0x1a0ad31a04ed4dbcec91a8a54c0d87187b50ab60e03139f404533332e9b31917

社区链测试钱包二  ：  0xb4860ef01adae0f0714776d1c35e7ad4b0937b79
密钥       ：  0x1642d3005ab9ce6efb682d2ba3f4d54deebd62bb5ba80ee802c3495e78d0eb84

均拥有已经配置的Erc20币，供测试使用。
```



### 配置流程

也可以按照以下步骤轻松改造其为联盟链钱包，同时熟悉相关配置。

-  修改Erc20币配置文件【assert/currency.json】

  currency.json配置文件将初始化为钱包页面的货币列表。

  默认第一位为基础货币，需要把相关信息从测试链的mfc信息，修改为moab信息。

  其他为Erc20币的配置，按相同格式替换为联盟链Ecr20币种即可。

  ![image-20201106162622108](https://github.com/wzh9967/tp-android/blob/dev1/README_PICTURE/image-20201106162622108.png)

-  修改节点的配置文件【assert/publicNode.json】

  publicNode.json配置文件将初始化为节点设置页面的节点列表。

![image-20201106162844467](https://github.com/wzh9967/tp-android/blob/dev1/README_PICTURE/image-20201106162844467.png)

- 修改Contant中的初始化配置。

![image-20201106170929861](https://github.com/wzh9967/tp-android/blob/dev1/README_PICTURE/image-20201106170929861.png)

> ```
> 注意：
> 
> *关于GasPrice 无论社区链节点还是联盟链节点 通过getGasPrice 得到的值均为 10 ^ 10 。和联盟链默认的GasPrice 相同。故默认交易相关的GasPrice为 通过storm3.fst.getGasPrice()获取的值。
> *和链的交互通过storm3实现，js放置在assert目录下。
> ```



### 钱包使用

> 1. 创建钱包：钱包备份只显示助记词备份（支持密钥备份，但输入太过繁琐，体验不佳故没有实装），相应密钥可以在钱包管理页面查看，记录。
>
>    
>
> 2. 导入钱包：支持密钥和助记词导入。可在钱包管理界面导入。
>
>    
>
> 3. 钱包切换： 通过钱包页面左上角小菜单手动切换，
>
>    ​					导入新钱包自动切换，
>
>    ​					查看交易记录页面小菜单手动切换。
>
>    
>
> 4. 转账：主页面【转账/收款】按钮均对应原生货币，进入erc20货币详情页后，【转账/收款】按钮对应相应货币转账。
>
>    
>
> 5. 收款：扫描二维码后，扫描方自动填写转账的地址和货币数量信息。并非扫描完场当时自动转账。
>
>    
>
> 6. 修改钱包信息（密码，备注，名称）：钱包管理页面。
>
>    
>
> 7. 查看密钥：钱包管理页面。
>
>    
>
> 8. 节点设置：切换到不可使用的节点将会影响余额的获取，转账，查看交易详情 。
>
>    ​					节点延迟仅仅代表网络的连通性，而非节点的可用。
>
>    ​					长按对应节点，跳出删除提示，默认节点（配置文件中）不可删除。



### Dapp

> 发现模块支持Dapp使用，其接口格式请参考[这里](https://github.com/TP-Lab/tp-js-sdk#MOAC)。





