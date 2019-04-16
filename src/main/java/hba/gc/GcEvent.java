package hba.gc;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.sun.management.GarbageCollectionNotificationInfo;

public class GcEvent {
	/** Order events from oldest to newest. */
	public static final Comparator<GcEvent> TIME_ORDER =
		(e1, e2) -> (int) (e1.getTimestamp() - e2.getTimestamp());
			

	/** Order events from newest to oldest. */
	public static final Comparator<GcEvent> REVERSE_TIME_ORDER =
		(e1, e2) -> (int) (e2.getTimestamp() - e1.getTimestamp());

		
	private final String name;
	private final GarbageCollectionNotificationInfo info;
	private final GcType type;
	private final long timestamp;
	
	public GcEvent(){
		name = "";
		info = null;
		type = GcType.UNKNOWN;
		timestamp = 0L;
	}
	
	public GcEvent(GarbageCollectionNotificationInfo info, long timestamp) {
		this.name = info.getGcName();
	    this.info = info;
	    this.type = getGcType(name);
	    this.timestamp = timestamp;
	}
	
	public String getName() {return name;}
	public GarbageCollectionNotificationInfo getInfo() {return info;}
	public GcType getType() {return type;}
	public long getTimestamp() {return timestamp;}
	
	

	static GcType getGcType(String name) {
	    GcType t = KNOWN_COLLECTOR_NAMES.get(name);
	    return (t == null) ? GcType.UNKNOWN : t;
	}
	
	private static final Map<String, GcType> KNOWN_COLLECTOR_NAMES = knownCollectors();
	private static Map<String, GcType> knownCollectors() {
	    Map<String, GcType> m = new HashMap<>();
	    m.put("ConcurrentMarkSweep",  GcType.OLD);
	    m.put("Copy",                 GcType.YOUNG);
	    m.put("G1 Old Generation",    GcType.OLD);
	    m.put("G1 Young Generation",  GcType.YOUNG);
	    m.put("MarkSweepCompact",     GcType.OLD);
	    m.put("PS MarkSweep",         GcType.OLD);
	    m.put("PS Scavenge",          GcType.YOUNG);
	    m.put("ParNew",               GcType.YOUNG);
	    return Collections.unmodifiableMap(m);
	}
}
