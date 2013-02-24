package net.stenac.jcleanstack.filters;

import net.stenac.jcleanstack.ThreadData;
import net.stenac.jcleanstack.ThreadFilter;
import net.stenac.jcleanstack.ThreadData.State;

public class EclipseThreadsFilter implements ThreadFilter {
    public void matches(ThreadData data) {
        if ("org.eclipse.jdt.internal.ui.text.JavaReconciler".equals(data.threadName) &&
                data.matches(0, "java.lang.Object", "wait") &&
                data.matches(1, "org.eclipse.jface.text.reconciler.AbstractReconciler$BackgroundThread", "run")) {
            data.state = State.ONE_LINER;
            data.filterReason = "Eclipse reconciler";
        }
        if (data.matches(2, "org.eclipse.core.internal.jobs.WorkerPool", "startJob") &&
            data.matches(1, "org.eclipse.core.internal.jobs.WorkerPool", "sleep") &&
            "TIMED_WAITING".equals(data.threadState)) {
            data.state = State.ONE_LINER;
            data.filterReason = "Eclipse Worker";
        }
    }
}
