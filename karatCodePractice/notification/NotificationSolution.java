package karatCodePractice.notification;

/*
We are building the backend for a retail order notification service. When something happens to a customer's order, the service creates a Notification for that order. Each notification has properties mentioned below. One order can have many notifications.

Definitions:
* A "Notification" is one message the service creates for an order. It has a notificationId, an orderId, and a createdAt time (in minutes since the start of the current window).
* A "delivery status" describes where a notification is in the delivery lifecycle:
  QUEUED, SENT, DELIVERED, or FAILED. (Delivery status is used from the next question onward.)
* A "NotificationCenter" is the class used to manage all notifications for the window.

To begin with, two tasks:
1.1) Read through and understand the code below. Take as much time as you need, and
   feel free to run the code.
1.2) The test for NotificationCenter is not passing due to a bug in the code. Make the
   necessary changes to NotificationCenter to fix the bug.
*/

/*
Delivery updates for a notification arrive from a provider, sometimes in batches, and the same update can be reported to us more than once. Each update carries a delivery status (one of QUEUED, SENT, DELIVERED, or FAILED) and the time it happened (in minutes since the start of the window). We need to ingest these batches while ignoring any update we have already stored.

Add two methods to NotificationCenter:

2.1) addUpdates(notificationId, updates): `updates` is a List<Update> for one notification. If the notification exists, store the Updates not already stored and return how many were newly stored; otherwise store nothing and return 0. An Update is a duplicate if the same status and timestamp is already stored, whether from an earlier batch or earlier in the same batch.

2.2) getUpdateCount(notificationId): return how many updates are currently stored for that notification (0 if it has none, or if there is no such notification).

Example:

Input (notification 40 already exists):
  addUpdates(40, Arrays.asList(new Update(DeliveryStatus.SENT, 5), new Update(DeliveryStatus.DELIVERED, 15)))  -> 2
  addUpdates(40, Arrays.asList(new Update(DeliveryStatus.SENT, 5), new Update(DeliveryStatus.FAILED, 30)))      -> 1
  addUpdates(40, Arrays.asList(new Update(DeliveryStatus.SENT, 5), new Update(DeliveryStatus.FAILED, 30)))      -> 0
  getUpdateCount(40)                                                                                           -> 3

Explanation:
The first call stores both Updates, so it returns 2. In the second call, new Update(DeliveryStatus.SENT, 5) is already stored, so only new Update(DeliveryStatus.FAILED, 30) is new and it returns 1. Notification 40 now has 3 stored updates in total.

The testAddUpdates and testGetUpdateCount methods are provided.
*/

// notificationId

import java.util.*;
import java.util.concurrent.*;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.*;

public class NotificationSolution {

    enum DeliveryStatus {
        QUEUED(1),
        SENT(2),
        DELIVERED(3),
        FAILED(4);

        private final int rank;

        DeliveryStatus(int rank) {
            this.rank = rank;
        }

        int getRank() {
            return rank;
        }
    }

