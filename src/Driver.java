public class Driver {
    public static void main(String[] args) {
        boolean doDebug = false;
        int useAltModel = 0;
        if (args.length > 0) {
            if (args[0] == "1") {
                useAltModel = 1;
            } else if ("showfel".equalsIgnoreCase(args[0])) {
                doDebug = true;
            }
        }
        if (args.length > 1) {
            if (args[1] == "1") {
                useAltModel = 1;
            } else if ("showfel".equalsIgnoreCase(args[1])) {
                doDebug = true;
            }
        }
        SoftwareSim ss = new SoftwareSim(doDebug);
        ss.schedule(1, EventType./*TODO*/, null);

        long startTime = System.currentTimeMillis();
        ss.runSim();
        long endTime = System.currentTimeMillis();

        // Output results
        // TODO
    }
}
