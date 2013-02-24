package net.stenac.jcleanstack.patterns;

import java.util.ArrayList;
import java.util.List;

import net.stenac.jcleanstack.CleanerPattern;
import net.stenac.jcleanstack.PatternProvider;

public class SocketIOPatternProvider implements PatternProvider {

    @Override
    public List<CleanerPattern> getPatterns() {
        List<CleanerPattern> out = new ArrayList<CleanerPattern>();
        out.add(new CleanerPattern("IO ServerSocket", new String[] {
                "java.net.PlainSocketImpl.socketAccept(Native Method)",
                "java.net.PlainSocketImpl.accept(PlainSocketImpl.java:408)",
                "java.net.ServerSocket.implAccept(ServerSocket.java:462)",
                "java.net.ServerSocket.accept(ServerSocket.java:430)"
        }));

        out.add(new CleanerPattern("LinkedBlockingQueue.take", new String[] {
                "sun.misc.Unsafe.park(Native Method)",
                "java.util.concurrent.locks.LockSupport.park(LockSupport.java:156)",
                "java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:1987)",
                "java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:399)"
        }));
        return out;
    }
}