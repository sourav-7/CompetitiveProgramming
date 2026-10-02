package karatCodePractice.obstacleCourse;

/*
We are writing software to collect and manage data on how fast racers can complete obstacle courses.
An obstacle course is a series of difficult physical challenges (like walls, hurdles, and ponds) that a racer must go through.
Each course consists of multiple obstacles. The software stores how long it takes for racers to
finish each obstacle, and provides useful statistics based on those times.

Definitions:
A "run" is a particular attempt to complete an entire obstacle course
A "run collection" is a group of runs on a particular course by the user.
An "obstacle" is a portion of a course. We track how long it takes to finish each portion of the
course
For example, here are some (list of) times for an obstacle course with four obstacles:
Obstacles: 01 02 03 04
Run 1: 3 4 5 6 (total: 18 seconds)
Run 2: 4 4 4 5 (total: 17 seconds)
Run 3: 4 5 4 6 (total: 19 seconds)
Run 4: 5 5 3 (13 seconds, but run is incomplete)
All of these runs for one obstacle course (including the incomplete run) make up a run collection.
To begin with, we present you with two tasks:
1.1) Read through and understand the code below. Please take as much time as necessary, and feel free to run the code.
1.2) The test for RunCollection is not passing due to a bug in the code. Make the necessary changes to RunCollection to fix the bug.

2) We would like to implement a new function in RunCollection called "best of bests".
This is a measure of how fast a run could be if everything went perfectly,
and is determined by taking the fastest times for each obstacle across all runs (even incomplete ones) and summing them.
In the run collection above, the times 3, 4, 3, and 5 combine to make the best of bests run of 15 seconds.
Implement this function, and add a test to verify that it works.
2.2) What other tests would you implement?

3) We would like to implement a new function "chanceOfPersonalBest". This takes in an
in-progress Run object, and determines the chance that this run will be a new personal best, or match the current personal best.
This assumes that, for every obstacle after the obstacles already completed in the run, the time to complete that obstacle is uniformly chosen from times
to complete that obstacle in all other runs.
We want to do this using simulation. To do this, write a method in RunCollection that takes in an in-progress run and does the following:

Over 10,000 trials:
* For each incomplete segment in the current run:
* Select a time randomly from previous runs (including completed obstacles from incomplete runs) After this I was asked the question,
    The current function is returning an approximate value, how will you return an exact value.
* Check if the run, including randomly collected times, is better than or matching the current personal best Return the chance, over those trials, that the run was a personal best.

The solution should consistently return a solution within .02 of the correct solutions.
For example, if there is a .5 (50%) chance of a personal best, the function should return a value between .48 and .52"
*/

import java.util.*;

class Course {
    String title;
    int obstacleCount;

    public Course(String title, int obstacleCount) {
        this.title = title;
        this.obstacleCount = obstacleCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Course other)) {
            return false;
        }
        return this.title.equals(other.title)
                && this.obstacleCount == other.obstacleCount;
    }
}

class Run {
    Course course;
    boolean complete;
    List<Integer> obstacleTimes;

    public Run(Course course) {
        this.course = course;
        this.complete = false;
        this.obstacleTimes = new ArrayList<>();
    }

    public void addObstacleTime(int obstacleTime) {
        if (complete) {
            throw new RuntimeException("Cannot add obstacle to complete run");
        }
        obstacleTimes.add(obstacleTime);
        if (obstacleTimes.size() == course.obstacleCount) {
            complete = true;
        }
    }

    public int getRunTime() {
        int sum = 0;
        for (int time : obstacleTimes) {
            sum += time;
        }
        return sum;
    }
}

class RunCollection {
    List<Run> runs;
    Course course;

    public RunCollection(Course course) {
        this.course = course;
        this.runs = new ArrayList<>();
    }

    public int getNumRuns() {
        return runs.size();
    }

    public void addRun(Run run) {
        if (!run.course.equals(this.course)) {
            throw new RuntimeException("Run's Course is not same as RunCollection's Course");
        }
        runs.add(run);

    }

    public int personalBest() {
        int best = Integer.MAX_VALUE;
        for (Run run : runs) {
            if (run.complete) {
                best = Math.min(best, run.getRunTime());
            }
        }
        if (best == Integer.MAX_VALUE) {
            throw new RuntimeException("No completed runs available");
        }
        return best;
    }

