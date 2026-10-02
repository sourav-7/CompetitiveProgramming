package com.top10coders;

import java.util.ArrayList;
import java.util.List;

import static com.top10coders.Palindrome.isPalindrome;

public class ThreePalindrome {

    public static List palindromeSplit(String inputString) {
        List<String> palindromeStringList = new ArrayList<>();
        int sizeOfString = inputString.length();
//        palindromeStringList.add("Size of String : {" + sizeOfString + "}");

        String firstPart = "";
        String secondPart = "";
        String thirdPart = "";
        /*firstPart = String.valueOf(inputString.charAt(0));
        secondPart = String.valueOf(inputString.charAt(sizeOfString / 2));
        thirdPart = String.valueOf(inputString.charAt(sizeOfString - 1));*/

        for (int indexi = 1; indexi < sizeOfString - 1; indexi++) {
            firstPart = inputString.substring(0, indexi);
            if (isPalindrome(firstPart)) {
                for (int indexj = 1; indexj < sizeOfString - indexi; indexj++) {
                    secondPart = inputString.substring(indexi, indexj);
                    thirdPart = inputString.substring(indexi + indexj, sizeOfString - indexi - indexj);
                    if (isPalindrome(secondPart) && isPalindrome(thirdPart)) {
                        palindromeStringList.add(firstPart);
                        palindromeStringList.add(secondPart);
                        palindromeStringList.add(thirdPart);
                        return palindromeStringList;
                    }
                }
            }
        }
        palindromeStringList.add("Impossible");
        return palindromeStringList;
    }

    public static void mainly(String[] args) {
        System.out.println(isPalindrome("thebhaiiahbeht"));
        System.out.println(isPalindrome("youuoythebhaiiahbehttenet"));
    }

    public static void main(String[] args) {
        String case1 = "nayannamantenet";   // nayan naman tenet
        String case2 = "nayanamantenet";    // Impossible
        String case3 = "aaaaa"; // a a aaa
        String case4 = "aaaabaaaa"; // a aaabaaa a
        String case5 = "aaaabaa";   // aaa aba a
        String case6 = "aabaaaaa";  // a aba aaaa
        String case7 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" +
                "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc";  // Check Time Complexity
        List<String> caseList = new ArrayList<>();
        caseList.add(case1);
        caseList.add(case2);
        caseList.add(case3);
        caseList.add(case4);
        caseList.add(case5);
        caseList.add(case6);
        caseList.add(case7);

        for (String inputCase : caseList) {
            List<String> palindromeStringList = palindromeSplit(inputCase);
            for (String outputString : palindromeStringList) {
                System.out.println(outputString);
            }
            System.out.println();
        }
    }
}
