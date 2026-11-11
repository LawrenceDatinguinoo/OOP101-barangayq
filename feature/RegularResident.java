public class RegularResident extends Resident {

    public RegularResident(String name, int age) {
        super(name, age);
    }

    @Override
    public int getPriorityLevel() {
        return 0;
    }

    @Override
    public String getCategory() {
        return "Regular";
    }
}