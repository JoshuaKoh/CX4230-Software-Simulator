import java.util.ArrayList;

public class SoftwareSim extends Engine {
    // CONSTANTS
    final int OUTAGE_CHECK = 96;

    // PARAMETERS
    boolean doDebug;
    int limitInput;
    int testChance;
    boolean isSingleStory;

    // GROUP/QUEUE ENTITIES
    // TODO

    // SSOVS
    int completedStories;
    int completedDefects;
    int completedRepairs;
    int completedTasks;
    ArrayList<Task> wipOverTime;
    int devTimeOnStories;
    int devTimeOnDefects;
    int devTimeOnRepairs;
    int devTimeOnSwitch;

    public SoftwareSim(boolean doDebug, int limitInput, int testChance, boolean isSingleStory) {
        this.doDebug = doDebug;
        this.limitInput = limitInput;
        this.testChance = testChance;
        this.isSingleStory = isSingleStory;

        completedStories = 0;
        completedDefects = 0;
        completedRepairs = 0;
        completedTasks = 0;
        wipOverTime = new ArrayList<Task>();
        devTimeOnStories = 0;
        devTimeOnDefects = 0;
        devTimeOnRepairs = 0;
        devTimeOnSwitch = 0;
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
        debugPrint(doDebug, "Initial event list:");
        printList(FEL);

        // Main scheduler loop
        BEvents currentEvent = remove();
        while (currentEvent != null) {
            assert(currentEvent.timestamp >= now);
            debugPrint(doDebug, "\nCurrent event list:");
            if (doDebug) printList(FEL);

            now = currentEvent.timestamp;
            debugPrint(doDebug, "- " + now + ": NOW RUNNING: " + currentEvent.eventType.toString());
            executeEvent(currentEvent);

            if (now >= 2880) {
                break;
            }
            currentEvent = remove();
        }
    }
}
