package net.stenac.jcleanstack;

import java.util.ArrayList;
import java.util.List;

public class ThreadData {
    public enum State {
        FULL_DISPLAY,
        ONE_LINER,
        HIDDEN
    }
    
    public String threadState;
    public String threadName;
    public String threadHeader;
    
    public StringBuilder origData = new StringBuilder();
    
    public State state;
    public String filterReason;
    
    public boolean matches(int stackElt, String className, String method) {
        if (stackElt >= stack.size()) {
            return false;
        }
        return stack.get(stackElt).matches(className, method);
    }
    public boolean containsStack(String className, String method) {
        for (StackElt se : stack) {
            if (se.matches(className, method)) return true;
        }
        return false;
    }
    
    public List<StackElt> stack = new ArrayList<StackElt>();
}