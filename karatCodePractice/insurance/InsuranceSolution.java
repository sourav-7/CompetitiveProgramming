/*
We are building a program to manage an insurance company's claims.
The system tracks multiple claims, each with a unique ID, policyholder, claim type, amount, and current status.
The program allows claims staff to add new claims, update claim statuses, and get statistics about resolved claims.

Definitions:
* A "claim" is an object that represents an insurance claim. It has properties for the ID, policyholder ID, claim type, amount, and current status.
* A "claims system" is a class which is used for managing all claims for the insurance company.
To begin with, we present you with two tasks:
1.1) Read through and understand the code below. Please take as much time as necessary, and feel free to run the code.
1.2) The test for ClaimsSystem is not passing due to a bug in the code. Make the necessary changes to ClaimsSystem to fix the bug.

Insurance claims move through multiple status changes over their lifecycle.
Το support operational reporting, we record a history of every status change on each claim (what changed, who changed it, when), and then aggregate activity across the whole system.

Definitions (in addition to those from Q1):
- A "status change" is a record of a single change to a claim's status.
It captures the new status, the role of the approver making the change, and the timestamp of the change (integer hours since a reference point).

Add three things to the system:
2.1) Extend the Claim class so each claim maintains a statusHistory - a list of StatusChange records, initially empty.
2.2) recordStatusChange (claimId, newStatus, approverRole, timestamp): update the given claim's current status to newStatus and append a new StatusChange record to its status history.
2.3) getStatusChangeCountsPerRole(): return a map of each approverRole to the total number of status changes made by that role, aggregated across all claims.
Only include roles that have made at least one change; omit roles that have none.
The testRecordStatusChange and testGetStatusChangeCountsPerRole methods are provided.
*/

// Add this class alongside the existing nested classes in Solution:

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

/**
 * Claim Status is of four types: SUBMITTED, UNDER_REVIEW, APPROVED, and REJECTED.
 * SUBMITTED and UNDER_REVIEW are active states for claims still being processed.
 * APPROVED and REJECTED are terminal (resolved) states.
 */
enum ClaimStatus {
    SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED
}

/*
 * Claim Type indicates the category of the insurance claim:
 * AUTO, HEALTH, and PROPERTY.
 */
enum ClaimType {
    AUTO, HEALTH, PROPERTY
}

/* Records a single status change on a claim. */
class StatusHistory {
    ClaimStatus newStatus;
    String approverRole;
    int timestamp;
    StatusHistory(ClaimStatus newStatus, String approverRole, int timestamp) {
        this.newStatus = newStatus;
        this.approverRole = approverRole;
        this.timestamp = timestamp;
    }
    @Override
    public String toString() {
        return "StatusChange (newStatus=" + newStatus
                + ", approverRole=" + approverRole
                + ", timestamp=" + timestamp + ")";
    }
}

/* Data about an insurance claim. */
class Claim {
    final int claimId;
    final int policyholderId;
    final ClaimType claimType;
    final double amount;
    ClaimStatus status;
    List<StatusHistory> statusHistory;

    Claim(int claimId, int policyholderId, ClaimType claimType, double amount, ClaimStatus status) {
        this.claimId = claimId;
        this.policyholderId = policyholderId;
        this.claimType = claimType;
        this.amount = amount;
        this.status = status;
        this.statusHistory = new ArrayList<>();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Claim c)) {
            return false;
        }
        return claimId == c.claimId
                && policyholderId == c.policyholderId
                && claimType == c.claimType
                && Double.compare(amount, c.amount) == 0
                && status == c.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(claimId, policyholderId, claimType, amount, status);
    }

    @Override
    public String toString() {
        return "Claim ID: " + claimId
                + ", Policyholder: " + policyholderId
                + ", Type: " + claimType
                + ", Amount: " + amount
                + ", Status: " + status;
    }
}

// Data for managing insurance claims, and methods which staff can use to perform any queries or updates.
class ClaimsSystem {
    final ArrayList<Claim> claims = new ArrayList<>();

