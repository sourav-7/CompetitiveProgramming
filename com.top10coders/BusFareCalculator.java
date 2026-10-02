package com.top10coders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BusFareCalculator {

    public static void main(String[] args) throws IOException {

        // Taking input from user
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int noOfCities = Integer.parseInt(reader.readLine());
        String fareListInString = reader.readLine();
        int skipCities = Integer.parseInt(reader.readLine());
        System.out.println();
    }
}
