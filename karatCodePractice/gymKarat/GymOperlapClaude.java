package karatCodePractice.gymKarat;

import java.io.*;
import java.util.*;

/*
A gym tracks members using a log of entry and exit timestamps.
Given a list of (member_name, entry_time, exit_time) records, find all pairs of members who were present in the gym at the same time.

Two members overlap if one entered before or when the other exited AND the other entered before or when the first exited:
  overlap if: entryA < exitB  AND  entryB < exitA

Return a list of [member1, member2] pairs (sorted alphabetically, no duplicates).

Examples
Input	logs = [["Alice", 1, 5], ["Bob", 2, 4], ["Carol", 6, 10], ["Dave", 3, 7]]
Output	[["Alice","Bob"], ["Alice","Dave"], ["Bob","Dave"]]

Explanation	Alice & Bob overlap (1-5 ∩ 2-4). Alice & Dave overlap (1-5 ∩ 3-7). Bob & Dave overlap (2-4 ∩ 3-7). Carol does not overlap with anyone in 1-5.

Constraints
•	1 <= logs.length <= 10^4
•	entry_time < exit_time for every record
•	All times are positive integers
•	Follow-up: What if a member can have multiple sessions?
*/

public class GymOperlapClaude {
    public static void main(String[] argv) {
        List<GymMember> memberList = Arrays.asList(
                new GymMember("Alice", 1, 5),
                new GymMember("Bob", 2, 4),
                new GymMember("Carol", 6, 10),
                new GymMember("Dave", 3, 7)
        );

        System.out.println(MemberUtils.getGymBuddyList(memberList));
    }
}

class GymMember implements Comparable<GymMember> {
    String member_name;
    int entry_time;
    int exit_time;

    GymMember(String member_name, int entry_time, int exit_time) {
        this.member_name = member_name;
        this.entry_time = entry_time;
        this.exit_time = exit_time;
    }

    @Override
    public int compareTo(GymMember member) {
        return this.member_name.compareTo(member.member_name);
    }

    @Override
    public String toString() {
        return "["
                + "\"" + member_name + "\""
                + ", " + entry_time
                + ", " + exit_time
                + "]";
    }
}

class MemberBuddy {
    String member_name1;
    String member_name2;

    MemberBuddy(String member_name1, String member_name2) {
        this.member_name1 = member_name1;
        this.member_name2 = member_name2;
    }

    @Override
    public String toString() {
        return "["
                + "\"" + member_name1 + "\""
                + ", "
                + "\"" + member_name2 + "\""
                + "]";
    }
}

class MemberUtils {
    public static List<MemberBuddy> getGymBuddyList(List<GymMember> memberList) {
        memberList.sort(null);
        List<MemberBuddy> buddyList = new ArrayList<>();
        for (int left = 0; left < memberList.size() - 1; left++) {
            for (int right = left + 1; right < memberList.size(); right++) {
                if (mergeIntervalChecker(memberList.get(left), memberList.get(right))) {
                    buddyList.add(new MemberBuddy(memberList.get(left).member_name, memberList.get(right).member_name));
                }
            }
        }
        return buddyList;
    }

    public static boolean mergeIntervalChecker(GymMember m1, GymMember m2) {
        return (m1.entry_time < m2.exit_time && m2.entry_time < m1.exit_time);
    }
}