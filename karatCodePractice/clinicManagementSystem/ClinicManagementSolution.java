package karatCodePractice.clinicManagementSystem;

/*
We are building the back-end for a clinic appointment management system.
The system tracks doctors, appointments, and patient visits.

Definitions:
* A "doctor" has: doctorId, name.
* An "appointment" has: appointmentId, doctorId, patientId, durationMinutes, status.
* AppointmentStatus is one of: SCHEDULED, COMPLETED, CANCELLED, NO_SHOW.
* "ClinicManager" manages doctors, appointments, and provides statistics.
To begin with, we present you with two tasks:

1.1) Read through and understand the code below. Feel free to run it.
1.2) The test for ClinicManager is not passing due to a bug in the code. Make the necessary changes to ClinicManager to fix the bug.

We are extending the platform to track patient visits to doctors.
Each PatientVisit represents one patient's visit to a doctor:
- visitId : unique identifier for the visit
- patientId : the patient who visited
- doctorId : the doctor the patient visited
- startTime : the start time of the visit, as minutes from the start of the day
- durationMinutes : how long the visit lasted, in minutes
- visitType : one of CONSULTATION, FOLLOWUP, EMERGENCY

To implement these changes, we need to add two functions to the ClinicManager class:
2.1) The addPatientVisit function should be used to store a visit. If the doctorId on the visit does not refer to a known doctor in ClinicManager, the visit should be ignored.
2.2) The getAverageVisitDurationByType function should return a dictionary mapping each visit type (CONSULTATION, FOLLOWUP, EMERGENCY) to the doctor's average visit duration in minutes for that type.
Only visit types the doctor has at least one visit for should appear in the dictionary.
If the doctor has no visits at all, return an empty dictionary.

To assist you in testing these new functions, we have provided the testAddPatientVisit and testGetAverageVisitDurationByType tests.

*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum AppointmentStatus {
    SCHEDULED, COMPLETED, CANCELLED, NO_SHOW
}

class Doctor {
    public int doctorId;
    public String name;

    public Doctor(int doctorId, String name) {
        this.doctorId = doctorId;
        this.name = name;
    }
}

class Appointment {
    public int appointmentId;
    public int doctorId;
    public int patientId;
    public int durationMinutes;
    public AppointmentStatus status;

    public Appointment(int appointmentId, int doctorId, int patientId, int durationMinutes, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.durationMinutes = durationMinutes;
        this.status = status;
    }
}

class AppointmentStats {
    public int totalAppointments;
    public int completedAppointments;
    public double noShowRate;

    public AppointmentStats(int totalAppointments, int completedAppointments, double noShowRate) {
        this.totalAppointments = totalAppointments;
        this.completedAppointments = completedAppointments;
        this.noShowRate = noShowRate;
    }
}

record PatientVisit(int visitId, int patientId, int doctorId, int startTime, int durationMinutes, VisitType visitType) {}

enum VisitType {
    CONSULTATION, FOLLOWUP, EMERGENCY
}

class ClinicManager {
    public Map<Integer, Doctor> doctors;
    public List<Appointment> appointments;
    public List<PatientVisit> visits;

    public ClinicManager() {
        doctors = new HashMap<>();
        appointments = new ArrayList<>();
        visits = new ArrayList<>();
    }

    public void addDoctor(Doctor doctor) {
        doctors.put(doctor.doctorId, doctor);
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    // Add patientVisit
    public void addPatientVisit(PatientVisit visit) {
        if (doctors.containsKey(visit.doctorId())) {
            visits.add(visit);
        }
    }

    public AppointmentStats getAppointmentStatistics() {
        int total = appointments.size();
        int completed = 0;

        for (Appointment a : appointments) {
            if (a.status == AppointmentStatus.COMPLETED || a.status == AppointmentStatus.NO_SHOW) {
                completed++;
            }
        }
        int noShows = 0;
        for (Appointment a : appointments) {
            if (a.status == AppointmentStatus.NO_SHOW) {
                noShows++;
            }
        }
        double noShowRate;
        if (total > 0) {
            noShowRate = (double) noShows / total;
        } else {
            noShowRate = 0.0;
        }
        return new AppointmentStats(total, completed, noShowRate);
    }

    // getAverageVisitDurationByType
    public Map<VisitType, Double> getAverageVisitDurationByType(int doctorId) {
        Map<VisitType, Double> averageDurationByVisit = new HashMap<>();
        Map<VisitType, Integer> countByVisitType = new HashMap<>();
        // List<PatientVisit> visits
        // int doctorId, int durationMinutes, VisitType visitType
        for (PatientVisit pv : visits) {
            if (pv.doctorId() == doctorId) {
                averageDurationByVisit.merge(pv.visitType(), 0d + pv.durationMinutes(), Double::sum);
                countByVisitType.merge(pv.visitType(), 1, Integer::sum);
            }
        }
        for (VisitType vt : countByVisitType.keySet()) {
            averageDurationByVisit.put(vt, (double) averageDurationByVisit.get(vt) / countByVisitType.get(vt));
        }
        return averageDurationByVisit;
    }
}


public class ClinicManagementSolution {

    public static void main(String[] args) {
        testGetAppointmentStatistics();
        testAddPatientVisit();
        testGetAverageVisitDurationByType();
        System.out.println("All tests completed!");
    }

    public static void testGetAppointmentStatistics() {
        System.out.println("Running testGetAppointmentStatistics");
        ClinicManager cm = new ClinicManager();
        cm.addAppointment(new Appointment(1, 10, 100, 30, AppointmentStatus.COMPLETED));
        cm.addAppointment(new Appointment(2, 10, 101, 45, AppointmentStatus.COMPLETED));
        cm.addAppointment(new Appointment(3, 10, 102, 30, AppointmentStatus.NO_SHOW));
        cm.addAppointment(new Appointment(4, 10, 103, 60, AppointmentStatus.CANCELLED));
        cm.addAppointment(new Appointment(5, 10, 104, 30, AppointmentStatus.SCHEDULED));

        AppointmentStats stats = cm.getAppointmentStatistics();

        System.out.println("Expected totalAppointments: 5, Actual: " + stats.totalAppointments);
        System.out.println("Expected completedAppointments: 2, Actual: " + stats.completedAppointments);
        System.out.println("Expected noShowRate: 0.2, Actual: " + stats.noShowRate);

        if (stats.totalAppointments == 5
                && stats.completedAppointments == 2
                && Math.abs(stats.noShowRate - 0.2) < 1e-4) {
            System.out.println("testGetAppointmentStatistics PASSED");
        } else {
            System.out.println("testGetAppointmentStatistics FAILED");
        }
        System.out.println();
    }

    static void testAddPatientVisit() {
        System.out.println("Running testAddPatientVisit");
        ClinicManager cm = new ClinicManager();
        cm.addDoctor(new Doctor(1, "dr_smith"));
        cm.addDoctor(new Doctor(2, "dr_jones"));
        cm.addPatientVisit(new PatientVisit(1, 100, 1, 540, 30, VisitType.CONSULTATION));
        cm.addPatientVisit(new PatientVisit(2, 101, 2, 600, 45, VisitType.FOLLOWUP));
        // Unknown doctor should be ignored
        cm.addPatientVisit(new PatientVisit(3, 102, 99, 660, 30, VisitType.EMERGENCY));

        System.out.println("Expected number of stored visits: 2");
        System.out.println("Actual number of stored visits: " + cm.visits.size());

        if (cm.visits.size() == 2) {
            System.out.println("testAddPatientVisit PASSED");
        } else {
            System.out.println("testAddPatientVisit FAILED");
        }
        System.out.println();
    }

    static void testGetAverageVisitDurationByType() {
        System.out.println("Running testGetAverage VisitDurationByType");
        ClinicManager cm = new ClinicManager();
        cm.addDoctor(new Doctor(1, "dr smith"));
        cm.addDoctor(new Doctor(2, "dr_jones"));
        cm.addPatientVisit(new PatientVisit(1, 100, 1, 540, 30, VisitType.CONSULTATION));
        cm.addPatientVisit(new PatientVisit(2, 101, 1, 600, 50, VisitType.CONSULTATION));
        cm.addPatientVisit(new PatientVisit(3, 102, 1, 660, 20, VisitType.FOLLOWUP));
        cm.addPatientVisit(new PatientVisit(4, 103, 2, 540, 40, VisitType.EMERGENCY));

        Map<VisitType, Double> avg1 = cm.getAverageVisitDurationByType(1);

        System.out.println("Doctor 1 CONSULTATION expected: 40.0, actual: " + avg1.get(VisitType.CONSULTATION));
        System.out.println("Doctor 1 FOLLOWUP expected: 20.0, actual: " + avg1.get(VisitType.FOLLOWUP));
        System.out.println("Doctor 1 should not contain EMERGENCY: " + !avg1.containsKey(VisitType.EMERGENCY));

        boolean doctor1Pass = Math.abs(40.0 - avg1.get(VisitType.CONSULTATION)) < 1e-4
                && Math.abs(20.0 - avg1.get(VisitType.FOLLOWUP)) < 1e-4
                && !avg1.containsKey(VisitType.EMERGENCY);

        Map<VisitType, Double> avg2 = cm.getAverageVisitDurationByType(2);

        System.out.println("Doctor 2 EMERGENCY expected: 40.0, actual: " + avg2.get(VisitType.EMERGENCY));
        System.out.println("Doctor 2 should not contain CONSULTATION: " + !avg2.containsKey(VisitType.CONSULTATION));

        System.out.println("Doctor 2 should not contain FOLLOWUP: " + !avg2.containsKey(VisitType.FOLLOWUP));

        boolean doctor2Pass = Math.abs(40.0 - avg2.get(VisitType.EMERGENCY)) < 1e-4
                && !avg2.containsKey(VisitType.CONSULTATION)
                && !avg2.containsKey(VisitType.FOLLOWUP);

        cm.addDoctor(new Doctor(3, "dr brown"));
        Map<VisitType, Double> avg3 = cm.getAverageVisitDurationByType(3);

        System.out.println("Doctor 3 expected empty map: " + avg3.isEmpty());

        boolean doctor3Pass = avg3.isEmpty();

        if (doctor1Pass && doctor2Pass && doctor3Pass) {
            System.out.println("testGetAverageVisitDurationByType PASSED");
        } else {
            System.out.println("testGetAverageVisitDurationByType FAILED");
        }
        System.out.println();
    }
}
