import java.util.Random;

public class RVP {
    static Random rand = new Random();

    private static int MIN;
    private static int MAX;

    private static final int FIVE_DAYS = 240;  //five days time
    private static final int THREE_DAYS = 144; //three days time
    private static final int TWO_DAYS = 96;    //two days time
    private static final int ONE_DAY = 24;     //one days time
    private static final int TEN = 10;

    // Poisson distribution
    public static int DuC(double lambda) {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= Math.random();
        } while (p > L);

        return k - 1;
    }
    // Uniform distribution
    public static int uniformDistribution(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }
    
    // Normal distribution
    public static int normalDistribution(int mean, int stdDev) {
    	return (int) rand.nextGaussian()*stdDev+mean;
    }

    // Provides the values of the creation times of new Design Stage stories. Creation times come from a Poisson distribution
    // where lamda is 144, representing three days time.
    public static int designRate() {
        return (DuC(THREE_DAYS));
    }

    // Provides the value for the amount of time to complete a Design Stage story.
    public static int designTime() {
        int designTime = normalDistribution(TWO_DAYS, ONE_DAY);
        if (designTime <= 0) {
            designTime = 1;
        }
        return designTime;
    }

    // Provides the percentage value for the amount of a task that a developer will test.
    public static int amountTested(int testChance) {
        int amountTested = normalDistribution(testChance, TEN);
        if (amountTested <= 0) {
            amountTested = 1;
        }
        return amountTested;
    }

    // Provides the value used to determine the chance that a story in Test Stage will fail testing.
    public static int testingRNG() {
        MIN = 0;
        MAX = TEN * TEN;
        return (uniformDistribution(MIN, MAX));
    }

    // Provides the value for the amount of time to copmlete a Testing Stage defect given the
    // coverage, a value for the percentage of lines of code covered.
    public static int defectTime(int coverage) {
        MAX = TWO_DAYS;
        MIN = (MAX - (MAX * coverage));
        return (uniformDistribution(MIN, MAX));
    }

    // Provides the valie for determining the chance that an outage will occur in Production stage.
    public static int outageRNG() {
        MIN = 0;
        MAX = TEN * TEN * TEN;
        return (uniformDistribution(MIN, MAX));
    }

    // Provides the value for the time to complete a Production Stage outage.
    public static int outageTime(int coverage) {
        MAX = FIVE_DAYS;
        MIN = (MAX - (MAX * coverage));
        return  (uniformDistribution(MIN, MAX));
    }

    // Provides a value for the number of lines of code of a task based on the time the task will take.
    // TODO verify that the casting to int of time doesn't cause an error later on.
    public static int getNumLines(int time) {
        int numLines = normalDistribution((int)(time * .8), TEN);
        if (numLines <= 0) {
            numLines = 1;
        }
        return numLines;
    }
}
