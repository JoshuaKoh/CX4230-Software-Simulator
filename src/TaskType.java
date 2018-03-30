public enum TaskType {
    DEV_HEAD("Do not use!"), STORY("Story"), DEFECT_FIX("Defect"), REPAIR("Repair");

    String name;

    TaskType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
