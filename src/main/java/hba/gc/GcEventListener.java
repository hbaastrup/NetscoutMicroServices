package hba.gc;

/** Listener for GC events. */
public interface GcEventListener {
	/** Invoked after a GC event occurs. */
	void onComplete(GcEvent event);
}
