package com.hazelcast.certification.process;

import com.hazelcast.certification.domain.FraudDetectionConstants;
import com.hazelcast.certification.domain.Transaction;
import com.hazelcast.certification.util.TransactionsUtil;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.*;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;

import java.util.List;

/**
 * Created by michi on 02.06.17.
 */
public class HistoricalTransactionLoaderImpl implements HistoricalTransactionsLoader {
    private final static ILogger log = Logger.getLogger(HistoricalTransactionLoaderImpl.class);
    private static final int MAX_CC_COUNT = 30 * 1000 *  1000;

    private static final int TRX_COUNT = 20;

    private TransactionsUtil trxUtil = new TransactionsUtil();
    private HazelcastInstance instance;

    public HistoricalTransactionLoaderImpl(HazelcastInstance instance) {
        this.instance = instance;
    }

    @Override
    public void loadHistoricalTransactions() {
        ILock lock = instance.getLock(FraudDetectionConstants.HIST_TRX_LOCK);
        lock.lock();
        try {

            IMap<String, List<Transaction>> historicalTransactions = instance.getMap(FraudDetectionConstants.HIST_TRX_MAP_NAME);
            historicalTransactions.addInterceptor(new TransactionEvictionMapInterceptor());
            for (int i = 0; i < MAX_CC_COUNT; i++) {
                String creditCardNumber = trxUtil.generateCreditCardNumber(i);
                List<Transaction> transactions = trxUtil.createAndGetCreditCardTransactions(creditCardNumber, TRX_COUNT);
                historicalTransactions.set(creditCardNumber,transactions);
            }
        } finally {
            lock.unlock();
        }

    }

}
