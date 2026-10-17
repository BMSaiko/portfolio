package pt.isep.dei.esinf.domain.enums;

public enum AllocationStatus {
    ELIGIBLE,       // Entire quantity allocated
    PARTIAL,        // 0 < allocated < requested
    UNDISPATCHABLE  // allocated = 0
}