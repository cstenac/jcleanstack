package net.stenac.jcleanstack.patterns;

import java.util.ArrayList;
import java.util.List;

import net.stenac.jcleanstack.CleanerPattern;
import net.stenac.jcleanstack.PatternProvider;

public class JettyPatternProvider implements PatternProvider {

    @Override
    public List<CleanerPattern> getPatterns() {
        List<CleanerPattern> out = new ArrayList<CleanerPattern>();

        out.add(new CleanerPattern("Jetty Acceptor", new String[] {
                "org.mortbay.io.nio.SelectorManager$SelectSet.doSelect(SelectorManager.java:498)\n",
                "org.mortbay.io.nio.SelectorManager.doSelect(SelectorManager.java:192)\n",
                "org.mortbay.jetty.nio.SelectChannelConnector.accept(SelectChannelConnector.java:124)\n",
                "org.mortbay.jetty.AbstractConnector$Acceptor.run(AbstractConnector.java:708)\n",
                "org.mortbay.thread.QueuedThreadPool$PoolThread.run(QueuedThreadPool.java:582)\n"
        }));

        return out;
    }
}
