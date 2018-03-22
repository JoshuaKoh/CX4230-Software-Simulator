import java.text.DecimalFormat;

public class Task {
    private static int counter = 1;

    final int id;
    int startTime;
    int workTime;
    int totalTime;
    int lines;
    int percentToTest;
    TaskType type;

    public Task(int startTime, int workTime, int totalTime, int lines, int percentToTest, TaskType type) {
        this.id = counter++;
        this.startTime = startTime;
        this.workTime = workTime;
        this.totalTime = totalTime;
        this.lines = lines;
        this.percentToTest = percentToTest;
        this.type = type;
    }

    @Override
    public String toString() {
        double percentDone = ((double) workTime) / totalTime * 100;
        DecimalFormat formatter = new DecimalFormat("#0.0");

        return "Task " + id + ". " + type + "\n" +
                "\t- Started:\t\t" + startTime + "\n" +
                "\t- Completion:\t" + workTime + "/" + totalTime + " (" + formatter.format(percentDone) + "%)";
    }
}
