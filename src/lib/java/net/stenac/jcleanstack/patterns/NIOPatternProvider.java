package net.stenac.jcleanstack.patterns;

import java.util.ArrayList;
import java.util.List;

import net.stenac.jcleanstack.CleanerPattern;
import net.stenac.jcleanstack.PatternProvider;

public class NIOPatternProvider implements PatternProvider{

    @Override
    public List<CleanerPattern> getPatterns() {
        List<CleanerPattern> out = new ArrayList<CleanerPattern>();

        out.add(new CleanerPattern("NIO Selector", new String[] {
                "sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)",
                "sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:136)",
                "sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:69)",
                "sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:69)",
                "sun.nio.ch.SelectorImpl.select(SelectorImpl.java:80)",
        }));

        return out;
    }

}
