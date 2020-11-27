package com.tokenbank.net.query;

import com.tokenbank.base.WCallback;

public interface QueryDataFromNet {
    /**
     * 从浏览器获取Erc20交易记录
     * @param PageSize
     * @param Decimal
     * @param contract
     * @param address
     * @param callback
     */
     void queryErc20TransactionList(int PageSize, int Decimal, final String contract, final String address, final WCallback callback);

    /**
     * 从浏览器获取交易记录
     * @param pagesize
     * @param address
     * @param callback
     */
     void queryTransactionList(int pagesize, final String address, final WCallback callback);
}
