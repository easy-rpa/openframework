package eu.ibagroup.easyrpa.openframework.core.utils;

public class MemoryMonitor {

    private static Thread monitor;
    private static boolean stopMonitor = false;

    public static void run(int refreshIntervalSec) {
        if (monitor == null) {
            monitor = new Thread(() -> {
                while (true) {
                    System.out.println(
                            String.format("%d of %d (%d)",
                                    (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024,
                                    Runtime.getRuntime().totalMemory() / 1024 / 1024,
                                    Runtime.getRuntime().maxMemory() / 1024 / 1024)
                    );
                    if (stopMonitor) {
                        monitor = null;
                        stopMonitor = false;
                        System.out.println("Monitor stopped.");
                        return;
                    }
                    try {
                        Thread.sleep(refreshIntervalSec * 1000);
                    } catch (Exception e) {
                        //do nothing
                    }
                }
            });
            monitor.setDaemon(true);
            monitor.start();
        }
    }

    public static void stop() {
        stopMonitor = monitor != null;
    }
}
