package hba.gc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;

import hba.utils.CircularBuffer;

public class GcEventLogger {
	// One major GC per hour would require 168 for a week
	// One minor GC per minute would require 180 for three hours
	private static final int BUFFER_SIZE = 256;
	
	private long youngGenSizeAfter = 0L;
	
	private String youngGenPoolName = null;
	private String oldGenPoolName = null;
	
	private GcNotificationListener notifListener = null;
	
	private GcEventListener eventListener = null;
	
	private final ConcurrentHashMap<String, CircularBuffer<GcEvent>> gcLogs = new ConcurrentHashMap<>();
	
	private final long jvmStartTime;
	
	// Max size of old generation memory pool
	private long maxDataSize = 0;
	
	// Size of old generation memory pool after a full GC
	private long liveDataSize = 0;
	
	// Incremented for any positive increases in the size of the old generation memory pool
	// before GC to after GC
	private long promotionRate = 0;
	
	// Incremented for the increase in the size of the young generation memory pool after one GC
	// to before the next
	private long allocationRate = 0;
	
	public GcEventLogger() {
		jvmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
		
		for (GarbageCollectorMXBean mbean : ManagementFactory.getGarbageCollectorMXBeans()) {
			CircularBuffer<GcEvent> buffer = new CircularBuffer<>(BUFFER_SIZE);
			gcLogs.put(mbean.getName(), buffer);
		}
		
		for (MemoryPoolMXBean mbean : ManagementFactory.getMemoryPoolMXBeans()) {
			if (isYoungGenPool(mbean.getName()))
				youngGenPoolName = mbean.getName();
			if (isOldGenPool(mbean.getName()))
				oldGenPoolName = mbean.getName();
		}
	}
	
	
	public synchronized void start(GcEventListener listener) {
		if (notifListener != null) {
			//TODO: log warning
			return;
		}
		eventListener = listener;
		notifListener = new GcNotificationListener();
		
		for (GarbageCollectorMXBean mbean : ManagementFactory.getGarbageCollectorMXBeans()) {
			if (mbean instanceof NotificationEmitter) {
				NotificationEmitter emitter = (NotificationEmitter) mbean;
				emitter.addNotificationListener(notifListener, null, null);
			}
		}
	}
	
	public synchronized void stop() {
		if (notifListener == null)
			return;
		
		for (GarbageCollectorMXBean mbean : ManagementFactory.getGarbageCollectorMXBeans()) {
			if (mbean instanceof NotificationEmitter) {
				NotificationEmitter emitter = (NotificationEmitter) mbean;
				try {
					emitter.removeNotificationListener(notifListener);
				} catch (ListenerNotFoundException e) {
					//TODO: Log exception: could not remove gc listener
				}
			}
		}
		notifListener = null;
	}
	
	public List<GcEvent> getLogs() {
		ArrayList<GcEvent> logs = new ArrayList<>();
		for (CircularBuffer<GcEvent> buffer : gcLogs.values()) {
			for (GcEvent event : buffer.toList()) logs.add(event);
//			logs.addAll(buffer.toList());
			Collections.sort(logs, GcEvent.REVERSE_TIME_ORDER);
		}
		return logs;
	}
	
	
	
	private void updateMetrics(String name, GcInfo info) {
		Map<String, MemoryUsage> before = info.getMemoryUsageBeforeGc();
		Map<String, MemoryUsage> after = info.getMemoryUsageAfterGc();
		
		if (oldGenPoolName != null) {
			long oldBefore = before.get(oldGenPoolName).getUsed();
			long oldAfter = after.get(oldGenPoolName).getUsed();
			long delta = oldAfter - oldBefore;
			if (delta > 0L) {
				promotionRate += delta;
			}
			
			// Some GCs such as G1 can reduce the old gen size as part of a minor GC. To track the
			// live data size we record the value if we see a reduction in the old gen heap size or
			// after a major GC.
			if (oldAfter < oldBefore || GcEvent.getGcType(name) == GcType.OLD) {
				liveDataSize = oldAfter;
		        long oldMaxAfter = after.get(oldGenPoolName).getMax();
		        maxDataSize = oldMaxAfter;
			}
		}
		
		if (youngGenPoolName != null) {
			long youngBefore = before.get(youngGenPoolName).getUsed();
			long youngAfter = after.get(youngGenPoolName).getUsed();
			long delta = youngBefore - youngGenSizeAfter;
			youngGenSizeAfter = youngAfter;
			if (delta > 0L)
				allocationRate += delta;
		}
	}
	
	private void processGcEvent(GarbageCollectionNotificationInfo info) {
		GcEvent event = new GcEvent(info, jvmStartTime + info.getGcInfo().getStartTime());
		gcLogs.get(info.getGcName()).add(event);
		
//		// Update pause timer for the action and cause...
//	    Id eventId = (isConcurrentPhase(info) ? CONCURRENT_PHASE_TIME : PAUSE_TIME)
//	      .withTag("action", info.getGcAction())
//	      .withTag("cause", info.getGcCause());
//	    Timer timer = Spectator.globalRegistry().timer(eventId);
//	    timer.record(info.getGcInfo().getDuration(), TimeUnit.MILLISECONDS);
//		// Update promotion and allocation counters
		
		updateMetrics(info.getGcName(), info.getGcInfo());
		
		// Notify an event listener if registered
		if (eventListener != null) {
			try {
				eventListener.onComplete(event);
			} catch (Exception e) {
				//TODO: log exception: exception thrown by event listener
			}
		}
	}
	
	private boolean isOldGenPool(String name) {
		return name.endsWith("Old Gen") || name.endsWith("Tenured Gen");
	}
	
	private boolean isYoungGenPool(String name) {
	    return name.endsWith("Eden Space");
	}
	
	
	
	private class GcNotificationListener implements NotificationListener {
		@Override
		public void handleNotification(Notification notification, Object ref) {
			final String type = notification.getType();
			if (type.equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
				CompositeData cd = (CompositeData) notification.getUserData();
				GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from(cd);
				processGcEvent(info);
			}
		}
	}
}
