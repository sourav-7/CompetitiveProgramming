package com.top10coders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TravelCost {

    public static void main(String[] args) throws IOException {

        // Taking input from user
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int noOfCities = Integer.parseInt(reader.readLine());
        String fareListInString = reader.readLine();
        int skipCities = Integer.parseInt(reader.readLine());
        System.out.println(costCalculator(noOfCities, fareListInString, skipCities));
    }

    private static int costCalculator(int noOfCities, String fareListInString, int skipCities) {

        int sumFare = 0;
        ArrayList<Integer> fareList = convertStringListToIntegerList(new ArrayList<>(Arrays.asList(fareListInString.split(" "))));
        if (fareList.get(noOfCities - 1) == -1) {
            return -1;
        }
        if (skipCities >= noOfCities) {
            return fareList.get(noOfCities - 1);
        }
        Collections.sort(fareList);

        int indexOfFirstNonNegativeInteger = 0;
        for (int index = 0; index < noOfCities; index++) {
            if (fareList.get(index) >= 0) {
                indexOfFirstNonNegativeInteger = index;
                skipCities = (skipCities - indexOfFirstNonNegativeInteger) < 0 ? 0 : skipCities - indexOfFirstNonNegativeInteger;
                break;
            }
        }

        if (indexOfFirstNonNegativeInteger < (noOfCities - skipCities)) {
            List<Integer> fareListAfterSkippingCities = fareList.subList(indexOfFirstNonNegativeInteger, noOfCities - skipCities);
            for (int fare : fareListAfterSkippingCities) {
                sumFare += fare;
            }
        }
        return sumFare;
    }

    public static ArrayList<Integer> convertStringListToIntegerList(ArrayList<String> listOfString) {
        ArrayList<Integer> integerList = new ArrayList<>();
        for (String integerValue : listOfString) {
            integerList.add(Integer.valueOf(integerValue));
        }
        return integerList;
    }
}
