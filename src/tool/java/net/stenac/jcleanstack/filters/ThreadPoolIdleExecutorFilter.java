package net.stenac.jcleanstack.filters;

import net.stenac.jcleanstack.ThreadData;
import net.stenac.jcleanstack.ThreadFilter;
import net.stenac.jcleanstack.ThreadData.State;

public class ThreadPoolIdleExecutorFilter implements ThreadFilter {
//    ead-4" prio=5 tid=7fd6d38f2800 nid=0x110682000 waiting on condition [110681000]
//    java.lang.Thread.State: WAITING (parking)
//         at sun.misc.Unsafe.park(Native Method)
//         - parking to wait for  <77614d5e0> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
//         at java.util.concurrent.locks.LockSupport.park(LockSupport.java:156)
//         at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:1987)
//         at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:399)
//         at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:947)
//         at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:907)
//         at java.lang.Thread.run(Thread.java:680)
    
    @Override
    public void matches(ThreadData stack) {
        if (stack.matches(0, "sun.misc.Unsafe", "park") &&
            stack.matches(1, "java.util.concurrent.locks.LockSupport", "park") &&
            stack.matches(4, "java.util.concurrent.ThreadPoolExecutor", "getTask")) {
            stack.state = State.ONE_LINER;
            stack.filterReason = "ThreadPool executor (idle)";
        }
    }

}
