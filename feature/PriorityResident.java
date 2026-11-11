public class PriorityResident extends Resident {

    private String priorityType;

    public PriorityResident(String name, int age, String priorityType) {
        super(name, age);
        this.priorityType = validatePriorityType(priorityType);
    }

    public String getPriorityType() {
        return priorityType;
    }

    public void setPriorityType(String priorityType) {
        this.priorityType = validatePriorityType(priorityType);
    }

    private static String validatePriorityType(String priorityType) {
        if (priorityType.equals("Senior") || priorityType.equals("PWD") || priorityType.equals("Pregnant")) {
            return priorityType;
        } else {
            throw new IllegalArgumentException("Invalid priority type: " + priorityType);
        }
    }

    @Override
    public int getPriorityLevel() {
        return 1;
    }

    @Override
    public String getCategory() {
        return priorityType;
    }
}