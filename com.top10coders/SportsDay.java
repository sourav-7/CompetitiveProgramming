package com.top10coders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SportsDay {

    private static int efficiencyCalculator(String stringListOfInputData) {

        int efficiencyScore = 0;
        ArrayList<Integer> integerListOfInputData = convertStringListToIntegerList(new ArrayList<>(Arrays.asList(stringListOfInputData.split(" "))));
        Collections.sort(integerListOfInputData);
        System.out.println("integerListOfInputData : " + integerListOfInputData);
        efficiencyScore = getEfficiencyOfListPickedByChintu(integerListOfInputData);
        return efficiencyScore;
    }

    private static int getEfficiencyOfListPickedByChintu(ArrayList<Integer> integerListOfInputData) {
        int efficiencyScoreWithNonNegetiveNumbers = 0;
        int finalefficiencyScore = 0;
        List<Integer> numbersPickedByChintuNonNegetive = new ArrayList<Integer>();
        List<Integer> finalNumbersPickedByChintu = new ArrayList<Integer>();

        int indexOfFirstNonNegetiveInteger = 0;
        for (int index = 0; index < integerListOfInputData.size(); index++) {
            if (integerListOfInputData.get(index) >= 0) {
                indexOfFirstNonNegetiveInteger = index;
                break;
            }
            if (index == integerListOfInputData.size() - 1 && integerListOfInputData.get(index) <= 0) {
                return 0;
            }
        }
        int currentIndex = indexOfFirstNonNegetiveInteger;

        numbersPickedByChintuNonNegetive = integerListOfInputData.subList(indexOfFirstNonNegetiveInteger, integerListOfInputData.size());
        efficiencyScoreWithNonNegetiveNumbers = getEfficiencyScore(numbersPickedByChintuNonNegetive);
        finalefficiencyScore = efficiencyScoreWithNonNegetiveNumbers;

        while (currentIndex > 0) {

            finalNumbersPickedByChintu = integerListOfInputData.subList(currentIndex - 1, integerListOfInputData.size());

            if (getEfficiencyScore(finalNumbersPickedByChintu) > finalefficiencyScore) {
                finalefficiencyScore = getEfficiencyScore(finalNumbersPickedByChintu);
            } else {
                break;
            }
            currentIndex--;
        }
        return finalefficiencyScore;
    }

    private static int getEfficiencyScore(List<Integer> numbersPickedByChintu) {

        int efficiency = 0;
        for (int index = 0; index < numbersPickedByChintu.size(); index++) {
            efficiency += numbersPickedByChintu.get(index) * (index + 1);
        }
        return efficiency;
    }

    public static ArrayList<Integer> convertStringListToIntegerList(ArrayList<String> listOfString) {
        ArrayList<Integer> integerList = new ArrayList<Integer>();
        for (String integerValue : listOfString) {
            integerList.add(Integer.valueOf(integerValue));
        }
        return integerList;
    }

    public static void main(String[] args) throws IOException {

        // Taking input from user
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputFromUserInString = reader.readLine();
        System.out.println(efficiencyCalculator(inputFromUserInString));
    }
}
