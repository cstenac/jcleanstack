package net.stenac.jcleanstack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class StackElt {
    public enum Type {
        STANDARD,
        RAW,
        COLLAPSE
    }

    public Type type;
    public int line;
    public String file;
    public String className;
    public String method;

    public String collapseReason;
    public int collapseNb;

    public String rawData;

    static Pattern stackLinePattern = Pattern.compile("at ([-A-z0-9$\\._]*)\\(([-A-z0-9$\\._: ]*)\\)");

    public static StackElt read(String line) {
        System.out.println(line);
        Matcher m = stackLinePattern.matcher(line);
        if (m.find()) {
            String classAndMethod = m.group(1);
            String fileAndLine = m.group(2);          
            String[] cmChunks = classAndMethod.split("\\.");
            if (cmChunks.length >= 2) {
                String className = StringUtils.join(ArrayUtils.subarray(cmChunks, 0, cmChunks.length - 1), ".");
                String methodName = cmChunks[cmChunks.length -1];
                if (fileAndLine.contains(":")) {
                    return new StackElt(className, methodName, fileAndLine.split(":")[0], Integer.parseInt(fileAndLine.split(":")[1]));
                } else {
                    return new StackElt(className, methodName, null, -1);
                }
            } else {
                return new StackElt(line);
            }
        } else {
            return new StackElt(line);
        }
    }

    public StackElt(String className, String method, String file, int line) {
        this.type = Type.STANDARD;
        this.className = className;
        this.method = method;
        this.file = file;
        this.line = line;
    }

    public StackElt(String data) {
        this.type = Type.RAW;
        this.rawData = data;
    }

    public StackElt(String collapsed, int collapseNb) {
        this.type = Type.COLLAPSE;
        this.collapseNb = collapseNb;
        this.collapseReason = collapsed;
    }

    public String format() {
        if (this.type == Type.STANDARD) {
            return className + "." + method + (file == null ? "(Unknown location)" : "(" + file + ":" + line + ")");
        } else if (this.type == Type.RAW) {
            return this.rawData;
        } else {
            return "[" + this.collapseNb + " hidden frames: " + this.collapseReason + "]";
        }
    }

    public String toString() {
        if (this.type == Type.STANDARD) {
            return this.type + " C=" + className + " m=" + method + " (f=" + file + " l=" + line + ")";
        } else {
            return this.type + " " +this.rawData + " " + this.collapseReason + " " + this.collapseNb;
        }
    }

    public boolean matches(String className, String method) {
        if (this.className == null || this.method == null) return false;
        return this.className.equals(className) && this.method.equals(method);
    }

    public boolean matches(String className, String method, int line) {
        if (this.className == null || this.method == null) return false;
        return this.className.equals(className) && this.method.equals(method) && this.line == line;    
    }
}
