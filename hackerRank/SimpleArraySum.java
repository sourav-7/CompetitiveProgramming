package hackerRank;

import java.util.*;

import static java.util.stream.Collectors.joining;

public class SimpleArraySum {

    /*
     * Complete the 'simpleArraySum' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts INTEGER_ARRAY ar as parameter.
     */

    public static int simpleArraySum(List<Integer> ar) {
        // Write your code here
        int sum = 0;
        for(Integer arrayElement : ar) {
            sum+=arrayElement;
        }
        return sum;
    }

}