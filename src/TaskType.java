public enum TaskType {
    STORY("Story"), DEFECT_FIX("Defect"), REPAIR("Repair");

    String name;

    TaskType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