    int bestOfBests() {
        int[] bestOfEach = new int[course.obstacleCount];
        Arrays.fill(bestOfEach, Integer.MAX_VALUE);

        for (Run run : runs) {
            for (int i = 0; i < run.obstacleTimes.size(); i++) {
                bestOfEach[i] = Math.min(bestOfEach[i], run.obstacleTimes.get(i));
            }
        }

        int bestOfBests = 0;
        for (int best : bestOfEach) {
            if (best == Integer.MAX_VALUE) {
                throw new RuntimeException("No time recorded for an obstacle");
            }
            bestOfBests += best;
        }
        return bestOfBests;
    }

    // SIMULATION FUNCTION
    public double chanceOfPersonalBest(Run currentRun) {
        int trials = 10000;
        int success = 0;
        int personalBestTime = personalBest();
        Random random = new Random();

        for (int t = 0; t < trials; t++) {
            int simulatedTime = currentRun.getRunTime();
            // Fill remaining obstacles randomly
            for (int i = currentRun.obstacleTimes.size(); i < course.obstacleCount; i++) {
                List<Integer> possibleTimes = new ArrayList<>();
                for (Run run : runs) {
                    if (i < run.obstacleTimes.size()) {
                        possibleTimes.add(run.obstacleTimes.get(i));
                    }
                }
                int randomIndex = random.nextInt(possibleTimes.size());
                simulatedTime += possibleTimes.get(randomIndex);
            }
            if (simulatedTime <= personalBestTime) {
                success++;
            }
        }
        return (double) success / trials;
    }

    double chanceOfPersonalBestByMe(Run run) {
        // Assuming I have a fixed set of runs data during this chanceOfPersonalBest operation
        Map<Integer, List<Integer>> perObstacleTimeMap = new HashMap<>();

        for (Run r : runs) {
            for (int i = 0; i < r.obstacleTimes.size(); i++) {
                perObstacleTimeMap
                        .computeIfAbsent(i, k -> new ArrayList<>())
                        .add(r.obstacleTimes.get(i));
            }
        }

        int elapsedTime = run.getRunTime();

        int trialsCount = 10000;
        int personalBestCount = 0;
        int personalBest = personalBest();
        Random random = new Random();
        for (int i = 0; i < trialsCount; i++) {
            int simulatedTime = 0;
            for (int j = run.obstacleTimes.size(); j < course.obstacleCount; j++) {
                int randomIndex = random.nextInt(perObstacleTimeMap.get(j).size());
                simulatedTime += perObstacleTimeMap.get(j).get(randomIndex); // --> check random
            }
            if ((elapsedTime + simulatedTime) <= personalBest) {
                personalBestCount++;
            }
        }
        System.out.println(personalBestCount);
        return (double) personalBestCount / trialsCount;
    }
}

public class ObstacleCourseSolution {
    public static RunCollection makeRunCollection(Course course, int[][] obstacleData) {
        RunCollection runCollection = new RunCollection(course);
        for (int[] runData : obstacleData) {
            Run run = new Run(course);
            for (int obstacleTime : runData) {
                run.addObstacleTime(obstacleTime);
            }
            runCollection.addRun(run);
        }
        return runCollection;
    }

    public static void main(String[] args) {
        /*
        Obstacles:  O1  O2  O3  O4
        Run 1:      3   4   5   6   -> 18
        Run 2:      4   4   4   5   -> 17
        Run 3:      4   5   4   6   -> 19
        Run 4:      5   5   3       -> incomplete
        */
        int[][] obstacleData = {
                {3, 4, 5, 6},
                {4, 4, 4, 5},
                {4, 5, 4, 6},
                {5, 5, 3}
        };

        Course testCourse = new Course("Test Course", 4);

        RunCollection runCollection = makeRunCollection(testCourse, obstacleData);
        System.out.println("Number of Runs: " + runCollection.getNumRuns());
        System.out.println("Personal Best: " + runCollection.personalBest());
        System.out.println("Best Of Bests: " + runCollection.bestOfBests());

        // Current in-progress run
        Run currentRun = new Run(testCourse);
        currentRun.addObstacleTime(3);
        currentRun.addObstacleTime(3);

        double chance = runCollection.chanceOfPersonalBest(currentRun);
        System.out.println("Chance Of Personal Best: " + chance);

        // ASSERT TESTS
        assert runCollection.getNumRuns() == 4;
        assert runCollection.personalBest() == 17;
        assert runCollection.bestOfBests() == 15;
        System.out.println("\nAll test cases passed.");
    }
}