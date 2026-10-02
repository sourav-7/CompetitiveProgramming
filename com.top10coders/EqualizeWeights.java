package com.top10coders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EqualizeWeights {

    public static void main(String[] args) throws IOException {

        // Taking input from user
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int noOfBags = Integer.parseInt(reader.readLine());
        HashMap<Integer, String[]> mapWeighsInABag = new HashMap<>();
        for (int index = 0; index < noOfBags; index++) {
            int noOfGoods = Integer.parseInt(reader.readLine());
            String[] bagWeights = reader.readLine().split(" ");
            mapWeighsInABag.put(index, bagWeights);
        }
        System.out.println(getEqualizedWeights(noOfBags, mapWeighsInABag));
    }

    private static ArrayList getEqualizedWeights(int noOfBags, HashMap<Integer, String[]> mapWeighsInABag) {
        Integer [][] bagWeightsArray = new Integer[20][20];
        for (int bagIndex = 0; bagIndex < noOfBags; bagIndex++) {
            String[] bagWeights = mapWeighsInABag.get(bagIndex);
            for (int goodWeightIndex = 0; goodWeightIndex < bagWeights.length; goodWeightIndex++) {
                bagWeightsArray[bagIndex][goodWeightIndex] = Integer.parseInt(mapWeighsInABag.get(bagIndex)[goodWeightIndex]);
            }
        }
        int eachBagWeight = 0;
        int totalBagWeight = 0;
        for (int bagIndex = 0; bagIndex < noOfBags; bagIndex++) {
            String[] bagWeights = mapWeighsInABag.get(bagIndex);
            for (int goodWeightIndex = 0; goodWeightIndex < bagWeights.length; goodWeightIndex++) {
                totalBagWeight += bagWeightsArray[bagIndex][goodWeightIndex];
                System.out.print(bagWeightsArray[bagIndex][goodWeightIndex]);
            }
            System.out.println();
        }
        System.out.println(totalBagWeight);
        eachBagWeight = totalBagWeight / noOfBags;
        System.out.println(eachBagWeight);
        return null;
    }
}
