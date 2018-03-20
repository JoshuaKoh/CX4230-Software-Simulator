public class BEvents {
    int timestamp;
    BEvents next;
    EventType eventType;
    Object eventData;


    public BEvents(int timestamp, EventType eventType, Object eventData) {
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.eventData = eventData;
    }

    @Override
    public String toString() {
        return Integer.toString(this.timestamp) + ": " + this.eventType.toString();
    }
}
