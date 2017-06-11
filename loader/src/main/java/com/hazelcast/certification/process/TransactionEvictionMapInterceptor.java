package com.hazelcast.certification.process;

import com.hazelcast.certification.domain.Transaction;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiMap;
import com.hazelcast.map.MapInterceptor;
import org.joda.time.DateTime;

import java.util.*;

/**
 * Created by michi on 09.06.17.
 */
public class TransactionEvictionMapInterceptor implements MapInterceptor {
    private IMap<String, List<Transaction>> original;

    public TransactionEvictionMapInterceptor(IMap<String, List<Transaction>> original) {
        this.original = original;
    }

    @Override
    public Object interceptGet(Object o) {
        List<Transaction> transactions = (List<Transaction>) o;
        if (transactions == null || transactions.isEmpty()) {
            return new ArrayList<>();
        }
        String creditCardNumber = transactions.get(0).getCreditCardNumber();
        return evict(creditCardNumber, transactions);
    }

    private List<Transaction> evict(String creditCardNumber, List<Transaction> transactions) {
        for (Iterator<Transaction> it = transactions.iterator(); it.hasNext(); ) {
            Transaction histTransaction = it.next();
            DateTime trxDate = new DateTime(histTransaction.getTimeStamp());
            if (trxDate.plusDays(90).isBefore(DateTime.now())) {
                it.remove();
            }
        }
        original.set(creditCardNumber, transactions);
        return transactions;
    }

    @Override
    public void afterGet(Object o) {

    }

    @Override
    public Object interceptPut(Object o, Object o1) {
        return null;
    }


    @Override
    public void afterPut(Object o) {

    }

    @Override
    public Object interceptRemove(Object o) {
        return null;
    }

    @Override
    public void afterRemove(Object o) {

    }
}
