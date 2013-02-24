package net.stenac.jcleanstack;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class CleanerTest {
    public void inner() {
        throw new IllegalArgumentException("inner exception");
    }

    public void outer() {
        try {
            inner();
        } catch (Exception e) {
            throw new IllegalArgumentException("outer", e);
        }
    }

    @Test
    public void first(){
        try {
            outer();
        } catch (Exception e) {
            System.out.println(StackCleaner.formatCleanStackTrace(e));
        }

    }
    @Test
    @Ignore
    public void a(){
        String x = 
                "        at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)\n"+
                        "        at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:136)\n"+
                        "        at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:69)\n"+
                        "        at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:69)\n"+
                        "        - locked <7bd6e1260> (a sun.nio.ch.Util$2)\n"+
                        "        - locked <7bd6e1270> (a java.util.Collections$UnmodifiableSet)\n"+
                        "        - locked <7bd6e1218> (a sun.nio.ch.KQueueSelectorImpl)\n"+
                        "        at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:80)\n"+
                        "       at org.mortbay.io.nio.SelectorManager$SelectSet.doSelect(SelectorManager.java:498)\n"+
                        "        at org.mortbay.io.nio.SelectorManager.doSelect(SelectorManager.java:192)\n"+
                        "        at org.mortbay.jetty.nio.SelectChannelConnector.accept(SelectChannelConnector.java:124)\n"+
                        "        at org.mortbay.jetty.AbstractConnector$Acceptor.run(AbstractConnector.java:708)\n"+
                        "        at org.mortbay.thread.QueuedThreadPool$PoolThread.run(QueuedThreadPool.java:582)\n";
        /*
        String[] lines = x.split("\n");


        List<StackElt> in = new ArrayList<StackElt>();
        for (String line : lines) {
            in.add(StackElt.read(line));
        }
        List<StackElt> out = StackCleaner.clean(in);
        System.out.println("****************");
        for (StackElt se : out) {
            System.out.println(se);
        }*/

        System.out.println(StackCleaner.formatCleanStackTrace(x));
    }
}