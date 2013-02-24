package net.stenac.jcleanstack.filters;

import net.stenac.jcleanstack.ThreadData;
import net.stenac.jcleanstack.ThreadFilter;
import net.stenac.jcleanstack.ThreadData.State;

public class AnyWaitingFilter implements ThreadFilter{

    @Override
    public void matches(ThreadData stack) {
        if ("WAITING".equals(stack.threadState)) {
            stack.state = State.ONE_LINER;
            stack.filterReason = "WAITING state";
        }
        if ("TIMED_WAITING".equals(stack.threadState)) {
            stack.state = State.ONE_LINER;
            stack.filterReason = "TIMED_WAITING state";
        }
        if ("RUNNABLE".equals(stack.threadState) && stack.matches(0, "java.net.PlainSocketImpl", "socketAccept")) {
            stack.state = State.ONE_LINER;
            stack.filterReason = "Socket accept";
        }
        if (stack.matches(0, "sun.nio.ch.KQueueArrayWrapper", "kevent0") &&
            stack.containsStack("sun.nio.ch.SelectorImpl", "select")) {
            stack.state = State.ONE_LINER;
            stack.filterReason = "NIO select";
        }
    }

}
