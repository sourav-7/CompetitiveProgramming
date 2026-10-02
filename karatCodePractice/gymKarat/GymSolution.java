/*
We are building a program to manage a gym's membership. The gym has multiple members, each with a unique ID, name, and membership status.
The program allows gym staff to add new members, update members status, and get membership statistics.

Definitions:
* A "member" is an object that represents a gym member. It has properties for the ID, name, and membership status.
* A "membership" is a class which is used for managing members in the gym.

// Question 1
To begin with, we present you with two tasks:
1-1) Read through and understand the code below. Please take as much time as necessary, and feel free to run the code.
1-2) The test for Membership is not passing due to a bug in the code. Make the necessary changes to Membership to fix the bug.

// Question 2
We are currently updating our system to include information about workouts for our members. As part of this update, we have introduced the Workout class,
which represents a single workout session for a member. Each object of the Workout class has a unique ID,
as well as a start time and end time that are represented in the number of minutes spent from the start of the day.
You can assume that all the Workouts are from the same day.

To implement these changes, we need to add two functions to the Membership class:
2.1) The addWorkout function should be used to add a workout session for a member. If the workout is stored successfully, the function should return `true`.
If the given member does not exist while calling this function, the workout should be ignored and the function should return 'false'.
2.2) The 'getAverageWorkoutDurations` function should calculate the average duration of workouts for each member in minutes and return the results as a map.
The returned map should include all members. If a member has no workouts, their value should be 'null'. To assist you in testing these new functions,
we have provided the testAddWorkout and testGetAverageWorkoutDurations functions.

// Question 3
We are developing a payment calculation system for our members. The payment amount is determined as follows:
- For members with BRONZE Membership:
- The first workout is free.
- From the second workout onwards, each hour costs $10.
- For members with SILVER Membership:
- The first three workouts are free.
- From the fourth workout onwards, each hour costs $8.
- For members with GOLD Membership:
- The first five workouts are free.
- From the sixth workout onwards, each hour costs $6.

The workouts are ordered by their ID.
The duration of each workout is always rounded up to the nearest hour. For example, if a person spent 80 minutes in a workout, they would be charged for 2 hours.

3) Implement a 'getDuePayments' function, which returns a dictionary associating each member ID with their due payment.
To help you understand the requirements and test this new function, we have provided the testGetDuePayments function.

// Question 4
We are developing a gym buddy program to help our members find suitable workout partners. Our analysis has shown that members who spend the maximum amount of time
working out at the same time are more compatible as gym buddies. To facilitate this, we want to generate a sorted list for each member, displaying their potential
gym buddies in descending order based on the time they have spent working out at the same time.

4) Implement the 'getGymBuddies function in 'Membership' class. This function returns a dictionary mapping each member ID to a list of (buddy member ID, total overlap duration) pairs,
sorted by overlap duration in descending order.

To assist you in understanding the requirements and testing this new function, we have provided the 'testGetGymBuddies' function.
*/

package karatCodePractice.gymKarat;

import java.util.*;

enum MembershipStatus {
    BRONZE,
    SILVER,
    GOLD
}

class Workout {
    int id;
    int startTime;
    int endTime;

    Workout(int id, int startTime, int endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    int getDuration() {
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return "Workout ID : " + id +
                " Start : " + startTime +
                " End : " + endTime;
    }
}

class Member {
    int memberId;
    String name;
    MembershipStatus membershipStatus;
    List<Workout> workouts;

    Member(int memberId, String name, MembershipStatus membershipStatus) {
        this.memberId = memberId;
        this.name = name;
        this.membershipStatus = membershipStatus;
        this.workouts = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Member ID : " + memberId +
                " Name : " + name +
                " MembershipStatus : " + membershipStatus;
    }

}

class Membership {

    List<Member> members;

    Membership() {
        members = new ArrayList<>();
    }

    // Add Member
    void addMember(Member member) {
        members.add(member);
    }

    // Update Membership
    void updateMembership(int memberId, MembershipStatus membershipStatus) {
        for (Member member : members) {
            if (member.memberId == memberId) {
                member.membershipStatus = membershipStatus;
                return;
            }
        }
    }