    /**
     * Represents one delivery status update.
     *
     * Example:
     * new Update(DeliveryStatus.SENT, 5)
     *
     * means the notification became SENT at minute 5.
     */
    record Update(DeliveryStatus status, int timestamp) {
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Update n)) {
                return false;
            }
            return  status == n.status()
                    && timestamp == n.timestamp();
        }

        @Override
        public int hashCode() {
            return Objects.hash(status, timestamp);
        }
    }

    /** Data about a single notification created for an order. */
    static class Notification {
        final int notificationId;
        final int orderId;
        final int createdAt; // minutes since the start of the window
        List<Update> notificationUpdates;

        Notification(int notificationId, int orderId, int createdAt) {
            this.notificationId = notificationId;
            this.orderId = orderId;
            this.createdAt = createdAt;
            this.notificationUpdates = new CopyOnWriteArrayList<>();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Notification n)) {
                return false;
            }

            return notificationId == n.notificationId
                    && orderId == n.orderId
                    && createdAt == n.createdAt;
        }

        @Override
        public int hashCode() {
            return Objects.hash(notificationId, orderId, createdAt);
        }

        @Override
        public String toString() {
            return "Notification " + notificationId
                    + " (order " + orderId + ") created=" + createdAt;
        }
    }

    /**
     * Data for managing order notifications, and methods staff use to query them.
     */
    static class NotificationCenter {
        private final ArrayList<Notification> notifications = new ArrayList<>();

        /** Registers a notification for the current window. */
        void addNotification(Notification notification) {
            notifications.add(notification);
        }

        /** Returns the notification with the given ID, or null if there is no such notification. */
        Notification getNotification(int notificationId) {
            for (Notification notification : notifications) {
                if (notification.notificationId == notificationId) {
                    return notification;
                }
            }
            return null;
        }

        /** Returns the IDs of all notifications for the given order, sorted in ascending order. */
        List<Integer> getOrderNotifications(int orderId) {
            List<Integer> result = new ArrayList<>();

            for (Notification notification : notifications) {
                if (notification.orderId == orderId) {
                    result.add(notification.notificationId);
                }
            }
            result.sort(Integer::compareTo);
            return result;
        }

        // addUpdates
        int addUpdates (int notificationId, List<Update> updates) {
            int totalUpdate = 0;
            Set<Update> updateSet = new HashSet<>(updates);
            for (Notification notification : notifications) {
                if (notification.notificationId == notificationId) {
                    if (notification.notificationUpdates.isEmpty()) {
                        notification.notificationUpdates.addAll(updateSet);
                        return updateSet.size();
                    }

                    for (Update incomingUpdate : updateSet) {
                        if (!notification.notificationUpdates.contains(incomingUpdate)) {
                            notification.notificationUpdates.add(incomingUpdate);
                            totalUpdate++;
                        }
                    }
                }
            }
            return totalUpdate;
        }

        // getUpdateCount
        int getUpdateCount (int notificationId) {
            for (Notification notification : notifications) {
                if (notification.notificationId == notificationId) {
                    return notification.notificationUpdates.size();
                }
            }
            return 0;
        }

        // Stores updates separately for each notification.
        private final Map<Integer, Set<Update>> notificationUpdates = new HashMap<>();
        /**
         * Adds updates for a notification.
         * Returns the number of updates that were newly stored.
         */
        int addUpdatesAI(int notificationId, List<Update> updates) {
            // Notification must exist.
            if (getNotification(notificationId) == null) {
                return 0;
            }
            // Create the update set for this notification if needed.
            Set<Update> storedUpdates = notificationUpdates.computeIfAbsent(notificationId, id -> new HashSet<>());
            int newlyStored = 0;

            for (Update update : updates) {
                // Set.add() returns true only if the update was not already present.
                if (storedUpdates.add(update)) {
                    newlyStored++;
                }
            }
            return newlyStored;
        }

        /**
         * Returns the number of currently stored updates for a notification.
         */
        int getUpdateCountAI(int notificationId) {
            return notificationUpdates
                    .getOrDefault(notificationId, Collections.emptySet())
                    .size();
        }
    }

    public static class TestSuite {

        @Test
        public void testNotification() {
            Notification n = new Notification(1, 500, 30);

            assertEquals(1, n.notificationId);
            assertEquals(500, n.orderId);
            assertEquals(30, n.createdAt);
        }

        @Test
        public void testGetOrderNotifications() {
            NotificationCenter center = new NotificationCenter();

            center.addNotification(new Notification(1, 1, 0));
            center.addNotification(new Notification(2, 2, 0));
            center.addNotification(new Notification(3, 1, 0));
            center.addNotification(new Notification(4, 2, 0));
            center.addNotification(new Notification(5, 1, 0));

            // Order 1 owns notifications 1, 3, and 5.
            assertEquals(
                    Arrays.asList(1, 3, 5),
                    center.getOrderNotifications(1)
            );

            // Order 2 owns notifications 2 and 4.
            assertEquals(
                    Arrays.asList(2, 4),
                    center.getOrderNotifications(2)
            );

            // An order with no notifications returns an empty list.
            assertEquals(
                    new ArrayList<>(),
                    center.getOrderNotifications(99)
            );
        }

        // Add these two methods inside the TestSuite class.
        @Test
        public void testAddUpdates() {
            NotificationCenter center = new NotificationCenter();
            center.addNotification(new Notification(10, 1, 0));
            center.addNotification(new Notification(20, 1, 0));

            // Two new updates stored.
            assertEquals(2, center.addUpdates(10, Arrays.asList(
                    new Update(DeliveryStatus.SENT, 5),
                    new Update(DeliveryStatus.DELIVERED, 15))));

            // (SENT, 5) is already stored, so only (FAILED, 20) is new.
            assertEquals(1, center.addUpdates(10, Arrays.asList(
                    new Update(DeliveryStatus.SENT, 5),
                    new Update(DeliveryStatus.FAILED, 20))));


            // Both already stored -> nothing new.
            assertEquals(0, center.addUpdates(10, Arrays.asList(
                    new Update(DeliveryStatus.SENT, 5),
                    new Update(DeliveryStatus.FAILED, 20))));

            System.out.println("passed");
            // Within-batch duplicate: the second (SENT, 8) repeats the first,
            // so only 2 are stored.
            assertEquals(2, center.addUpdates(20, Arrays.asList(
                    new Update(DeliveryStatus.SENT, 8),
                    new Update(DeliveryStatus.SENT, 8),
                    new Update(DeliveryStatus.DELIVERED, 12))));


            // No notification with this ID: nothing is stored.
            assertEquals(0, center.addUpdates(999, Collections.singletonList(
                    new Update(DeliveryStatus.SENT, 0))));

            System.out.println("passed");
            // Same timestamp, different status -> both kept.
            assertEquals(2, center.addUpdates(10, Arrays.asList(
                    new Update(DeliveryStatus.SENT, 25),
                    new Update(DeliveryStatus.FAILED, 25))));

            System.out.println("passed");
            // Same status, different timestamp -> both kept.
            assertEquals(2, center.addUpdates(20, Arrays.asList(
                    new Update(DeliveryStatus.SENT, 30),
                    new Update(DeliveryStatus.SENT, 35))));
        }

        @Test
        public void testGetUpdateCount() {
            NotificationCenter center = new NotificationCenter();
            center.addNotification(new Notification(10, 1, 0));
            center.addNotification(new Notification(30, 2, 0)); // never updated

            center.addUpdates(10, Arrays.asList(
                    new Update(DeliveryStatus.SENT, 5),
                    new Update(DeliveryStatus.DELIVERED, 15)));

            center.addUpdates(10, Arrays.asList(
                    new Update(DeliveryStatus.SENT, 5),
                    new Update(DeliveryStatus.FAILED, 20))); // one duplicate

            center.addUpdates(999, Collections.singletonList(
                    new Update(DeliveryStatus.SENT, 0))); // ignored

            assertEquals(3, center.getUpdateCount(10));
            assertEquals(0, center.getUpdateCount(30));  // exists but no updates
            assertEquals(0, center.getUpdateCount(999)); // no such notification
        }
    }

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestSuite.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(
                    failure.getTestHeader() + ": " + failure.getException()
            );
        }

        if (result.wasSuccessful()) {
            System.out.println("All tests passed successfully.");
        } else {
            System.err.println(
                    "Tests failed: "
                            + result.getFailureCount()
                            + " test(s) failed."
            );
            System.exit(1);
        }
    }
}
