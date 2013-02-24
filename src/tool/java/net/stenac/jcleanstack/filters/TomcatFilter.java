package net.stenac.jcleanstack.filters;

import net.stenac.jcleanstack.ThreadData;
import net.stenac.jcleanstack.ThreadFilter;
import net.stenac.jcleanstack.ThreadData.State;

public class TomcatFilter implements ThreadFilter {
    //    SE sun.misc.Unsafe -- park  -1
    //    SE java.util.concurrent.locks.LockSupport -- park  156
    //    SE java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject -- await  1987
    //    SE java.util.concurrent.LinkedBlockingQueue -- take  399
    //    SE org.apache.tomcat.util.threads.TaskQueue -- take  104
    //    SE org.apache.tomcat.util.threads.TaskQueue -- take  32
    //    SE java.util.concurrent.ThreadPoolExecutor -- getTask  947
    //    SE java.util.concurrent.ThreadPoolExecutor$Worker -- run  907
    //    SE java.lang.Thread -- run  680


    //    SE java.net.PlainSocketImpl -- socketAccept  -1
    //    SE java.net.PlainSocketImpl -- accept  408
    //    SE java.net.ServerSocket -- implAccept  462
    //    SE java.net.ServerSocket -- accept  430
    //    SE org.apache.tomcat.util.net.DefaultServerSocketFactory -- acceptSocket  60
    //    SE org.apache.tomcat.util.net.JIoEndpoint$Acceptor -- run  216
    //    SE java.lang.Thread -- run  680

    //    java.lang.Thread.State: TIMED_WAITING (sleeping)
    //    at java.lang.Thread.sleep(Native Method)
    //    at org.apache.tomcat.util.net.JIoEndpoint$AsyncTimeout.run(JIoEndpoint.java:148)
    //    at java.lang.Thread.run(Thread.java:680)

    //    at java.lang.Thread.sleep(Native Method)
    //    at org.apache.catalina.core.ContainerBase$ContainerBackgroundProcessor.run(ContainerBase.java:1508)
    //    at java.lang.Thread.run(Thread.java:680)

    @Override
    public void matches(ThreadData stack) {
        if (stack.matches(0, "sun.misc.Unsafe", "park") &&
                stack.matches(1, "java.util.concurrent.locks.LockSupport", "park") &&
                stack.matches(4, "org.apache.tomcat.util.threads.TaskQueue", "take") && 
                stack.matches(6, "java.util.concurrent.ThreadPoolExecutor", "getTask")) {
            stack.state = State.ONE_LINER;
            stack.filterReason = "Tomcat executor thread";
            return;
        }
        if (stack.matches(0, "java.net.PlainSocketImpl", "socketAccept") &&
                stack.matches(4, "org.apache.tomcat.util.net.DefaultServerSocketFactory", "acceptSocket") &&
                stack.matches(5, "org.apache.tomcat.util.net.JIoEndpoint$Acceptor", "run")) {
            stack.state = State.ONE_LINER;
            stack.filterReason = "Tomcat Acceptor thread";
            return;
        }
        if (stack.matches(0, "java.lang.Thread", "sleep") && 
                stack.matches(1, "org.apache.tomcat.util.net.JIoEndpoint$AsyncTimeout", "run") &&
                stack.stack.size() == 3) {
            stack.state = State.ONE_LINER;
            
            stack.filterReason = "Tomcat Async IO timeout thread";
            return;
        }
        if (stack.matches(0, "java.lang.Thread", "sleep") && 
                stack.matches(1, "org.apache.catalina.core.ContainerBase$ContainerBackgroundProcessor", "run") &&
                stack.stack.size() == 3) {
            stack.state = State.ONE_LINER;
            stack.filterReason = "Tomcat Container background processor";
            return;
        }
    }
}
