package karatCodePractice.transaction;

import java.util.*;

/*
We are developing a payment transaction monitoring system that tracks accounts and their transactions.

The system can compute each account's current balance and basic statistics.
Definitions: An "account" has a unique accountid and an owner name.
* A "transaction" represents money moving in or out of an account. - CREDIT increases the account balance. - DEBIT decreases the account balance.
* "AccountManager" manages accounts and transactions and provides balance-related methods.

To begin with, we present you with two tasks:
1.1) Read through and understand the code below. Please take as much time as necessary, and feel free to run it.
1.2) The test for AccountManager is not passing due to a bug in the code. Make the necessary changes to AccountManager to fix the bug. */

/* We are extending our payment transaction monitoring system to support basic analytics over transactions.
For this task, we want to calculate the average transaction amount per account.
2) Implement the function getAverageTransactionAmountByAccount in AccountManager that returns the average transaction amount for each account.
Requirements: - The result should associate each accountld with the average amount of its transactions.
- Both CREDIT and DEBIT transactions should be considered.
- Transaction amounts should be treated as absolute values when calculating averages. Accounts with no transactions should not appear in the result.
- Transactions always refer to valid accounts.
To assist you in testing this new function, we have provided the testGetAverageTransactionAmountByAccount test.
*/

/* We are extending our payment transaction monitoring system to calculate transaction fees based on business rules.
Each account is charged transaction fees as follows:
- The first 3 transactions for an account are FREE.
- From the 4th transaction onward:
    - CREDIT transactions cost a flat fee of $1.
    - DEBIT transactions cost a flat fee of $2.
Transactions must be processed in chronological order (by timestamp). Fees are applied per account independently.

3) Implement getTransactionFees() in AccountManager. The function should return a map associating each accountld with the total transaction fees charged to that account.
To assist you in testing this new function, we have provided the testGetTransaction Fees function.
*/

enum TransactionType {
    CREDIT, DEBIT
}

class Account {
    int accountId;
    String ownerName;

    Account(int accountId, String ownerName) {
        this.accountId = accountId;
        this.ownerName = ownerName;
    }
}

class Transaction {
    int transactionId;
    int accountId;
    TransactionType type;
    double amount; // Always positive
    long timestampSec;

    Transaction(int transactionId, int accountId, TransactionType type, double amount, long timestampSec) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestampSec = timestampSec;
    }

    @Override
    public String toString() {
        return "TEntry: " + accountId + " : " + amount;
    }
}

class AccountManager {
    Map<Integer, Account> accounts = new HashMap<>();
    List<Transaction> transactions = new ArrayList<>();

    // Add account
    void addAccount(Account acc) {
        accounts.put(acc.accountId, acc);
    }

    // Add transaction
    void addTransaction(Transaction tx) {
        transactions.add(tx);
    }

    // 1) FIXED BUG - GET BALANCE
    // CREDIT -> increase
    // DEBIT -> decrease
    double getBalance(int accountId) {
        double balance = 0.0;
        for (Transaction tx : transactions) {
            if (tx.accountId == accountId) {
                if (TransactionType.CREDIT == tx.type) {
                    balance += tx.amount;
                } else {
                    balance -= tx.amount;
                }
            }
        }
        return balance;
    }

    Map<Integer, Double> getAverageTransactionAmountByAccount() {
        Map<Integer, Double> avgMap = new HashMap<>();
        Map<Integer, Double> totalAmountMap = new HashMap<>();
        Map<Integer, Integer> countMap = new HashMap<>();

        for (Transaction tx : transactions) {
            totalAmountMap.merge(tx.accountId, Math.abs(tx.amount), Double::sum);
            countMap.merge(tx.accountId, 1, Integer::sum);
        }

        for (Integer accId : countMap.keySet()) {
            avgMap.put(accId, totalAmountMap.get(accId) / countMap.get(accId));
        }
        return avgMap;
    }

    Map<Integer, Double> getTransactionFees() {
        Map<Integer, Double> feesMap = new HashMap<>();
        Map<Integer, Integer> transactionCounter = new HashMap<>();

        List<Transaction> sortedTransactions = new ArrayList<>(transactions);
        sortedTransactions.sort(Comparator.comparingLong(tx -> tx.timestampSec));

        for (Transaction tx : sortedTransactions) {
            int count = transactionCounter.getOrDefault(tx.accountId, 0);
            if (count >= 3) {
                double fees = tx.type == TransactionType.CREDIT ? 1.0 : 2.0;
                feesMap.merge(tx.accountId, fees, Double::sum);
            }
            transactionCounter.put(tx.accountId, count + 1);
        }
        return feesMap;
    }

