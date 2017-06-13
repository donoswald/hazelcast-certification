package com.hazelcast.certification.process;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;

import java.io.File;

/**
 * Created by michi on 05.06.17.
 */
public class LoaderMain {
    private final static ILogger log = Logger.getLogger(LoaderMain.class);

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("hazelcast.partition.count","2791");
        Config config = new XmlConfigBuilder(LoaderMain.class.getClassLoader().getResourceAsStream("hazelcast.xml")).build();
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);

        HistoricalTransactionLoaderImpl loader = new HistoricalTransactionLoaderImpl(instance);
        loader.loadHistoricalTransactions();
        log.info("Transactions loaded");
    }
}
