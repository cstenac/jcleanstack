package net.stenac.jcleanstack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;

public class ApplicationData {
    StringBuilder headerData = new StringBuilder();
    StringBuilder footerData = new StringBuilder();
    public List<ThreadData> threads = new ArrayList<ThreadData>();
    
    public Map<String, MutableInt> hiddenThreadReasons = new HashMap<String, MutableInt>(); 
}
