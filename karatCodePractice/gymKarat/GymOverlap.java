package karatCodePractice.gymKarat;

import java.util.*;

public class GymOverlap {
    public static List<List<String>> findOverlaps(int[][] logs, String[] names) {
        Set<String> seen = new LinkedHashSet<>();
        List<List<String>> result = new ArrayList<>();
        int n = logs.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                // Overlap condition: intervals intersect
                if (logs[i][0] < logs[j][1] && logs[j][0] < logs[i][1]) {
                    String a = names[i], b = names[j];
                    if (a.compareTo(b) > 0) { String tmp = a; a = b; b = tmp; }
                    String key = a + "," + b;
                    if (seen.add(key)) {
                        result.add(Arrays.asList(a, b));
                    }
                }
            }
        }
        return result;
    }

    // Entry: logs[i] = {entryTime, exitTime}, names[i] = memberName
    public static void main(String[] args) {
        String[] names = {"Alice", "Bob", "Carol", "Dave"};
        int[][] logs = {{1,5},{2,4},{6,10},{3,7}};
        System.out.println(findOverlaps(logs, names));
        // Output: [[Alice, Bob], [Alice, Dave], [Bob, Dave]]
    }
}

