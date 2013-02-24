package net.stenac.jcleanstack.filters;

import net.stenac.jcleanstack.ThreadData;
import net.stenac.jcleanstack.ThreadFilter;
import net.stenac.jcleanstack.ThreadData.State;

public class HotspotThreadsFilter implements ThreadFilter {
    @Override
    public void matches(ThreadData thread) {
        if (thread.threadName == null) return;
        
        if (thread.threadName.equals("Attach Listener") && thread.stack.size() == 0) {
            thread.state = State.HIDDEN;
            thread.filterReason = "System thread (Java Hotspot)";
        } else if (thread.threadName.equals("Reference Handler") &&
                thread.matches(2, "java.lang.ref.Reference$ReferenceHandler", "run")) {
            thread.state = State.HIDDEN;
            thread.filterReason = "System thread (Java Hotspot)";
        } else if (thread.threadName.equals("Finalizer") &&
                thread.matches(5, "java.lang.ref.Finalizer$FinalizerThread", "run")) {
            thread.state = State.HIDDEN;
            thread.filterReason = "System thread (Java Hotspot)";
        } else if (thread.threadName.contains("C2 CompilerThread") ||
                thread.threadName.equals("Signal Dispatcher") ||
                thread.threadName.equals("Low Memory Detector") ||
                thread.threadName.equals("Exception Catcher Thread") ||
                thread.threadName.equals("VM Thread") ||
                thread.threadName.equals("VM Periodic Task Thread")
                ) {
            thread.state = State.HIDDEN;
            thread.filterReason = "System thread (Java Hotspot)";
        }
    }
}
