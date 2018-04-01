import java.text.DecimalFormat;

public class Task implements Comparable<Task> {
    private static int counter = 1;

    final int id;
    int startTime;
    int workTime;
    int totalTime;
    int lines;
    int percentToTest;
    TaskType type;
    Task next; // so they can be chained in the dev queue

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

    /**
     * Compares based on priority first, then by timestamp.
     * @return  0   if Tasks are tied (should never happen?)
     *          1   if this has more priority than o
     *          -1  if o has more priority than this
     */
    @Override
    public int compareTo(Task o) {
        if (this.type == TaskType.STORY) {
            if (o.type == TaskType.STORY) {
                return tiebreakWithStartTime(this.startTime, o.startTime);
            } else {
                return -1;
            }
        } else if (this.type == TaskType.DEFECT_FIX) {
            if (o.type == TaskType.STORY) {
                return 1;
            } else if (o.type == TaskType.DEFECT_FIX) {
                return tiebreakWithStartTime(this.startTime, o.startTime);
            } else {
                return -1;
            }
        } else {
            if (o.type == TaskType.REPAIR) {
                return tiebreakWithStartTime(this.startTime, o.startTime);
            } else {
                return 1;
            }
        }
    }

    /**
     * Smaller timestamp wins!
     */
    private int tiebreakWithStartTime(int t, int o) {
        if (t < o) return 1;
        if (t > o) return -1;
        else       return 0;
    }
}
