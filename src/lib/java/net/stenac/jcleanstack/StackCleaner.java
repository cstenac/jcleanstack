package net.stenac.jcleanstack;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.stenac.jcleanstack.StackElt.Type;
import net.stenac.jcleanstack.patterns.JUnitPatternProvider;
import net.stenac.jcleanstack.patterns.JettyPatternProvider;
import net.stenac.jcleanstack.patterns.NIOPatternProvider;
import net.stenac.jcleanstack.patterns.RMIPatternProvider;
import net.stenac.jcleanstack.patterns.SocketIOPatternProvider;

public class StackCleaner {
    private static List<CleanerPattern> patterns = new ArrayList<CleanerPattern>();
    static {
        patterns.addAll(new NIOPatternProvider().getPatterns());
        patterns.addAll(new JettyPatternProvider().getPatterns());
        patterns.addAll(new SocketIOPatternProvider().getPatterns());
        patterns.addAll(new RMIPatternProvider().getPatterns());
        patterns.addAll(new JUnitPatternProvider().getPatterns());
    }
    
    private static boolean eltMatchesLine(String line, StackElt elt) {
        StackElt toMatch = StackElt.read("at " + line);
        if (elt.method.equals(toMatch.method) && 
            elt.className.equals(toMatch.className)) {
            if (toMatch.file != null && !toMatch.file.equals(elt.file)) return false;
            if (toMatch.line > 0 && elt.line > 0 && toMatch.line != elt.line) return false;
            return true;
        }
        return false;
    }
    
    // Return the number of stack lines removed. -1 if not match
    private static int checkFurther(CleanerPattern pattern, List<StackElt> in, int firstLine) {
        int curStackLine = firstLine;
        int curPatLine = 0;
        while (true) {
            if (curPatLine == pattern.lines.length) return curStackLine - firstLine;
            if (curStackLine >= in.size()) return -1;
            if (in.get(curStackLine).type != Type.STANDARD) {
                curStackLine ++;
                continue;
            }

            if (eltMatchesLine(pattern.lines[curPatLine], in.get(curStackLine))) {
                curStackLine++;
                curPatLine++;
            } else {
                return -1;
            }
        }
    }
    
    public static String formatCleanStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        
        String stackTrace = sw.toString();
        return formatCleanStackTrace(stackTrace);
    }
    public static String formatCleanStackTrace(String stackTrace) {
        List<StackElt> in = new ArrayList<StackElt>();
        String[] lines = stackTrace.split("\n");
        for (String line : lines) {
            in.add(StackElt.read(line));
        }
        List<StackElt> out = StackCleaner.clean(in);
        return formatStackElts(out);
    }
    public static String formatStackElts(List<StackElt> elts) {
        StringBuilder sb = new StringBuilder();
        for (StackElt se : elts) {
            if (se.type == Type.RAW) {
                sb.append(se.rawData);
                sb.append('\n');
            } else {
            sb.append('\t');
            sb.append(se.format());
            sb.append('\n');
            }
        }
        return sb.toString();
    }
    
    public static List<StackElt> clean(List<StackElt> in) {
        List<StackElt> out = new ArrayList<StackElt>();

        stNext : for (int i = 0; i < in.size(); i++) {
            if (in.get(i).type != StackElt.Type.STANDARD) {
                out.add(in.get(i));
                continue;
            }
            for (CleanerPattern pattern: patterns) {
//                System.out.println("CHECK " + in.get(i));
                if (eltMatchesLine(pattern.lines[0], in.get(i))) {
                    int patternMatches = checkFurther(pattern, in, i);
                    if (patternMatches > 0) {
                        out.add(new StackElt(pattern.description, pattern.lines.length));
                        System.out.println("Collapsed " + patternMatches + "/" +pattern.lines.length);
                        i += patternMatches - 1;
//                        System.out.println("NOW, i= " + i + "/" + in.size());
                        continue stNext;
                    }
                }
            }
//            System.out.println("add raw " + in.get(i));
            out.add(in.get(i));
        }
        return out;
    }
}