    List<Integer> getSuspiciousAccounts() {
        List<Integer> suspicious = new ArrayList<>();
        Map<Integer, List<Long>> suspiciousMap = new HashMap<>();

        for (Transaction tx : transactions) {
            if (tx.type == TransactionType.DEBIT && tx.amount >= 50.0) {
                suspiciousMap.putIfAbsent(tx.accountId, new ArrayList<>());
                suspiciousMap.get(tx.accountId).add(tx.timestampSec);
                /*suspiciousMap
                        .computeIfAbsent(tx.accountId, k -> new ArrayList<>())
                        .add(tx.timestampSec);*/
            }
        }

        for (Map.Entry<Integer, List<Long>> entry : suspiciousMap.entrySet()) {
            List<Long> suspiciousTimes = entry.getValue();
            suspiciousTimes.sort(Comparator.naturalOrder());
            if (isSuspicious(suspiciousTimes)) {
                suspicious.add(entry.getKey());
            }
        }
        return suspicious;
    }

    boolean isSuspicious(List<Long> suspiciousTimes) {
        if (suspiciousTimes.size() > 2) {
            for (int i = 0; i + 2 < suspiciousTimes.size(); i++) {
                if (suspiciousTimes.get(i + 2) - suspiciousTimes.get(i) <= 60) {
                    return true;
                }
            }
        }
        return false;
    }
}

public class TransactionSolution {
    public static void main(String[] args) {
        testGetBalance_basic();
        testGetBalance_multipleAccounts();
        testGetAverageTransactionAmountByAccount();
        testGetTransactionFees();
        testGetSuspiciousAccounts();
        System.out.println("\nAll tests passed successfully.");
    }

