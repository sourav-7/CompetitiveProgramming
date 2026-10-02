package karatCodePractice.studentCourse;

import java.util.*;

public class StudentCoursePairs {

    public static void main(String[] argv) {

        String[][] enrollments = {
                {"58","Linear Algebra"},
                {"94","Art History"},
                {"94","Operating Systems"},
                {"17","Software Design"},
                {"58","Mechanics"},
                {"58","Economics"},
                {"17","Linear Algebra"},
                {"17","Political Science"},
                {"94","Economics"},
                {"25","Economics"},
                {"58","Software Design"}
        };
        System.out.println(findPairs(enrollments));
    }
    public static Map<String, List<String>> findPairs(String[][] enrollments) {
        // Step 1: Build student -> courses map
        Map<String, Set<String>> studentCourses = new HashMap<>();
        for (String[] e : enrollments) {
            studentCourses
                    .computeIfAbsent(e[0], k -> new HashSet<>())
                    .add(e[1]);
        }

        List<String> students = new ArrayList<>(studentCourses.keySet());
        Map<String, List<String>> result = new TreeMap<>();

        // Step 2: Check every pair
        for (int i = 0; i < students.size(); i++) {
            for (int j = i + 1; j < students.size(); j++) {
                String a = students.get(i), b = students.get(j);
                // Sort pair key by numeric ID
                if (a.compareTo(b) > 0) {
                    a = students.get(j);
                    b = students.get(i);
                }
                // Step 3: Intersection
                Set<String> shared = new HashSet<>(studentCourses.get(a));
                shared.retainAll(studentCourses.get(b));
                if (!shared.isEmpty()) {
                    List<String> sorted = new ArrayList<>(shared);
                    Collections.sort(sorted);
                    result.put(a + "," + b, sorted);
                }
            }
        }
        return result;
    }
}

