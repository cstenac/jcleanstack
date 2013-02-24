package net.stenac.jcleanstack.filters;

import java.util.regex.Pattern;

import net.stenac.jcleanstack.ThreadData;
import net.stenac.jcleanstack.ThreadFilter;
import net.stenac.jcleanstack.ThreadData.State;

public class GCThreadsFilter implements ThreadFilter {
    Pattern p = Pattern.compile("^Gang");
    @Override
    public void matches(ThreadData thread) {
        if (thread.threadName == null) return;
        
        if (p.matcher(thread.threadName).find() ||
            thread.threadName.equals("GC Daemon") ||
            thread.threadName.equals("Concurrent Mark-Sweep GC Thread") ||
            thread.threadName.equals("Surrogate Locker Thread (Concurrent GC)")
                ){
            thread.state = State.HIDDEN;
            thread.filterReason = "System thread (Java GC)";
        }
    }
}
