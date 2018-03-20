public abstract class Engine {
    int now = 0;
    BEvents FEL = new BEvents(-1, null, null);

    public void printList(BEvents head) {
        BEvents temp = head;
        while (temp.next != null) {
            temp = temp.next;
            System.out.println(temp);
        }
    }

    public BEvents remove() {
        if (FEL.next == null) {
            System.out.println("No new events in queue!");
            return null;
        }

        BEvents removed = FEL.next;
        FEL.next = removed.next;
        return removed;
    }

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
