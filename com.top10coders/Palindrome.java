package com.top10coders;

public class Palindrome {

    public static boolean isPalindrome(String inputString) {
        int sizeOfString = inputString.length();
        if (sizeOfString == 1) {
            return true;
        } else {
            String firstHalfOfString = inputString.substring(0, sizeOfString / 2);
            StringBuilder reversedSecondHalfOfString = new StringBuilder();
            reversedSecondHalfOfString.append(inputString.substring(sizeOfString % 2 == 1 ? sizeOfString / 2 + 1 : sizeOfString / 2));
            reversedSecondHalfOfString.reverse();
            return firstHalfOfString.equalsIgnoreCase(String.valueOf(reversedSecondHalfOfString));
        }
    }
}
