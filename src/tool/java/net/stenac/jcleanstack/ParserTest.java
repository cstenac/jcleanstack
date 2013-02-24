package net.stenac.jcleanstack;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.junit.Test;

public class ParserTest {
    @Test
    public void a() throws Exception {
        char[] buf = new char[200000];

        int read = new FileReader(new File("/tmp/test")).read(buf);

        String data = new String(buf, 0, read);

        ApplicationData threads = JCleanStack.parse(data);
        JCleanStack.filter(threads);
        
        JCleanStack.print(threads);
/*
        for (ThreadStack ts : list) {
            System.out.println("Thread");
            for (StackElt se : ts.stack) {
                System.out.println("  SE " + se.className + " -- " + se.method + "  " + se.line);
            }
        }
        */
    }
}
