package com.hazelcast.certification.process;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * Created by michi on 10.06.17.
 */
public class MemberMain {
    public static void main(String[] args) {
        System.setProperty("hazelcast.partition.count","9173");
        Config config = new XmlConfigBuilder(LoaderMain.class.getClassLoader().getResourceAsStream("hazelcast.xml")).build();
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
    }
}
