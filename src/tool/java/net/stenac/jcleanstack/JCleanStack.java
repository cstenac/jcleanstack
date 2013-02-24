package net.stenac.jcleanstack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.stenac.jcleanstack.StackElt.Type;
import net.stenac.jcleanstack.ThreadData.State;
import net.stenac.jcleanstack.filters.AnyWaitingFilter;
import net.stenac.jcleanstack.filters.EclipseThreadsFilter;
import net.stenac.jcleanstack.filters.GCThreadsFilter;
import net.stenac.jcleanstack.filters.HotspotThreadsFilter;
import net.stenac.jcleanstack.filters.ThreadPoolIdleExecutorFilter;
import net.stenac.jcleanstack.filters.TomcatFilter;

import org.apache.commons.lang.mutable.MutableInt;

public class JCleanStack {
    private enum ParserState {
        HEADER,
        THREAD_HEADER,
        THREAD_STATE,
        THREAD_STACK,
        INTER_THREAD
    };

    static Pattern threadNamePattern = Pattern.compile("^\"([^\"]*)\"");
    static Pattern threadStatePattern = Pattern.compile("java.lang.Thread.State: ([A-Z_]*)");

    public static void filter(ApplicationData in, boolean filterAllWaiting) {
        List<ThreadFilter> filters = new ArrayList<ThreadFilter>();

        filters.add(new ThreadPoolIdleExecutorFilter());
        filters.add(new TomcatFilter());
        filters.add(new GCThreadsFilter());
        filters.add(new HotspotThreadsFilter());
        filters.add(new EclipseThreadsFilter());

        if (filterAllWaiting) {
            filters.add(new AnyWaitingFilter());
        }

        for (ThreadData ts : in.threads) {
            ts.state = net.stenac.jcleanstack.ThreadData.State.FULL_DISPLAY;
            for (ThreadFilter sf : filters) {
                sf.matches(ts);
                if (ts.state == net.stenac.jcleanstack.ThreadData.State.HIDDEN) {
                    MutableInt mi = in.hiddenThreadReasons.get(ts.filterReason);
                    if (mi == null) {
                        mi = new MutableInt(0);
                        in.hiddenThreadReasons.put(ts.filterReason, mi);
                    }
                    mi.increment();
                    break;
                } else if (ts.state == net.stenac.jcleanstack.ThreadData.State.ONE_LINER) break;
            }
        }

        /* Collapse useless data within stacks */
        for (ThreadData ts : in.threads) {
            List<StackElt> collapsedStack = StackCleaner.clean(ts.stack);
            ts.stack = collapsedStack;
        }
    }

    public static void print(ApplicationData data) {
        System.out.println(data.headerData.toString());
        for (int i = 0; i < data.threads.size(); i++) {
            ThreadData ts = data.threads.get(i);

            if (ts.state == State.ONE_LINER) {
                System.out.println(ts.threadHeader + " (filtered: " + ts.filterReason + ")");

                if (i < data.threads.size() - 1 && data.threads.get(i+1).state == State.FULL_DISPLAY) {
                    System.out.println("");
                }

            } else if (ts.state == State.FULL_DISPLAY) {
                System.out.println(ts.threadHeader);

                boolean hasCollapse = false;
                for (StackElt se : ts.stack) {
                    if (se.type == Type.COLLAPSE)  hasCollapse = true;
                }
                if (hasCollapse) {
                    System.out.println(StackCleaner.formatStackElts(ts.stack));
                } else {
                    System.out.println(ts.origData.toString());
                }
            }
        }
        if (data.hiddenThreadReasons.size() > 0) {
            System.out.println("");
        }
        for (Map.Entry<String, MutableInt> hidden : data.hiddenThreadReasons.entrySet()) {
            System.out.println("" + hidden.getValue().intValue() + " threads hidden: " + hidden.getKey());
        }
    }

    public static ApplicationData  parse(String data) throws IOException {
        BufferedReader br = new BufferedReader(new StringReader(data));
        ApplicationData out = new ApplicationData();
        ThreadData curThread = null;

        ParserState state = ParserState.HEADER;
        while (true) {
            String line = br.readLine();
            if (line == null) break;

            if (line.length() == 0) {
                if (curThread != null) {
                    out.threads.add(curThread);
                    curThread = null;
                }
                state = ParserState.INTER_THREAD;
                continue;
            } else if (state == ParserState.INTER_THREAD) {
                curThread = new ThreadData();
                curThread.threadHeader = line;
                Matcher m = threadNamePattern.matcher(line);
                if (m.find()) {
                    curThread.threadName = m.group(1);
                }
                state = ParserState.THREAD_HEADER;
                continue;
            } else if (state == ParserState.THREAD_HEADER || state == ParserState.THREAD_STATE) {
                curThread.origData.append(line);
                curThread.origData.append('\n');
            } else {
                out.headerData.append(line);
                out.headerData.append('\n');
            }

            Matcher stateMatcher = threadStatePattern.matcher(line);
            if (stateMatcher.find()) {
                state = ParserState.THREAD_STATE;
                curThread.threadState = stateMatcher.group(1);
            } else {
                StackElt se = StackElt.read(line);
                if (se != null) curThread.stack.add(se);
            }

        }
        if (curThread != null) {
            out.threads.add(curThread);
        }
        return out;
    }
}
