package net.stenac.jcleanstack.patterns;

import java.util.ArrayList;
import java.util.List;

import net.stenac.jcleanstack.CleanerPattern;
import net.stenac.jcleanstack.PatternProvider;

public class RMIPatternProvider implements PatternProvider {

    @Override
    public List<CleanerPattern> getPatterns() {
        List<CleanerPattern> out = new ArrayList<CleanerPattern>();
        out.add(new CleanerPattern("RMI Server", new String[] {
                "sun.management.jmxremote.LocalRMIServerSocketFactory$1.accept(LocalRMIServerSocketFactory.java:34)",
                "sun.rmi.transport.tcp.TCPTransport$AcceptLoop.executeAcceptLoop(TCPTransport.java:369)",
                "sun.rmi.transport.tcp.TCPTransport$AcceptLoop.run(TCPTransport.java:341)",
                "java.lang.Thread.run(Thread.java:680)"
        }));
        return out;
    }
}