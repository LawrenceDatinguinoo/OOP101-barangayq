import java.util.ArrayList;

public class QueueManager {

    public static final String[] SERVICE_NAMES = {"Barangay Clearance", "Certificate of Residency", "Complaint"};
    public static final String[] SERVICE_CODES = {"BC", "CR", "CP"};

    public static final int[] AVERAGE_MINUTES = {5, 4, 8};

    private ArrayList<Ticket> clearanceQueue = new ArrayList<Ticket>();
    private ArrayList<Ticket> residencyQueue = new ArrayList<Ticket>();
    private ArrayList<Ticket> complaintQueue = new ArrayList<Ticket>();

    private ArrayList<Ticket> allTickets = new ArrayList<Ticket>();

    private Counter[] counters = new Counter[4];

    private int[] lastNumber = new int[3];

    public QueueManager() {
        counters[0] = new Counter(1, 0);
        counters[1] = new Counter(2, 0);
        counters[2] = new Counter(3, 1);
        counters[3] = new Counter(4, 2);
    }

    public Counter[] getCounters() {
        return counters;
    }

    public ArrayList<Ticket> getQueue(int serviceType) {
        if (serviceType == 0) {
            return clearanceQueue;
        } else if (serviceType == 1) {
            return residencyQueue;
        } else {
            return complaintQueue;
        }
    }

    public Ticket issueTicket(int serviceType, Resident resident) {
        lastNumber[serviceType]++;
        String number = SERVICE_CODES[serviceType] + "-" + String.format("%03d", lastNumber[serviceType]);

        Ticket ticket = new Ticket(number, serviceType, resident);
        ArrayList<Ticket> queue = getQueue(serviceType);

        int position = queue.size();
        if (resident.getPriorityLevel() > 0) {
            position = 0;

            while (position < queue.size() && queue.get(position).getResident().getPriorityLevel() > 0) {
                position++;
            }
        }
        queue.add(position, ticket);
        allTickets.add(ticket);
        return ticket;
    }

    public int countOpenCounters(int serviceType) {
        int count = 0;
        for (int i = 0; i < counters.length; i++) {
            if (counters[i].getServiceType() == serviceType && counters[i].isOpen()) {
                count++;
            }
        }
        return count;
    }

    public int getEstimatedWait(Ticket ticket) {
        ArrayList<Ticket> queue = getQueue(ticket.getServiceType());
        int position = queue.indexOf(ticket);
        if (position < 0) {
            return 0;
        }

        int openCounters = countOpenCounters(ticket.getServiceType());
        if (openCounters == 0) {
            return -1;
        }

        int freeCounters = openCounters - countBusyCounters(ticket.getServiceType());
        if (position < freeCounters) {
            return 0;
        }

        int ticketsToFinish = position - freeCounters + 1;
        int totalMinutes = ticketsToFinish * AVERAGE_MINUTES[ticket.getServiceType()];
        return (totalMinutes + openCounters - 1) / openCounters;
    }

    public int countBusyCounters(int serviceType) {
        int count = 0;
        for (int i = 0; i < counters.length; i++) {
            if (counters[i].getServiceType() == serviceType && counters[i].isBusy()) {
                count++;
            }
        }
        return count;
    }

    public Ticket findTicket(String ticketNumber) {
        for (int i = 0; i < allTickets.size(); i++) {
            if (allTickets.get(i).getTicketNumber().equalsIgnoreCase(ticketNumber.trim())) {
                return allTickets.get(i);
            }
        }
        return null;
    }

    public Counter findCounterServing(Ticket ticket) {
        for (int i = 0; i < counters.length; i++) {
            if (counters[i].getCurrentTicket() == ticket) {
                return counters[i];
            }
        }
        return null;
    }

    public Ticket callNext(Counter counter) {
        ArrayList<Ticket> queue = getQueue(counter.getServiceType());
        if (queue.isEmpty()) {
            return null;
        }
        Ticket ticket = queue.remove(0);
        ticket.setStatus(Ticket.SERVING);
        ticket.setTimeCalled(System.currentTimeMillis());
        counter.setCurrentTicket(ticket);
        return ticket;
    }

    public void completeCurrent(Counter counter) {
        Ticket ticket = counter.getCurrentTicket();
        if (ticket != null) {
            ticket.setStatus(Ticket.COMPLETED);
            ticket.setTimeFinished(System.currentTimeMillis());
            counter.setCurrentTicket(null);
        }
    }

    public void skipCurrent(Counter counter) {
        Ticket ticket = counter.getCurrentTicket();
        if (ticket != null) {
            ticket.setStatus(Ticket.SKIPPED);
            ticket.setTimeFinished(System.currentTimeMillis());
            counter.setCurrentTicket(null);
        }
    }

    public int countByStatus(String status) {
        int count = 0;
        for (int i = 0; i < allTickets.size(); i++) {
            if (allTickets.get(i).getStatus().equals(status)) {
                count++;
            }
        }
        return count;
    }

    public int countServedPerService(int serviceType) {
        int count = 0;
        for (int i = 0; i < allTickets.size(); i++) {
            Ticket t = allTickets.get(i);
            if (t.getServiceType() == serviceType && t.getStatus().equals(Ticket.COMPLETED)) {
                count++;
            }
        }
        return count;
    }

    public int countPriorityServed() {
        int count = 0;
        for (int i = 0; i < allTickets.size(); i++) {
            Ticket t = allTickets.get(i);
            if (t.getStatus().equals(Ticket.COMPLETED) && t.getResident().getPriorityLevel() > 0) {
                count++;
            }
        }
        return count;
    }

    public long getAverageWaitMillis(int serviceType) {
        long total = 0;
        int count = 0;
        for (int i = 0; i < allTickets.size(); i++) {
            Ticket t = allTickets.get(i);
            boolean sameService = (serviceType == -1) || (t.getServiceType() == serviceType);
            if (sameService && t.wasCalled()) {
                total = total + t.getWaitedMillis();
                count++;
            }
        }
        if (count == 0) {
            return -1;
        }
        return total / count;
    }

    public static String formatTime(long millis) {
        if (millis < 0) {
            return "no data yet";
        }
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        if (minutes == 0) {
            return seconds + " sec";
        }
        return minutes + " min " + seconds + " sec";
    }

    public int getTotalIssued() {
        return allTickets.size();
    }

    public int getTotalWaiting() {
        return clearanceQueue.size() + residencyQueue.size() + complaintQueue.size();
    }
}
