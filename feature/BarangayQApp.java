import java.util.ArrayList;
import java.util.Scanner;

public class BarangayQApp {

    private static Scanner scanner = new Scanner(System.in);
    private static QueueManager manager = new QueueManager();

    private static ArrayList<Staff> staffAccounts = new ArrayList<Staff>();

    private static final String STAFF_CODE = "BRGY2026";

    public static void main(String[] args) {
        staffAccounts.add(new Staff("Maria Santos", "staff1", "pass123"));
        staffAccounts.add(new Staff("Juan Dela Cruz", "staff2", "pass456"));

        System.out.println("========================================");
        System.out.println("   BARANGAYQ - SMART QUEUE MANAGEMENT   ");
        System.out.println("========================================");

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("========== WELCOME, RESIDENT ==========");
            System.out.println("[1] Get a ticket");
            System.out.println("[2] Track my ticket");
            System.out.println("[3] View display board");
            System.out.println("[4] Staff Portal (login / sign up)");
            System.out.println("[0] Exit");
            int choice = readInt("Enter choice: ", 0, 4);

            switch (choice) {
                case 1:
                    getTicket();
                    break;
                case 2:
                    trackTicket();
                    break;
                case 3:
                    showDisplayBoard();
                    break;
                case 4:
                    staffPortal();
                    break;
                case 0:
                    running = false;
                    break;
            }
        }

