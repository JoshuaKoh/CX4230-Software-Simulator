public abstract class Engine {
    int now = 0;
    BEvents FEL = new BEvents(-1, null, null);

    public void printList(BEvents head) {
        System.out.println("Current event list:");
        BEvents temp = head;
        while (temp.next != null) {
            temp = temp.next;
            System.out.println(temp);
        }
    }

    public BEvents popFromFEL() {
        if (FEL.next == null) {
            System.out.println("No new events in queue!");
            return null;
        }

        BEvents removed = FEL.next;
        FEL.next = removed.next;
        return removed;
    }

    /**
     * Creates a new BEvent based in parameter data. Inserts BEvent into the FEL, such that the FEL remains
     * in ascending order of timestamp.
     * Note that the FEL contains one dummy node at the head which never changes and is always "less than" any event.
     * @param timestamp The time when this event will be triggered
     * @param eventType The type of event to process when timestamp is reached
     * @param eventData Any extra data that the event will need when being processed
     */
    public void schedule(int timestamp, EventType eventType, Object eventData) {
        BEvents newEvent = new BEvents(timestamp, eventType, eventData);
        BEvents temp = FEL;
        while (temp.next != null && temp.next.timestamp < newEvent.timestamp) {
            temp = temp.next;
        }
        newEvent.next = temp.next;
        temp.next = newEvent;
    }

    public void debugPrint(boolean isDebug, String print) {
        if (isDebug) {
            System.out.println(print);
        }
    }

    public abstract void executeEvent(BEvents event);
}
