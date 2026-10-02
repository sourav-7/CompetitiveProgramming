/*
We are developing a stock trading data management software that tracks the prices of different stocks over time and provides useful statistics.

The program includes three classes: 'Stock', 'PriceRecord', and 'StockCollection'.

Classes:
The 'Stock' class represents data about a specific stock.
The 'PriceRecord class holds information about a single price record for a stock.
The 'StockCollection' class manages a collection of price records for a particular stock and provides methods to retrieve useful statistics about the stock's prices.

To begin with, we present you with two tasks:

1-1) Read through and understand the code below. Please take as much time as necessary, and feel free to run the code
1-2) The test for StockCollection is not passing due to a bug in the code. Make the necessary changes to StockCollection to fix the bug.

2) We want to add a new function called "getBiggestChange" to the StockCollection class. This function calculates and returns the largest change in stock price between
any two consecutive days in the price records of a stock along with the dates of the change in a list. For example, let's consider the following price records of a stock:

Price Records:
Price: 110 112 90 105
Date: 2023-06-29 2023-07-01 2023-06-25 2023-07-06
Stock price changes (sorted based on date):
Date: 2023-06-25 -> 2023-06-29 -> 2023-07-01 -> 2023-07-06
Price: 90 -> 110 -> 112 -> 105
Change: +20 +2 -7

In this case, the biggest change in the stock price was +20, which occurred between 2023-06-25 and 2023-06-29. In this case, the function should return [20, "2023-06-25", "2023-06-29"]

Two days are considered consecutive if there are no other days' data in between them in the price records based on their dates. To assist you in testing this new function,
we have provided the testGetBiggestChange function.
*/

package karatCodePractice.stockCollection;

import java.util.*;

class Stock {
    String symbol;
    String name;

    public Stock(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }
}

class PriceRecord {
    Stock stock;
    int price;
    String date;

    public PriceRecord(Stock stock, int price, String date) {
        this.stock = stock;
        this.price = price;
        this.date = date;
    }
}

class StockCollection {
    private final List<PriceRecord> priceRecords;
    private final Stock stock;

    public StockCollection(Stock stock) {
        this.stock = stock;
        this.priceRecords = new ArrayList<>();
    }

    public void addPriceRecord(PriceRecord record) {
        if (!record.stock.symbol.equals(stock.symbol)) {
            throw new IllegalArgumentException("PriceRecord stock does not match collection stock");
        }
        priceRecords.add(record);
    }

    public int getNumPriceRecords() {
        return priceRecords.size();
    }

    public Integer getMaxPrice() {
        /*if (priceRecords.isEmpty()) {
            return null;
        }*/
        int max = Integer.MIN_VALUE;

        for (PriceRecord record : priceRecords) {
            max = Math.max(max, record.price);
        }
        return max;
    }

    public Integer getMinPrice() {
        /*if (priceRecords.isEmpty()) {
            return null;
        }*/
        int min = Integer.MAX_VALUE;

        for (PriceRecord record : priceRecords) {
            min = Math.min(min, record.price);
        }
        return min;
    }

    public Double getAvgPrice() {
        /*if (priceRecords.isEmpty()) {
            return null;
        }*/
        int sum = 0;

        for (PriceRecord record : priceRecords) {
            sum += record.price;
        }
        return (double) sum / priceRecords.size();
    }

    record BiggestChange(int priceChange, String previousDate, String nextDate) {
        @Override
        public String toString() {
            return priceChange + ", \"" + previousDate + "\", \"" + nextDate + "\"";
        }
    }

    // Assuming Multiple stocks
    List<BiggestChange> getBiggestChange() {
        List<BiggestChange> biggestChangeList = new ArrayList<>();  // Result will return empty list // Assign null for null return
        // Edge case check
        if (priceRecords.size() < 2) {
            return biggestChangeList; // Return null or empty list?
        }
        // Sort price records based on date
        // List<PriceRecord> sortedRecords = new ArrayList<>(priceRecords);
        priceRecords.sort(Comparator.comparing(pr -> pr.date));

        int biggestChange = 0;
        // for
        for (int i = 0; i < priceRecords.size() - 1; i++) {
            // PriceRecord current = sortedRecords.get(i);
            // PriceRecord next = sortedRecords.get(i + 1);
            int currentChange = priceRecords.get(i + 1).price - priceRecords.get(i).price;
            // Calculate max of them based on price change(+ or -) Math.abs();
            if (Math.abs(currentChange) >= Math.abs(biggestChange)) {
                biggestChange = currentChange;
                // Put the details in list
                biggestChangeList.clear();
                biggestChangeList.add(new BiggestChange(biggestChange, priceRecords.get(i).date, priceRecords.get(i + 1).date));
            }
        }
        // return list
        return biggestChangeList;
    }
}

public class StockCollectionSolution {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Stock stock = new Stock("AAPL", "Apple Inc.");

        StockCollection collection = new StockCollection(stock);
        collection.addPriceRecord(new PriceRecord(stock, 110, "2023-06-29"));
        collection.addPriceRecord(new PriceRecord(stock, 112, "2023-07-01"));
        collection.addPriceRecord(new PriceRecord(stock, 90, "2023-06-25"));
        collection.addPriceRecord(new PriceRecord(stock, 105, "2023-07-06"));

        System.out.println("Number of Records: " + collection.getNumPriceRecords());
        System.out.println("Max Price: " + collection.getMaxPrice());
        System.out.println("Min Price: " + collection.getMinPrice());
        System.out.println("Average Price: " + collection.getAvgPrice());
        System.out.println("Biggest Change: " + collection.getBiggestChange());
        System.out.println("ResponseTime : " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
