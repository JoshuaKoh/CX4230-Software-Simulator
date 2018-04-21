import java.util.ArrayList;

public class SoftwareSim extends Engine {
    // CONSTANTS
    final int OUTAGE_CHECK = 96;
    final int WAIT_TIME = 10;
    final int RATE_OF_DESIGN = 96;
    final int TESTING_TIME = 45;
    final int PRODUCTION_TIME = 30;

    // PARAMETERS
    boolean doDebug;
    int limitInput;
    int testChance;
    boolean isSingleStory;

    // GROUP/QUEUE ENTITIES
    Task devTodoQueueHead;
    int availableDevelopers;

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

        devTodoQueueHead = new Task(-1, 0, 0, 0, 0, TaskType.DEV_HEAD);
        availableDevelopers = 3;

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
    // PRECONDITIONS
    //////////////////////

    public void preConditions() {
        if (isDevAvailableForNewStory()) {
            Task taskToWorkOn = popFromDevQueue();
            availableDevelopers--;
            int newTime = now + WAIT_TIME;
            schedule(newTime, EventType.DEVELOPER_FINISHES_WAITING, taskToWorkOn);
        } else if (false) {
            // TODO add more preconditions as needed
        }
    }

    private boolean isDevAvailableForNewStory() {
        return (availableDevelopers > 0 && devTodoQueueHead.next != null);
    }


    //////////////////////
    // EVENTS
    //////////////////////

    @Override
    public void executeEvent(BEvents event) {
        switch (event.eventType) {
            case CREATE_DESIGN_STORY:
                addNewStoryToDevQueue();
                bootstrapNewDesignStory();
                break;
            case DEVELOPER_FINISHES_WAITING:
                scheduleTaskCompletion((Task) event.eventData);
                break;
            case DEVELOPER_FINISHES_TASK:
                scheduleTestCompletion((Task) event.eventData);
                break;
            case TASK_FINISHES_TESTING:
                scheduleProductionCompletion((Task) event.eventData);
            break;
            case CHECK_FOR_OUTAGE:
                // TODO verify checkForOutage
                // TODO add additional logic as necessary
                checkForOutage((Task) event.eventData);
            break;
            case TASK_FINISHES_PRODUCTION:
                scheduleCheckForOutage((Task) event.eventData);
            break;
            default: // Should never happen
                System.out.println("Unexpected event type: " + event.eventType.toString() + "!");
                System.exit(1);
        }
        preConditions();
    }

    private void addNewStoryToDevQueue() {
        // TODO verify workTime is set correctly
        Task newStory = new Task(now, 0, RVP.designTime(), RVP.getNumLines(200), RVP.amountTested(100), TaskType.STORY);
        addToDevQueue(newStory);
    }

    private void bootstrapNewDesignStory() {
        int newTime = now + RATE_OF_DESIGN;
        schedule(newTime, EventType.CREATE_DESIGN_STORY, null);
    }

    private void scheduleTaskCompletion(Task t) {
        int newTime = now + t.totalTime;
        schedule(newTime, EventType.DEVELOPER_FINISHES_TASK, t);
    }

    private void scheduleTestCompletion(Task t) {
        int newTime = now + TESTING_TIME;
        schedule(newTime, EventType.TASK_FINISHES_TESTING, t);
    }

    private void scheduleProductionCompletion(Task t) {
        int newTime = now + PRODUCTION_TIME;
        int failureRisk = RVP.normalDistribution(50, 10);
        if (t.percentToTest*100 < failureRisk) {
            //TODO verify workTime is set correctly; how are we determining the workTime?
            Task defectFix = new Task(now, 0, RVP.defectTime(t.percentToTest), RVP.getNumLines(t.lines), RVP.amountTested(100), TaskType.DEFECT_FIX);
            addToDevQueue(defectFix);
        } else {
            schedule(newTime, EventType.TASK_FINISHES_PRODUCTION, t);
        }
    }

    private void checkForOutage(Task t) {
        //TODO what is our standard for determining when an outage has occured?

        int chanceOfOutage = RVP.outageRNG();
        if (chanceOfOutage > 30){
            //outage occurs
            Task repairFix = new Task(now, 0, RVP.outageTime(testChance), t.lines, t.percentToTest, TaskType.REPAIR);
            addToDevQueue(repairFix);
        } else {
            //no outage
            // TODO determine what, if anything, should be done if no outage occurs.
            updateCountersOfCompletedTasks(t);
        }
    }

    private void updateCountersOfCompletedTasks(Task t) {
        //Update counters for completed tasks
        if (t.type == TaskType.STORY) {
            completedStories += 1;
        } else if (t.type == TaskType.DEFECT_FIX) {
            completedDefects += 1;
        } else {
            completedRepairs += 1;
        }
        completedTasks += 1;
    }

    private void scheduleCheckForOutage(Task t) {
        int newTime = now + OUTAGE_CHECK;
        schedule(newTime, EventType.CHECK_FOR_OUTAGE, t);
    }


    //////////////////////
    // EVENT UTILITY
    //////////////////////

    /**
     * Uses the same logic as the FEL, inserting a new Task into the dev queue,
     * while maintaining sorted order.
     * Thus, removing from the dev queue will always result in the highest priority task being returned.
     */
    private void addToDevQueue(Task t) {
        if (devTodoQueueHead.next == null) {
            devTodoQueueHead.next = t;
        } else {
            Task temp = devTodoQueueHead;
            while (temp.next != null && t.compareTo(temp) < 1) {
                temp = temp.next;
            }
            t.next = temp.next;
            temp.next = t;
        }
    }

    /**
     * Returns the highest priority task from the dev queue.
     */
    private Task popFromDevQueue() {
        if (devTodoQueueHead.next == null) {
            System.out.println("Tried to pop from dev queue, but no tasks found!");
            System.exit(1);
        }

        Task temp = devTodoQueueHead.next.next;
        Task popMe = devTodoQueueHead.next;
        devTodoQueueHead.next = temp;
        return popMe;
    }

    /**
     * So long as simulation time has not expired and the FEL is not empty (this second condition should never happen),
     * Remove the soonest event from the FEL.
     * Update the global clock to match this event.
     * Execute the event. Often, this will cause new events to be scheduled and placed in the FEL.
     * Repeat.
     */
    public void runSim() {
        debugPrint(doDebug, "Initial event list:");
        printList(FEL);

        // Main scheduler loop
        BEvents currentEvent = popFromFEL();
        while (currentEvent != null) {
            assert(currentEvent.timestamp >= now);

            if (doDebug) printList(FEL);

            now = currentEvent.timestamp;
            debugPrint(doDebug, "- " + now + ": NOW RUNNING: " + currentEvent.eventType.toString());
            executeEvent(currentEvent);

            if (now >= 2880) {
                break;
            }
            currentEvent = popFromFEL();
        }
    }
}
