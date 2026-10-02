package neetcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MeetingRoomII {

    public static void main(String[] args) {
        MeetingRoomII solution = new MeetingRoomII();

        List<Interval> intervals = new ArrayList<>();
        intervals.add(new Interval(0, 40));
        intervals.add(new Interval(5, 10));
        intervals.add(new Interval(15, 20));

        int rooms = solution.minMeetingRooms(intervals);
        System.out.println("Minimum meeting rooms required: " + rooms);
    }

    public int minMeetingRooms(List<Interval> intervals) {

        if (intervals == null || intervals.isEmpty()) {
            return 0;
        }

        List<Integer> startTimes = new ArrayList<>();
        List<Integer> endTimes = new ArrayList<>();

        for (Interval interval : intervals) {
            startTimes.add(interval.start);
            endTimes.add(interval.end);
        }

        Collections.sort(startTimes);
        Collections.sort(endTimes);

        int roomCount = 0;

        Iterator<Integer> startIterator = startTimes.iterator();
        Iterator<Integer> endIterator = endTimes.iterator();

        int endTime = endIterator.next();
        int startTime;

        while (startIterator.hasNext()) {
            startTime = startIterator.next();

            if (startTime < endTime) {
                roomCount++;
            } else {
                endTime = endIterator.next();
            }
        }

        return roomCount;
    }
}

class Interval {
    public int start, end;

    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }
}