    static void assertAlmost(double expected, double actual, double eps) {
        if (Math.abs(expected - actual) > eps) {
            throw new RuntimeException("Test Failed -> Expected: " + expected + " Actual: " + actual);
        }
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Test Failed -> " + message);
        }
    }

    static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new RuntimeException("Test Failed -> " + message);
        }
    }

    static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new RuntimeException("Test Failed -> Expected: " + expected + " Actual: " + actual);
        }
    }

    // TEST 1
    public static void testGetBalance_basic() {
        System.out.println("Running testGetBalance_basic");
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));
        mgr.addTransaction(new Transaction(101, 1, TransactionType.CREDIT, 100.0, 1000));
        mgr.addTransaction(new Transaction(102, 1, TransactionType.DEBIT, 30.0, 1010));
        mgr.addTransaction(new Transaction(103, 1, TransactionType.DEBIT, 20.0, 1020));
        mgr.addTransaction(new Transaction(104, 1, TransactionType.CREDIT, 10.0, 1030));
        // 100-30-20 + 10 = 60
        assertAlmost(60.0, mgr.getBalance(1), 0.0001);
    }

    // TEST 2
    public static void testGetBalance_multipleAccounts() {
        System.out.println("Running testGetBalance_multipleAccounts");
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));
        mgr.addAccount(new Account(2, "Bob"));
        mgr.addTransaction(new Transaction(201, 1, TransactionType.CREDIT, 50.0, 2000));
        mgr.addTransaction(new Transaction(202, 2, TransactionType.CREDIT, 80.0, 2005));
        mgr.addTransaction(new Transaction(203, 1, TransactionType.DEBIT, 10.0, 2010));
        mgr.addTransaction(new Transaction(204, 2, TransactionType.DEBIT, 5.5, 2015));
        mgr.addTransaction(new Transaction(205, 2, TransactionType.DEBIT, 14.5, 2020));
        // 50-1040
        assertAlmost(40.0, mgr.getBalance(1), 0.0001);
        //80-5.5-14.5 = 60
        assertAlmost(60.0, mgr.getBalance(2), 0.0001);
    }

    // TEST 3
    public static void testGetAverageTransactionAmountByAccount() {
        System.out.println("Running testGetAverage TransactionAmountByAccount");
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));
        mgr.addAccount(new Account(2, "Bob"));
        mgr.addAccount(new Account(3, "Charlie"));

        // Account 1
        mgr.addTransaction(new Transaction(101, 1, TransactionType.CREDIT, 100.0, 1000));
        mgr.addTransaction(new Transaction(102, 1, TransactionType.DEBIT, 30.0, 1010));
        mgr.addTransaction(new Transaction(103, 1, TransactionType.DEBIT, 20.0, 1020));
        mgr.addTransaction(new Transaction(104, 1, TransactionType.CREDIT, 10.0, 1030));

        // Account 2
        mgr.addTransaction(new Transaction(201, 2, TransactionType.CREDIT, 80.0, 2005));
        mgr.addTransaction(new Transaction(202, 2, TransactionType.DEBIT, 5.5, 2015));
        mgr.addTransaction(new Transaction(203, 2, TransactionType.DEBIT, 14.5, 2020));

        Map<Integer, Double> avg = mgr.getAverageTransactionAmountByAccount();
        assertAlmost(40.0, avg.get(1), 0.0001);
        assertAlmost(33.3333, avg.get(2), 0.0001);
        assertFalse(avg.containsKey(3), "Account 3 should not exist");
    }

    // TEST 4
    public static void testGetTransactionFees() {
        System.out.println("Running testGetTransactionFees");
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));
        mgr.addAccount(new Account(2, "Bob"));
        mgr.addAccount(new Account(3, "Jane"));
        // Account 1
        mgr.addTransaction(new Transaction(1, 1, TransactionType.CREDIT, 100.0, 1000));
        mgr.addTransaction(new Transaction(2, 1, TransactionType.DEBIT, 20.0, 1010));
        mgr.addTransaction(new Transaction(3, 1, TransactionType.CREDIT, 10.0, 1020));
        mgr.addTransaction(new Transaction(4, 1, TransactionType.DEBIT, 5.0, 1030));
        mgr.addTransaction(new Transaction(5, 1, TransactionType.CREDIT, 7.0, 1040));
        // Account 2
        mgr.addTransaction(new Transaction(6, 2, TransactionType.DEBIT, 50.0, 2000));
        mgr.addTransaction(new Transaction(7, 2, TransactionType.DEBIT, 10.0, 2010));
        mgr.addTransaction(new Transaction(8, 2, TransactionType.CREDIT, 20.0, 2020));
        mgr.addTransaction(new Transaction(9, 2, TransactionType.DEBIT, 5.0, 2030));
        // Account 3
        mgr.addTransaction(new Transaction(26, 3, TransactionType.DEBIT, 50.0, 2000));
        mgr.addTransaction(new Transaction(27, 3, TransactionType.DEBIT, 10.0, 2010));
        mgr.addTransaction(new Transaction(28, 3, TransactionType.CREDIT, 20.0, 2020));
        mgr.addTransaction(new Transaction(29, 3, TransactionType.DEBIT, 5.0, 2005));

        Map<Integer, Double> fees = mgr.getTransactionFees();
        assertAlmost(3.0, fees.get(1), 0.0001);
        assertAlmost(2.0, fees.get(2), 0.0001);
        assertAlmost(1.0, fees.get(3), 0.0001);
    }

    // TEST 5
    public static void testGetSuspiciousAccounts() {
        System.out.println("Running testGetSuspiciousAccounts");
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));
        mgr.addAccount(new Account(2, "Bob"));

        // Account 1 suspicious
        mgr.addTransaction(new Transaction(1, 1, TransactionType.DEBIT, 60, 1000));
        mgr.addTransaction(new Transaction(2, 1, TransactionType.DEBIT, 70, 1020));
        mgr.addTransaction(new Transaction(3, 1, TransactionType.DEBIT, 80, 1050));
        // Account 2 not suspicious
        mgr.addTransaction(new Transaction(4, 2, TransactionType.DEBIT, 60, 2000));
        mgr.addTransaction(new Transaction(5, 2, TransactionType.DEBIT, 70, 2100));

        List<Integer> suspicious = mgr.getSuspiciousAccounts();
        assertEquals(1, suspicious.size());
        assertEquals(Integer.valueOf(1), suspicious.get(0));
    }
}

