import java.util.ArrayList;

public class SoftwareSim extends Engine {
    // CONSTANTS
    final int OUTAGE_CHECK = 96;
    final int WAIT_TIME = 10;
    final int RATE_OF_DESIGN = 96;
    final int TESTING_TIME = 45;

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
                devTimeOnSwitch += WAIT_TIME;
                scheduleTaskCompletion((Task) event.eventData);
                break;
            case DEVELOPER_FINISHES_TASK:
                availableDevelopers++;
                Task taskInDev = (Task) event.eventData;
                updateDevTimes(taskInDev);
                scheduleTestCompletion(taskInDev);
                break;
            case TASK_FINISHES_TESTING:
                Task taskInTest = (Task) event.eventData;
                boolean doesPassTesting = doesTaskPassTestingStage(taskInTest);
                if (doesPassTesting) {
                    scheduleChecksForOutage(taskInTest);
                    updateCountersOfCompletedTasks(taskInTest);
                }
            break;
            case CHECK_FOR_OUTAGE:
                checkForOutage((Task) event.eventData);
            break;
            default: // Should never happen
                System.out.println("Unexpected event type: " + event.eventType.toString() + "!");
                System.exit(1);
        }
        preConditions();
    }

    private void addNewStoryToDevQueue() {
        Task newStory = new Task(now, 0, RVP.designTime(), RVP.getNumLines(200), RVP.amountTested(testChance), TaskType.STORY);
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

    private void updateDevTimes(Task t) {
        if (t.type == TaskType.STORY) {
            devTimeOnStories += t.totalTime;
        } else if (t.type == TaskType.DEFECT_FIX) {
            devTimeOnDefects += t.totalTime;
        } else {
            devTimeOnRepairs += t.totalTime;
        }
    }

    private void scheduleTestCompletion(Task t) {
        int newTime = now + TESTING_TIME;
        schedule(newTime, EventType.TASK_FINISHES_TESTING, t);
    }

    private boolean doesTaskPassTestingStage(Task t) {
        int failureRisk = RVP.uniformDistribution(0, 100);
        if (t.percentToTest < failureRisk) {
            assert(t.workTime >= t.totalTime);
            int totalTime = RVP.defectTime(t.percentToTest);
            Task defectFix = new Task(now, 0, totalTime, RVP.getNumLines(totalTime), RVP.amountTested(testChance), TaskType.DEFECT_FIX);
            addToDevQueue(defectFix);
            return false;
        }
        return true;
    }

    private void scheduleChecksForOutage(Task t) {
        int firstCheck = now + OUTAGE_CHECK;
        int secondCheck = firstCheck + OUTAGE_CHECK;
        schedule(firstCheck, EventType.CHECK_FOR_OUTAGE, t);
        schedule(secondCheck, EventType.CHECK_FOR_OUTAGE, t);
    }

    private void checkForOutage(Task t) {
        int chanceOfOutage = RVP.outageRNG();
        if (chanceOfOutage < 30){
            //outage occurs
            int totalTime = RVP.outageTime(testChance);
            Task repairFix = new Task(now, 0, totalTime, RVP.getNumLines(totalTime), RVP.amountTested(testChance), TaskType.REPAIR);
            addToDevQueue(repairFix);
        }
    }

    /**
     * Update counters for completed tasks
     *
     * @param t     The task just completed
     */
    private void updateCountersOfCompletedTasks(Task t) {
        if (t.type == TaskType.STORY) {
            completedStories += 1;
        } else if (t.type == TaskType.DEFECT_FIX) {
            completedDefects += 1;
        } else {
            completedRepairs += 1;
        }
        completedTasks += 1;
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
