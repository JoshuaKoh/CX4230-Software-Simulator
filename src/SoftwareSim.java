public class SoftwareSim extends Engine {
    boolean isDebug = false;

    // CONSTANTS
    // TODO

    // PARAMETERS
    // TODO

    // GROUP/QUEUE ENTITIES
    // TODO

    // SSOVS
    // TODO

    public SoftwareSim(boolean debug) {
        isDebug = debug;
    }

    //////////////////////
    // ACTIVITIES
    //////////////////////
    public void preConditions() {
        // TODO
    }


    //////////////////////
    // EVENTS
    //////////////////////

    @Override
    public void executeEvent(BEvents event) {
        switch (event.eventType) {
            // TODO
        }
        preConditions();
    }

    public void runSim() {
        debugPrint(isDebug, "Initial event list:");
        printList(FEL);

        // Main scheduler loop
        BEvents currentEvent = remove();
        while (currentEvent != null) {
            assert(currentEvent.timestamp >= now);
            debugPrint(isDebug, "\nCurrent event list:");
            if (isDebug) printList(FEL);

            now = currentEvent.timestamp;
            debugPrint(isDebug, "- " + now + ": NOW RUNNING: " + currentEvent.eventType.toString());
            executeEvent(currentEvent);

            if (now >= 2880) {
                break;
            }
            currentEvent = remove();
        }
    }
}
