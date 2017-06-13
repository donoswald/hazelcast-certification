package com.hazelcast.certification.server.process.executorService;

import com.hazelcast.certification.business.ruleengine.RuleEngine;
import com.hazelcast.certification.domain.FraudDetectionConstants;
import com.hazelcast.certification.domain.Result;
import com.hazelcast.certification.domain.Transaction;
import com.hazelcast.certification.server.process.FraudDetection;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by michi on 01.06.17.
 */
public class FraudDetectionImpl extends FraudDetection {
    private final static ILogger log = Logger.getLogger(FraudDetectionImpl.class);
    private int numberOfWorkers;


    private IMap<String, List<Transaction>> allHistory;
    private ILock historicalTransactionLock;
    private AtomicBoolean checkLock = new AtomicBoolean(true);

    public FraudDetectionImpl() {
        this.init();
    }

    private void init() {
        ClientConfig config = new XmlClientConfigBuilder(getClass().getClassLoader().getResourceAsStream("hazelcast-client.xml")).build();
        HazelcastInstance instance = HazelcastClient.newHazelcastClient(config);
        historicalTransactionLock = instance.getLock(FraudDetectionConstants.HIST_TRX_LOCK);
        allHistory = instance.getMap(FraudDetectionConstants.HIST_TRX_MAP_NAME);
        loadProperties();
    }

    protected void startFraudDetection() {
        for (int i = 0; i < numberOfWorkers; i++) {

            Thread checker = new Thread() {
                @Override
                public void run() {
                    while (!Thread.interrupted()) {
                        if (!locked()) {
                            checkLock.set(false);
                            processDetection();
                        }
                    }

                }
            };
            checker.setDaemon(true);
            checker.start();
        }

    }

    private void processDetection() {
        Transaction transaction = nextTrx();



        List<Transaction> histTrxs = allHistory.get(transaction.getCreditCardNumber());


        if (histTrxs != null && !histTrxs.isEmpty()) {
            RuleEngine ruleEngine = new RuleEngine(transaction, histTrxs);
            try {
                ruleEngine.executeRules();
            } catch (ArithmeticException e) {
                log.severe("error executing rules", e);
            }
            Result result = new Result();
            result.setCreditCardNumber(transaction.getCreditCardNumber());
            result.setFraudTransaction(ruleEngine.isFraudTxn());
            registerResult(result);
        }
        if (histTrxs != null) {
            histTrxs.add(transaction);
            allHistory.set(transaction.getCreditCardNumber(), histTrxs);
        }


    }


    private Transaction nextTrx() {
        try {
            return getNextTransaction();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean locked() {
        return checkLock.get() && historicalTransactionLock.isLocked();
    }

    private void loadProperties() {
        String propFileName = "FraudDetection.properties";
        InputStream stream = getClass().getClassLoader().getResourceAsStream(
                propFileName);
        if (null == stream) {
            try {
                throw new FileNotFoundException("Property file " + propFileName
                        + " not found in the classpath");
            } catch (FileNotFoundException e) {
                log.severe(e);
            }
        }
        try {
            Properties properties = new Properties();
            properties.load(stream);

            String numberOfWorkersString = properties.getProperty("NumberOfWorkers");
            if (numberOfWorkersString != null) {
                numberOfWorkers = Integer.valueOf(numberOfWorkersString);
            } else {
                numberOfWorkers = 8;
            }

        } catch (IOException e) {
            log.severe(e);
        }
    }
}