    // Membership Statistics
    Map<String, Object> getMembershipStatistics() {
        int totalMembers = members.size();
        int totalPaidMembers = 0;

        for (Member member : members) {
            if (member.membershipStatus == MembershipStatus.GOLD) {
                totalPaidMembers++;
            }
        }

        double conversionRate = ((double) totalPaidMembers / totalMembers) * 100;

        Map<String, Object> result = new HashMap<>();

        result.put("total_members", totalMembers);
        result.put("total_paid_members", totalPaidMembers);
        result.put("conversion_rate", conversionRate);
        return result;
    }

    // TODO: add Workout
    boolean addWorkout(int memberId, Workout workout) {
        if (memberId <= 0) {    // Can memberId be <= 0?
            return false;
        }

        if ((workout.id <= 0) || (workout.startTime >= workout.endTime)) {  // Can workout.id be <= 0?
            return false;
        }

        for (Member workoutMember : members) {
            if (workoutMember.memberId == memberId) {

                if (workoutMember.workouts.contains(workout)) { // Added Duplicate Workout checks (Clarify if there can be duplicate workouts)
                    return false;
                }
                workoutMember.workouts.add(workout);
                return true;
            }
        }
        return false;
    }

    // TODO: average Workout
    Map<Integer, Double> getAverageWorkoutDurations() {
        Map<Integer, Double> averageWorkoutDurationRecord = new HashMap<>();

        for (Member member : members) {
            if (member.workouts.isEmpty()) {
                averageWorkoutDurationRecord.put(member.memberId, null);
                continue;
            }

            int totalWorkoutDuration = 0;
            for (Workout workout : member.workouts) {
                totalWorkoutDuration += workout.getDuration();
            }

            // Not possible
            if (totalWorkoutDuration == 0) {
                averageWorkoutDurationRecord.put(member.memberId, null);
                continue;
            }

            averageWorkoutDurationRecord.put(
                    member.memberId,
                    (double) totalWorkoutDuration/member.workouts.size()
            );
        }
        return averageWorkoutDurationRecord;
    }

    // TODO: average Workout
    Map<Integer, Double> getAverageWorkoutDurationsBetter() {
        // id : average
        Map<Integer, Double> averageDurationMap = new HashMap<>();
        // loop on members list
        for (Member m : members) {
            // total workouts count of each member
            int workoutCount = m.workouts.size();
            // If a member has no workouts, their value should be 'null'
            if (workoutCount == 0) {
                averageDurationMap.put(m.memberId, null);
                continue; // Lazy member
            }
            double totalWorkoutDuration = 0;
            // list of workouts
            for (Workout wo : m.workouts) {
                // Assuming no overlap in workout endTime
                // calculate total minutes of workout for each member
                totalWorkoutDuration += wo.getDuration();
            }
            // averageDuration = totalWorkoutDuration / workoutCount
            averageDurationMap.put(m.memberId, totalWorkoutDuration / workoutCount);
        }
        return averageDurationMap;
    }