    /**
     * Adds a claim to the system.
     */
    void addClaim(Claim claim) {
        claims.add(claim);
    }

    // Update the status of the given claim.
    void updateClaimStatus(int claimId, ClaimStatus status) {
        for (Claim claim : claims) {
            if (claim.claimId == claimId) {
                claim.status = status;
                break;
            }
        }
    }

    // Calculates and returns statistics for all claims
    Map<String, Object> getClaimStatistics() {
        int totalClaims = claims.size();
        int totalResolvedClaims = 0;
        for (Claim claim : claims) {
            if (claim.status == ClaimStatus.APPROVED || claim.status == ClaimStatus.REJECTED) {
                totalResolvedClaims++;
            }
        }
        double resolutionRate = ((double) totalResolvedClaims / totalClaims) * 100;
        Map<String, Object> result = new HashMap<>();

        result.put("total_claims", totalClaims);
        result.put("total_resolved_claims", totalResolvedClaims);
        result.put("resolution_rate", resolutionRate);
        return result;
    }

    public void recordStatusChange(int claimId, ClaimStatus newStatus, String approverRole, int timestamp) {
        for (Claim c : claims) {
            if (claimId == c.claimId) {
                c.status = newStatus;
                c.statusHistory.add(new StatusHistory(newStatus, approverRole, timestamp));
            }
        }
    }

    public Map<String, Integer> getStatusChangeCountsPerRole() {
        Map<String, Integer> statusChangeByRoleMap = new HashMap<>();
        for (Claim c : claims) {
            for (StatusHistory status : c.statusHistory) {
                statusChangeByRoleMap.merge(status.approverRole, 1, Integer::sum);
            }
        }
        return statusChangeByRoleMap;
    }
}

