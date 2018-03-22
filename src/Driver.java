
public class Driver {
    public static void main(String[] args) {
        boolean doDebug = false;
        int limitInput = 0;
        int testChance = 0;
        boolean isSingleStory = false;

        for (int i = 0; i < args.length; i++) {
            if ("help".equals(args[i])) {
                printUsageAndExit();
            } else if ("-l".equals(args[i])) {
                if (i+1 < args.length && isNumeric(args[i+1])) {
                    limitInput = Integer.parseInt(args[i+1]);
                    i++;
                } else {
                    printUsageAndExit();
                }
            } else if ("-t".equals(args[i])) {
                if (i+1 < args.length && isNumeric(args[i+1])) {
                    testChance = Integer.parseInt(args[i+1]);
                    i++;
                } else {
                    printUsageAndExit();
                }
            } else if ("-s".equals(args[i])) {
                if (i + 1 < args.length && isNumeric(args[i + 1])) {
                    int val = Integer.parseInt(args[i + 1]);
                    if (val == 1) {
                        isSingleStory = true;
                    } else if (val != 0) {
                        printUsageAndExit();
                    }
                    i++;
                } else {
                    printUsageAndExit();
                }
            } else if ("-d".equals(args[i])) {
                doDebug = true;
            } else {
                printUsageAndExit();
            }
        }

        SoftwareSim ss = new SoftwareSim(doDebug, limitInput, testChance, isSingleStory);
        ss.schedule(1, EventType./*TODO*/, null);

        long startTime = System.currentTimeMillis();
        ss.runSim();
        long endTime = System.currentTimeMillis();

        // Output results
        System.out.println(
                "OUTPUTS:" + "\n" +
                        "-----------------------" + "\n" +
                        "Stories completed:\t\t\t" +        ss.completedStories + "\n" +
                        "Defects completed:\t\t\t" +        ss.completedDefects + "\n" +
                        "Repairs completed:\t\t\t" +        ss.completedRepairs + "\n" +
                        "Total tasks completed: \t\t" +     ss.completedTasks + "\n" +
                        "-----------------------" + "\n" +
                        "Developer time spent on:" + "\n" +
                        "Stories:\t\t\t" +      ss.devTimeOnStories + "\n" +
                        "Defects:\t\t\t" +      ss.devTimeOnDefects + "\n" +
                        "Repairs:\t\t\t" +      ss.devTimeOnRepairs + "\n" +
                        "Switching Tasks:\t" +  ss.devTimeOnSwitch + "\n" +
                        "-----------------------" + "\n" +
                        "END OF SIMULATION"
        );
    }

    private static void printUsageAndExit() {
        System.out.println("" +
                "Usage:" +
                "\nhelp \tShow this usage text." +
                "\n-d\tPrint the FEL after each event." +
                "\n-l\tHow long does Design Stage wait before creating a new story? Defaults to 0." +
                "\n-s\t1 if developers should limit to working one story at a time, 0 otherwise. Defaults to 0." +
                "\n-t\tWhat percentage of code should developers test? 100 for maximum, 0 for minimum. Defaults to 0.");
        System.exit(0);
    }

    private static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
