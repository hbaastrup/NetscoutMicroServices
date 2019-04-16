package hba.gc;

public enum GcType {
	OLD,      // Major collection
	YOUNG,    // Minor collection
	UNKNOWN;  // Could not determine the collection type

}
