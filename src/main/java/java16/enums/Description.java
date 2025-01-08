package java16.enums;

public enum Description {
    Mentor,
    Management,
    Instructor;
    @Override
    public String toString() {
        return name();
    }
}
