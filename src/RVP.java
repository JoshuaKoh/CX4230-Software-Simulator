import java.util.Random;

public class RVP {
    static Random rand = new Random();

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
    	return rand.nextGaussian()*stdDev+mean;
    }
    // TODO
}
