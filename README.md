# Fraud Detection Application
## Installation Instructions
Thre Fraud Detection application consists of the following modules
1. common
1. loader
1. server
1. generator

### Common Module
The common module is not really a module, because it is not deployable on its own.
It contains the domain classes and the util classes to be used by the other modules.

### Loader Module
The loader module has two different starting points
* MemberMain, which participates in the cluster of the loaded transactions
* LoaderMain, which initializes a distributed map containing the 600 Mio transactions

There are the two scripts
1. membmer.sh
1. loader.sh

for starting these processes.

First run **member.sh** on each member participating on the cluster. Be sure to provide enough space on the started members
to hold the whole 600 Mio transactions.
In a second step start the **loader.sh**, which than initializes the cluster with the data.

In the loader module there is an additional script **size.sh** which prints out the size of a transaction. This size is
base of all calculations later for latency, cluster size and expected performance.

### Server Module
The server connects to the cluster and listens on incomming transactions. It has a configurable number of worker threads
which then check the incomming thransactions for frauds. They will get the historical transactions of
the given credit cards from the cluster to do the check.
There is a **server.sh** script.
You can start as many servers you like.

### Generater Module
The generater module generates the transactions. Simply run the **generator.sh** script to run it.
