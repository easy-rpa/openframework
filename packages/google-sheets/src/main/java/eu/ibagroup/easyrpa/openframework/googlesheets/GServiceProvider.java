package eu.ibagroup.easyrpa.openframework.googlesheets;

import com.google.api.services.sheets.v4.Sheets;

import java.util.HashMap;
import java.util.Map;

public class GServiceProvider {
    private static Map<String, Sheets> gServices = new HashMap<>();

    public static boolean isGServiceExists(String serviceId) {
        return gServices.containsKey(serviceId);
    }

    public static Sheets getGService(String serviceId) {
        return gServices.get(serviceId);
    }

    public static String addGService(Sheets service) {
        String serviceId = (Math.random() * 100) + "" + (System.currentTimeMillis() % 1000000);
        gServices.put(serviceId, service);
        return serviceId;
    }

    public static void stopGService(String serviceId) {
        gServices.remove(serviceId);
    }


}
