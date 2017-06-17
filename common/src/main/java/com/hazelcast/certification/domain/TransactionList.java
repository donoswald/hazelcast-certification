package com.hazelcast.certification.domain;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michi on 16.06.17.
 */
public class TransactionList extends ArrayList<Transaction> implements DataSerializable {
    public TransactionList(){}
    public TransactionList(List<Transaction> transactions) {
        super(transactions);
    }

    @Override
    public void writeData(ObjectDataOutput oda) throws IOException {
        oda.writeInt(this.size());
        for(Transaction transaction : this){
            transaction.writeData(oda);
        }
    }

    @Override
    public void readData(ObjectDataInput odi) throws IOException {
      int size = odi.readInt();
      for( int i=0;i<size;i++){
          Transaction transaction =  new Transaction();
          transaction.readData(odi);
          this.add(transaction);
      }
    }
}
