public abstract class Resident {

    private String name;
    private int age;

    public Resident(String name, int age) {
        setName(name);
        setAge(age);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 1 || age > 120) {
            throw new IllegalArgumentException("Age must be from 1 to 120.");
        }
        this.age = age;
    }

    public abstract int getPriorityLevel();

    public abstract String getCategory();

    @Override
    public String toString() {
        return name + " (Age " + age + ", " + getCategory() + ")";
    }
}