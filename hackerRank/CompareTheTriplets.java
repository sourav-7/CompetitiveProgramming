package hackerRank;


import java.util.*;

import static java.util.stream.Collectors.joining;


public class CompareTheTriplets {
    /*
     * Complete the 'compareTriplets' function below.
     *
     * The function is expected to return an INTEGER_ARRAY.
     * The function accepts following parameters:
     *  1. INTEGER_ARRAY a
     *  2. INTEGER_ARRAY b
     */

    public static List<Integer> compareTriplets(List<Integer> a, List<Integer> b) {
        Integer aCount = 0, bCount = 0;
        for(int index = 0; index < 3; index++) {
            if (Objects.equals(a.get(index), b.get(index))) {
                continue;
            }
            if (a.get(index) > b.get(index)) {
                aCount++;
            } else {
                bCount++;
            }
        }
        return Arrays.asList(aCount, bCount);
    }

}