        System.out.println();
        System.out.println("Thank you for using BarangayQ!");
        scanner.close();
    }

    private static void getTicket() {
        System.out.println();
        System.out.println("--- GET A TICKET ---");

        System.out.println("Select a service:");
        for (int i = 0; i < QueueManager.SERVICE_NAMES.length; i++) {
            System.out.println("[" + (i + 1) + "] " + QueueManager.SERVICE_NAMES[i]);
        }
        int serviceType = readInt("Enter choice: ", 1, 3) - 1;

        String name = readName("Enter your name: ");
        int age = readInt("Enter your age: ", 1, 120);

        System.out.println("Select your status:");
        System.out.println("[1] Regular");
        System.out.println("[2] Senior Citizen");
        System.out.println("[3] PWD");
        System.out.println("[4] Pregnant");
        int status = readInt("Enter choice: ", 1, 4);

        if (status == 2 && age < 60) {
            System.out.println("Senior Citizen priority is for ages 60 and above.");
            System.out.println("Your ticket will be issued as Regular.");
            status = 1;
        }

        Resident resident = null;
        try {
            if (status == 1) {
                resident = new RegularResident(name, age);
            } else if (status == 2) {
                resident = new PriorityResident(name, age, "Senior");
            } else if (status == 3) {
                resident = new PriorityResident(name, age, "PWD");
            } else {
                resident = new PriorityResident(name, age, "Pregnant");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        Ticket ticket = manager.issueTicket(serviceType, resident);
        int wait = manager.getEstimatedWait(ticket);

        System.out.println();
        System.out.println("*****************************************");
        System.out.println("  YOUR TICKET");
        System.out.println("  Ticket No.  : " + ticket.getTicketLabel());
        System.out.println("  Name        : " + resident.getName());
        System.out.println("  Service     : " + QueueManager.SERVICE_NAMES[serviceType]);
        System.out.println("  Position    : " + (manager.getQueue(serviceType).indexOf(ticket) + 1)
                + " in line");
        System.out.println("  Est. wait   : " + describeWait(wait));
        System.out.println("*****************************************");
        System.out.println("Keep your ticket number! Choose \"Track my ticket\" to check your turn.");
    }

    private static String describeWait(int wait) {
        if (wait < 0) {
            return "No counter is open yet";
        } else if (wait == 0) {
            return "A counter is free - you will be called very soon";
        }
        return "about " + wait + " minute(s)";
    }

    private static void trackTicket() {
        System.out.println();
        System.out.println("--- TRACK MY TICKET ---");
        System.out.print("Enter your ticket number (example BC-001): ");
        String number = scanner.nextLine().trim();

        Ticket ticket = manager.findTicket(number);
        if (ticket == null) {
            System.out.println("Ticket not found. Please check the number and try again.");
            return;
        }

        int serviceType = ticket.getServiceType();
        String status = ticket.getStatus();

        System.out.println();
        System.out.println("=========== TICKET STATUS ===========");
        System.out.println("Ticket  : " + ticket.getTicketNumber());
        System.out.println("Service : " + QueueManager.SERVICE_NAMES[serviceType]);
        System.out.println("Status  : " + status);

        if (status.equals(Ticket.WAITING)) {
            ArrayList<Ticket> queue = manager.getQueue(serviceType);
            int position = queue.indexOf(ticket);
            System.out.println("Position: " + (position + 1) + " in line");
            System.out.println("Ahead of you: " + position + " ticket(s)");
            System.out.println("Est. wait: " + describeWait(manager.getEstimatedWait(ticket)));
            System.out.println("Waiting for: " + QueueManager.formatTime(ticket.getWaitedMillis()));
            System.out.println("-------------------------------------");
            showNowServing(serviceType);
            System.out.println("-------------------------------------");
            System.out.println("Waiting line:");
            printLine(queue, ticket);
        } else if (status.equals(Ticket.SERVING)) {
            Counter counter = manager.findCounterServing(ticket);
            System.out.println(">>> It is your turn! Please go to Counter " + counter.getCounterNumber() + ".");
        } else if (status.equals(Ticket.COMPLETED)) {
            System.out.println("Your transaction is done. Thank you!");
        } else {
            System.out.println("Your ticket was skipped because you were not at the counter when called.");
            System.out.println("Please get a new ticket.");
        }
        System.out.println("=====================================");
    }

    private static void showNowServing(int serviceType) {
        System.out.println("Now serving:");
        Counter[] counters = manager.getCounters();
        for (int i = 0; i < counters.length; i++) {
            if (counters[i].getServiceType() == serviceType) {
                System.out.println("  Counter " + counters[i].getCounterNumber() + ": " + counters[i].getStatusText());
            }
        }
    }

    private static void printLine(ArrayList<Ticket> queue, Ticket mine) {
        if (queue.isEmpty()) {
            System.out.println("  (no one is waiting)");
            return;
        }
        for (int i = 0; i < queue.size(); i++) {
            Ticket t = queue.get(i);
            String marker = "";
            if (t == mine) {
                marker = "   <-- YOU";
            }
            System.out.println("  " + (i + 1) + ". " + t.getPublicLabel() + marker);
        }
    }

    private static void showDisplayBoard() {
        System.out.println();
        System.out.println("=========== DISPLAY BOARD ===========");
        Counter[] counters = manager.getCounters();
        for (int i = 0; i < counters.length; i++) {
            Counter c = counters[i];
            System.out.println("Counter " + c.getCounterNumber() + " ("
                    + QueueManager.SERVICE_NAMES[c.getServiceType()] + "): " + c.getStatusText());
        }
        System.out.println("-------------------------------------");
        for (int i = 0; i < QueueManager.SERVICE_NAMES.length; i++) {
            System.out.println(QueueManager.SERVICE_NAMES[i] + " - waiting: "
                    + manager.getQueue(i).size());
            printLine(manager.getQueue(i), null);
        }
        System.out.println("=====================================");
    }

    private static void staffPortal() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("-------- STAFF PORTAL --------");
            System.out.println("[1] Log in");
            System.out.println("[2] Sign up (new staff)");
            System.out.println("[0] Back");
            int choice = readInt("Enter choice: ", 0, 2);

            if (choice == 1) {
                Staff staff = staffLogin();
                if (staff != null) {
                    staffMenu(staff);
                    back = true;
                }
            } else if (choice == 2) {
                staffSignUp();
            } else {
                back = true;
            }
        }
    }

    private static void staffSignUp() {
        System.out.println();
        System.out.println("-------- STAFF SIGN UP --------");

        System.out.print("Enter staff code from the barangay office: ");
        String code = scanner.nextLine().trim();
        if (!code.equals(STAFF_CODE)) {
            System.out.println("Wrong staff code. Sign up cancelled.");
            return;
        }

        String fullName = readName("Full name: ");

        String username = "";
        boolean usernameOk = false;
        while (!usernameOk) {
            System.out.print("Choose a username (at least 4 characters, no spaces): ");
            username = scanner.nextLine().trim();
            if (!Staff.isValidUsername(username)) {
                System.out.println("Invalid username. Try again.");
            } else if (usernameExists(username)) {
                System.out.println("That username is already taken. Try another one.");
            } else {
                usernameOk = true;
            }
        }

        String password = "";
        boolean passwordOk = false;
        while (!passwordOk) {
            System.out.print("Choose a password (at least 6 characters): ");
            password = scanner.nextLine();
            if (!Staff.isValidPassword(password)) {
                System.out.println("Password is too short. Try again.");
                continue;
            }
            System.out.print("Confirm password: ");
            String confirm = scanner.nextLine();
            if (password.equals(confirm)) {
                passwordOk = true;
            } else {
                System.out.println("Passwords do not match. Try again.");
            }
        }

        try {
            staffAccounts.add(new Staff(fullName, username, password));
            System.out.println("Account created! You can now log in, " + fullName + ".");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static boolean usernameExists(String username) {
        for (int i = 0; i < staffAccounts.size(); i++) {
            if (staffAccounts.get(i).getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    private static Staff staffLogin() {
        System.out.println();
        System.out.println("-------- STAFF LOGIN --------");
        int attempts = 0;
        while (attempts < 3) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            for (int i = 0; i < staffAccounts.size(); i++) {
                Staff account = staffAccounts.get(i);
                if (account.getUsername().equals(username) && account.checkPassword(password)) {
                    System.out.println("Login successful. Welcome, " + account.getFullName() + "!");
                    return account;
                }
            }

            attempts++;
            System.out.println("Wrong username or password. Attempts left: " + (3 - attempts));
        }
        System.out.println("Too many failed attempts. Returning to the staff portal.");
        return null;
    }

    private static void staffMenu(Staff staff) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println();
            System.out.println("-------- STAFF MENU (" + staff.getFullName() + ") --------");
            System.out.println("[1] Manage counters");
            System.out.println("[2] View reports");
            System.out.println("[0] Log out");
            int choice = readInt("Enter choice: ", 0, 2);

            if (choice == 1) {
                selectCounter();
            } else if (choice == 2) {
                showReport();
            } else {
                loggedIn = false;
                System.out.println("You have logged out.");
            }
        }
    }

    private static void selectCounter() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("-------- SELECT COUNTER --------");
            Counter[] counters = manager.getCounters();
            for (int i = 0; i < counters.length; i++) {
                Counter c = counters[i];
                System.out.println("[" + c.getCounterNumber() + "] Counter " + c.getCounterNumber()
                        + " - " + QueueManager.SERVICE_NAMES[c.getServiceType()]
                        + " - " + c.getStatusText());
            }
            System.out.println("[0] Back");
            int choice = readInt("Enter choice: ", 0, counters.length);

            if (choice == 0) {
                back = true;
            } else {
                counterMenu(counters[choice - 1]);
            }
        }
    }

    private static void counterMenu(Counter counter) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Counter " + counter.getCounterNumber() + " ("
                    + QueueManager.SERVICE_NAMES[counter.getServiceType()] + ") ---");
            System.out.println("Status: " + counter.getStatusText());
            System.out.println("[1] Call next ticket");
            System.out.println("[2] Mark current ticket as COMPLETED");
            System.out.println("[3] Mark current ticket as SKIPPED");
            System.out.println("[4] View waiting list");
            System.out.println("[5] Open / Close this counter");
            System.out.println("[0] Back");
            int choice = readInt("Enter choice: ", 0, 5);

            switch (choice) {
                case 1:
                    callNextTicket(counter);
                    break;
                case 2:
                    finishTicket(counter, true);
                    break;
                case 3:
                    finishTicket(counter, false);
                    break;
                case 4:
                    showWaitingList(counter.getServiceType());
                    break;
                case 5:
                    toggleCounter(counter);
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    private static void callNextTicket(Counter counter) {
        if (!counter.isOpen()) {
            System.out.println("This counter is closed. Please open it first.");
            return;
        }
        if (counter.isBusy()) {
            System.out.println("Finish the current ticket first (" + counter.getCurrentTicket().getTicketNumber() + ").");
            return;
        }

        Ticket ticket = manager.callNext(counter);
        if (ticket == null) {
            System.out.println("No one is waiting for this service.");
        } else {
            System.out.println();
            System.out.println(">>> NOW SERVING: " + ticket.getTicketLabel());
            System.out.println("    Resident: " + ticket.getResident());
        }
    }

    private static void finishTicket(Counter counter, boolean completed) {
        if (!counter.isBusy()) {
            System.out.println("This counter is not serving anyone right now.");
            return;
        }
        String number = counter.getCurrentTicket().getTicketNumber();
        if (completed) {
            manager.completeCurrent(counter);
            System.out.println("Ticket " + number + " marked as COMPLETED.");
        } else {
            manager.skipCurrent(counter);
            System.out.println("Ticket " + number + " marked as SKIPPED.");
        }
    }

    private static void showWaitingList(int serviceType) {
        ArrayList<Ticket> queue = manager.getQueue(serviceType);
        System.out.println();
        System.out.println("Waiting list - " + QueueManager.SERVICE_NAMES[serviceType]);
        if (queue.isEmpty()) {
            System.out.println("(no one is waiting)");
            return;
        }
        for (int i = 0; i < queue.size(); i++) {
            Ticket t = queue.get(i);
            System.out.println((i + 1) + ". " + t.getTicketLabel() + " - " + t.getResident().getName());
        }
    }

    private static void toggleCounter(Counter counter) {
        if (counter.isOpen()) {
            if (counter.isBusy()) {
                System.out.println("Cannot close the counter while serving a ticket.");
            } else {
                counter.setOpen(false);
                System.out.println("Counter " + counter.getCounterNumber() + " is now CLOSED.");
            }
        } else {
            counter.setOpen(true);
            System.out.println("Counter " + counter.getCounterNumber() + " is now OPEN.");
        }
    }

    private static void showReport() {
        System.out.println();
        System.out.println("============== REPORT ==============");
        System.out.println("Total tickets issued : " + manager.getTotalIssued());
        System.out.println("Total served         : " + manager.countByStatus(Ticket.COMPLETED));
        System.out.println("  - Priority served  : " + manager.countPriorityServed());
        System.out.println("Total skipped        : " + manager.countByStatus(Ticket.SKIPPED));
        System.out.println("Total waiting        : " + manager.getTotalWaiting());
        System.out.println("Currently being served: " + manager.countByStatus(Ticket.SERVING));
        System.out.println("Average waiting time : " + QueueManager.formatTime(manager.getAverageWaitMillis(-1)));
        System.out.println("------------------------------------");
        System.out.println("Per service:");
        for (int i = 0; i < QueueManager.SERVICE_NAMES.length; i++) {
            System.out.println("  " + QueueManager.SERVICE_NAMES[i] + ": served "
                    + manager.countServedPerService(i) + ", average wait "
                    + QueueManager.formatTime(manager.getAverageWaitMillis(i)));
        }
        System.out.println("====================================");
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                int number = Integer.parseInt(input.trim());
                if (number >= min && number <= max) {
                    return number;
                }
                System.out.println("Please enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numbers only.");
            }
        }
    }

    private static String readName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (!input.trim().isEmpty()) {
                return input.trim();
            }
            System.out.println("Name cannot be empty.");
        }
    }
}
