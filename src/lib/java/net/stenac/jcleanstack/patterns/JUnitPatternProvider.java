package net.stenac.jcleanstack.patterns;

import java.util.ArrayList;
import java.util.List;

import net.stenac.jcleanstack.CleanerPattern;
import net.stenac.jcleanstack.PatternProvider;

public class JUnitPatternProvider implements PatternProvider {

    @Override
    public List<CleanerPattern> getPatterns() {
        List<CleanerPattern> out = new ArrayList<CleanerPattern>();
        out.add(new CleanerPattern("JUnit runner", new String[] {
                "org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:45)",
                "org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)",
                "org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:42)",
                "org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)",
                "org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)",
                "org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:68)",
                "org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:47)",
                "org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)",
                "org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)",
                "org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)",
                "org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)",
                "org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)",
                "org.junit.runners.ParentRunner.run(ParentRunner.java:300)",
        }));
        return out;
    }
}