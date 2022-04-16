package FalconClientSpigot.utils;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class MemoryUtil {


    public static long[] getFormattedMemory() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        int i = operatingSystemMXBean.getAvailableProcessors();
        Runtime runtime = Runtime.getRuntime();
        long l7 = runtime.maxMemory();
        long l8 = runtime.totalMemory();
        long l9 = runtime.freeMemory();
        long l10 = l7 - l8 - l9;
        long[] arr = new long[7];
        arr[0] = Thread.getAllStackTraces().keySet().size(); //running threads;
        arr[1] = i; //no.of cpu cores
        arr[2] = (long) getProcessCpuLoad(); //cpu load
        arr[3] = 100 - l10 * 100 / l7; // memory usage percentage
        arr[4] = (l7 - l10) / 1024L / 1024L; // current memory usage
        arr[5] = l7 / 1024L / 1024L; //max memory
        arr[6] = l8 / 1024L / 1024L; //allocated memory
        return arr;

    }

    public static String getOsName(){
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        return operatingSystemMXBean.getName();

    }

    public static double getProcessCpuLoad() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList attributeList = mBeanServer.getAttributes(objectName, new String[]{"ProcessCpuLoad"});
            if (attributeList.isEmpty())
                return 0.0D;
            Attribute attribute = (Attribute) attributeList.get(0);
            Double double_ = (Double) attribute.getValue();
            if (double_ == -1.0D)
                return 0.0D;
            return (int) (double_ * 10000.0D) / 100.0D;
        }
        catch (Exception e){

        }
        return 0.0D;
    }
}