public class InsuranceSolution {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestSuite.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.getTrace());
        }
        if (result.wasSuccessful()) {
            System.out.println("All tests passed successfully.");
        } else {
            System.err.println("Tests failed: " + result.getFailureCount() + " test(s) failed.");
            System.exit(1);
        }
    }

    // This is not a complete test suite, but tests some basic functionality of the code and shows how to use it.
    public static class TestSuite {
        @Test
        public void testClaim() {
            Claim c = new Claim(1, 101, ClaimType.AUTO, 2500.00, ClaimStatus.SUBMITTED);

            assertEquals(1, c.claimId);
            assertEquals(101, c.policyholderId);
            assertEquals(ClaimType.AUTO, c.claimType);
            assertEquals(2500.00, c.amount, 0.001);
            assertEquals(ClaimStatus.SUBMITTED, c.status);
        }

        @Test
        public void testClaimsSystem() {
            ClaimsSystem system = new ClaimsSystem();
            Claim c1 = new Claim(1, 101, ClaimType.AUTO, 2500.00, ClaimStatus.SUBMITTED);
            system.addClaim(c1);

            assertEquals(1, system.claims.size());
            assertEquals(c1, system.claims.get(0));

            system.updateClaimStatus(1, ClaimStatus.UNDER_REVIEW);

            assertEquals(ClaimStatus.UNDER_REVIEW, system.claims.get(0).status);
            system.addClaim(new Claim(2, 102, ClaimType.HEALTH, 8400.00, ClaimStatus.APPROVED));
            system.addClaim(new Claim(3, 103, ClaimType.PROPERTY, 15000.00, ClaimStatus.REJECTED));
            system.addClaim(new Claim(4, 104, ClaimType.AUTO, 950.00, ClaimStatus.APPROVED));
            system.addClaim(new Claim(5, 105, ClaimType.HEALTH, 3200.00, ClaimStatus.UNDER_REVIEW));
            system.addClaim(new Claim(6, 106, ClaimType.PROPERTY, 22000.00, ClaimStatus.APPROVED));
            system.addClaim(new Claim(7, 107, ClaimType.AUTO, 1800.00, ClaimStatus.REJECTED));
            system.addClaim(new Claim(8, 108, ClaimType.HEALTH, 6700.00, ClaimStatus.SUBMITTED));
            system.addClaim(new Claim(9, 109, ClaimType.AUTO, 4200.00, ClaimStatus.UNDER_REVIEW));

            Map<String, Object> stats = system.getClaimStatistics();

            assertEquals(9, stats.get("total_claims"));
            assertEquals(5, stats.get("total_resolved_claims"));
            assertEquals(55.56, (double) stats.get("resolution_rate"), 0.1);
        }

        @Test
        public void testRecordStatusChange() {
            ClaimsSystem system = new ClaimsSystem();
            Claim c = new Claim(1, 101, ClaimType.AUTO, 2500.00, ClaimStatus.SUBMITTED);
            system.addClaim(c);

            system.recordStatusChange(1, ClaimStatus.SUBMITTED, "JUNIOR", 10);

            assertEquals(ClaimStatus.SUBMITTED, system.claims.get(0).status);
            assertEquals(1, system.claims.get(0).statusHistory.size());
            assertEquals(ClaimStatus.SUBMITTED, system.claims.get(0).statusHistory.get(0).newStatus);
            assertEquals("JUNIOR", system.claims.get(0).statusHistory.get(0).approverRole);
            assertEquals(10, system.claims.get(0).statusHistory.get(0).timestamp);

            // A second change updates the current status and appends a new history entry.
            system.recordStatusChange(1, ClaimStatus.UNDER_REVIEW, "SENIOR", 15);

            assertEquals(ClaimStatus.UNDER_REVIEW, system.claims.get(0).status);
            assertEquals(2, system.claims.get(0).statusHistory.size());
            assertEquals(ClaimStatus.UNDER_REVIEW, system.claims.get(0).statusHistory.get(1).newStatus);
            assertEquals(15, system.claims.get(0).statusHistory.get(1).timestamp);
        }

        @Test
        public void testGetStatusChangeCountsPerRole() {
            ClaimsSystem system = new ClaimsSystem();
            // A claim reviewed at junior tier twice, then approved by a senior.
            Claim c1 = new Claim(1, 101, ClaimType.AUTO, 2500.00, ClaimStatus.SUBMITTED);
            system.addClaim(c1);

            system.recordStatusChange(1, ClaimStatus.SUBMITTED, "JUNIOR", 10);
            system.recordStatusChange(1, ClaimStatus.UNDER_REVIEW, "JUNIOR", 20);
            system.recordStatusChange(1, ClaimStatus.APPROVED, "SENIOR", 30);

            // A claim escalated through senior review to manager approval.
            Claim c2 = new Claim(2, 102, ClaimType.HEALTH, 8400.00, ClaimStatus.SUBMITTED);
            system.addClaim(c2);

            system.recordStatusChange(2, ClaimStatus.SUBMITTED, "SENIOR", 40);
            system.recordStatusChange(2, ClaimStatus.UNDER_REVIEW, "SENIOR", 50);
            system.recordStatusChange(2, ClaimStatus.APPROVED, "MANAGER", 68);

            // A rejected claim.
            Claim c3 = new Claim(3, 103, ClaimType.PROPERTY, 15000.00, ClaimStatus.SUBMITTED);

            system.addClaim(c3);

            system.recordStatusChange(3, ClaimStatus.SUBMITTED, "JUNIOR", 70);
            system.recordStatusChange(3, ClaimStatus.REJECTED, "SENIOR", 80);

            // A newly-submitted claim with just one change.
            Claim c4 = new Claim(4, 104, ClaimType.AUTO, 95.00, ClaimStatus.SUBMITTED);
            system.addClaim(c4);

            system.recordStatusChange(4, ClaimStatus.SUBMITTED, "JUNIOR", 90);

            // A claim added with no status changes recorded.
            Claim c5 = new Claim(5, 105, ClaimType.HEALTH, 3200.00, ClaimStatus.SUBMITTED);
            system.addClaim(c5);

            Map<String, Integer> expected = new HashMap<>();
            expected.put("JUNIOR", 4);
            expected.put("SENIOR", 4);
            expected.put("MANAGER", 1);
            assertEquals(expected, system.getStatusChangeCountsPerRole());
        }
    }
}
