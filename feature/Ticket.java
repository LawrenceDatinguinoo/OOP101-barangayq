public class Ticket {

    public static final String WAITING = "WAITING";
    public static final String SERVING = "SERVING";
    public static final String COMPLETED = "COMPLETED";
    public static final String SKIPPED = "SKIPPED";

    private String ticketNumber;
    private int serviceType;
    private Resident resident;
    private String status;

    private long timeIssued;
    private long timeCalled;
    private long timeFinished;

    public Ticket(String ticketNumber, int serviceType, Resident resident) {
        this.ticketNumber = ticketNumber;
        this.serviceType = serviceType;
        this.resident = resident;
        this.status = WAITING;
        this.timeIssued = System.currentTimeMillis();
        this.timeCalled = 0;
        this.timeFinished = 0;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public int getServiceType() {
        return serviceType;
    }

    public Resident getResident() {
        return resident;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimeIssued() {
        return timeIssued;
    }

    public long getTimeCalled() {
        return timeCalled;
    }

    public void setTimeCalled(long timeCalled) {
        this.timeCalled = timeCalled;
    }

    public long getTimeFinished() {
        return timeFinished;
    }

    public void setTimeFinished(long timeFinished) {
        this.timeFinished = timeFinished;
    }

    public boolean wasCalled() {
        return timeCalled > 0;
    }

    public long getWaitedMillis() {
        if (wasCalled()) {
            return timeCalled - timeIssued;
        }
        return System.currentTimeMillis() - timeIssued;
    }

    public String getTicketLabel() {
        if (resident.getPriorityLevel() > 0) {
            return ticketNumber + " [PRIORITY - " + resident.getCategory() + "]";
        }
        return ticketNumber;
    }

    public String getPublicLabel() {
        if (resident.getPriorityLevel() > 0) {
            return ticketNumber + " [PRIORITY]";
        }
        return ticketNumber;
    }

    @Override
    public String toString() {
        return getTicketLabel() + " - " + resident.getName() + " (" + status + ")";
    }
}
