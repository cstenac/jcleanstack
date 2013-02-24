package net.stenac.jcleanstack;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class Main {
    private static String getJStackOutput(int pid) throws Exception {
        PrintStream prevOut = System.out;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream tempOut = new PrintStream(os);

            System.setOut(tempOut);
            Class<?> c = Class.forName("sun.tools.jstack.JStack");
            Method m = c.getMethod("main", String[].class);
            System.err.println(m);
            m.invoke(null, new Object[] { new String[]{""+pid} } );
            return new String(os.toByteArray());
        } finally {
            System.setOut(prevOut);
        }
    }

    private static String getJpsOutput() throws Exception {
        PrintStream prevOut = System.out;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream tempOut = new PrintStream(os);

            System.setOut(tempOut);
            Class<?> c = Class.forName("sun.tools.jps.Jps");
            Method m = c.getMethod("main", String[].class);
            m.invoke(null, new Object[] { new String[]{"-l", "-v" } } );
            return new String(os.toByteArray());
        } finally {
            System.setOut(prevOut);
        }
    }

    private static int getFirstPidMatching(String jps, String filter1, String filter2) {
        int firstPid = -1;
        for (String line : StringUtils.split(jps, '\n')) {
            String[] chunks = line.split(" ");
            if (chunks.length < 3) continue;
            
            String cmdLineArgs = StringUtils.join(chunks, " ", 2, chunks.length);

            if (chunks[1].contains(filter1) || cmdLineArgs.contains(filter2)) {
                if (firstPid > -1) {
                    System.err.println("WARNING: Several pids match your filter");
                } else {
                    firstPid = Integer.parseInt(chunks[0]);

                }
            }
        }
        return firstPid;
    }
    
    private static void usage(Options options) {
        HelpFormatter hf = new HelpFormatter();
        System.out.println( "Displays filtered stacks for a Java (Hotspot) application.\n" +
                "  jcleanstack <PID>               - Displays filtered stacks for process PID\n" +
                "  jcleanstack <FILTER>            - Displays filtered stacks for process where \n" +
                "                                    command line matches FILTER\n" +
                "  jcleanstack <CFILTER> <AFILTER> - Displays filtered stacks for process where \n" +
                "                                    class name matches CFILTER and command line\n" +
                "                                    matches AFILTER\n");
        hf.printHelp(" <PID> |Ê<FILTER> | <CFILTER> <AFILTER>", options);
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options();

        options.addOption("a", false, "Filter out all waiting and sleeping threads, not only known patterns");
        options.addOption("h", false, "Display help");

        Parser parser = new GnuParser();

        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        } catch (Exception e) {
            e.printStackTrace();
            usage(options);
        }
        
        if (line.hasOption('h')) {
            usage(options);
        }

        String input = null;
        if (line.getArgs().length == 0) {
            input  = IOUtils.toString(System.in);
        } else if (line.getArgs().length == 1 && StringUtils.isNumeric(line.getArgs()[0])) {
            int pid = Integer.parseInt(line.getArgs()[0]);
            input = getJStackOutput(pid);
        } else {
            String jps = getJpsOutput();
            int pid = getFirstPidMatching(jps, line.getArgs()[0], line.getArgs().length > 1 ? line.getArgs()[1] : line.getArgs()[0]);
            input = getJStackOutput(pid);
        }
        ApplicationData data = JCleanStack.parse(input);
        JCleanStack.filter(data, line.hasOption('a'));
        JCleanStack.print(data);

    }
}