    // Merge Intervals
    List<int[]> mergeIntervals(List<Workout> workouts) {
        List<int[]> intervals = new ArrayList<>();

        for (Workout workout : workouts) {
            intervals.add(new int[]{workout.startTime, workout.endTime});
        }

        intervals.sort(Comparator.comparingInt(a -> a[0]));

        List<int[]> merged = new ArrayList<>();

        for (int[] current : intervals) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < current[0]) {
                merged.add(current);
            } else {
                merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], current[1]);
            }
        }
        return merged;
    }

    // Due Payments
    Map<Integer, Integer> getDuePayments() {
        Map<Integer, Integer> result = new TreeMap<>();
        for (Member member : members) {
            List<Workout> workouts = new ArrayList<>(member.workouts);

            workouts.sort(Comparator.comparingInt(w -> w.id));

            int freeCount = 0;
            int costPerHour = 0;

            switch (member.membershipStatus) {
                case BRONZE:
                    freeCount = 1;
                    costPerHour = 10;
                    break;
                case SILVER:
                    freeCount = 3;
                    costPerHour = 8;
                    break;
                case GOLD:
                    freeCount = 5;
                    costPerHour = 6;
                    break;
            }

            List<Workout> paidWorkouts = new ArrayList<>();

            for (int i = freeCount; i < workouts.size(); i++) {
                paidWorkouts.add(workouts.get(i));
            }

            List<int[]> merged = mergeIntervals(paidWorkouts);

            int totalPayment = 0;

            for (int[] interval : merged) {
                int duration = interval[1] - interval[0];
                int hours = (duration + 59) / 60;
                totalPayment += hours * costPerHour;
            }

            // Daily CAP = 100
            totalPayment = Math.min(totalPayment, 100);
            result.put(member.memberId, totalPayment);
        }
        return result;
    }

    record GymBuddy(int memberId, int overlapTime) {
        @Override
        public String toString() {
            return "(" + memberId + ", " + overlapTime + ")";
        }
    }

    // getGymBuddies
    Map<Integer, List<GymBuddy>> getGymBuddies() {
        Map<Integer, List<GymBuddy>> gymBuddiesMap = new HashMap<>(); // Do we need insertion order or sorting? Use LinkedHashMap or TreeMap

        // Traverse members get member1
        for (Member m1 : members) {
            List<GymBuddy> gymBuddyList = new ArrayList<>();
            // Traverse members get member2
            for (Member m2 : members) {
                if (m1.memberId == m2.memberId) {
                    continue;
                }
                // Get m1 workouts
                // Get m2 workouts
                // calculate overlap
                int overlapTime = calculateOverlapTime(m1.workouts, m2.workouts);
                if (overlapTime > 0) {
                    // put it in list
                    gymBuddyList.add(new GymBuddy(m2.memberId, overlapTime));
                }
                System.out.println(gymBuddyList);
                // sort the list in descending order based on the overlapTime
                gymBuddyList.sort(Comparator.comparingInt(GymBuddy::overlapTime).reversed());
                // gymBuddyList.sort((a, b) -> b.overlapTime - a.overlapTime);

                // put list in map
                gymBuddiesMap.put(m1.memberId, gymBuddyList);
            }
        }
        // return map
        return gymBuddiesMap;
    }

    int calculateOverlapTime(List<Workout> wl1, List<Workout> wl2) {
        int overlapTime = 0;
        for (Workout w1 : wl1) {
            for (Workout w2 : wl2) {
                int start1 = w1.startTime, end1 = w1.endTime;
                int start2 = w2.startTime, end2 = w2.endTime;
                if (start2 < end1 && start1 < end2) { // Assuming there are no overlaps in a members workout
                    overlapTime += Math.min(end1, end2) - Math.max(start1, start2);
                }
            }
        }
        return overlapTime;
    }

}

public class GymSolution {

    public static void main(String[] args) {
        Membership gym = new Membership();

        gym.addMember(new Member(1, "Alice", MembershipStatus.BRONZE));
        gym.addMember(new Member(2, "Bob", MembershipStatus.SILVER));
        gym.addMember(new Member(3, "Charlie", MembershipStatus.GOLD));

        // Update Membership
        gym.updateMembership(1, MembershipStatus.SILVER);

        gym.addWorkout(1, new Workout(1, 10, 40));
        gym.addWorkout(1, new Workout(2, 100, 180));
        gym.addWorkout(2, new Workout(3, 20, 70));
        gym.addWorkout(2, new Workout(4, 120, 210));
        gym.addWorkout(3, new Workout(5, 50, 95));
        gym.addWorkout(3, new Workout(6, 300, 420));

        // INVALID WORKOUT
        // WILL BE IGNORED
        gym.addWorkout( 1, new Workout(7, 100, 100 ));

        // MEMBERSHIP STATS
        System.out.println( "===== MEMBERSHIP STATS =====" );

        System.out.println(gym.getMembershipStatistics());

        // AVERAGE WORKOUTS
        System.out.println( "===== AVERAGE WORKOUT DURATION =====" );

        System.out.println(gym.getAverageWorkoutDurations());

        // PAYMENTS
        System.out.println( "===== DUE PAYMENTS =====" );
        System.out.println(gym.getDuePayments());

        // GYM BUDDIES
        System.out.println( "===== GYM BUDDIES =====" );
        System.out.println(gym.getGymBuddies());

        // ASSERT TESTS

        System.out.println("\n===== ASSERT TESTS =====");

        // TEST 1
        assert gym.getDuePayments().get(1) == 0;

        // TEST 2
        assert gym.getMembershipStatistics().get("total_members").equals(3);

        // TEST 3
        assert gym.getAverageWorkoutDurations().get(3) == 82.5;

        System.out.println( "ALL ASSERT TESTS PASSED" );
    }
}
