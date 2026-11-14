public class Counter {

    private int counterNumber;
    private int serviceType;
    private boolean open;
    private Ticket currentTicket;

    public Counter(int counterNumber, int serviceType) {
        this.counterNumber = counterNumber;
        this.serviceType = serviceType;
        this.open = true;
        this.currentTicket = null;
    }

    public int getCounterNumber() {
        return counterNumber;
    }

    public int getServiceType() {
        return serviceType;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Ticket getCurrentTicket() {
        return currentTicket;
    }

    public void setCurrentTicket(Ticket currentTicket) {
        this.currentTicket = currentTicket;
    }

    public boolean isBusy() {
        return currentTicket != null;
    }

    public String getStatusText() {
        if (!open) {
            return "CLOSED";
        }
        if (isBusy()) {
            return "SERVING " + currentTicket.getTicketNumber();
        }
        return "OPEN (idle)";
    }
}
