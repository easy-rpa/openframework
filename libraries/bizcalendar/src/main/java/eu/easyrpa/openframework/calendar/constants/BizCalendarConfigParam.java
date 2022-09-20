package eu.easyrpa.openframework.calendar.constants;

/**
 * The list of configuration parameter names which can be specified within RPA platform to provide necessary
 * for BizCalendar object initialization.
 */
public class BizCalendarConfigParam {
    /**
     * Name of configuration parameter with DataStore name where holidays are hold.
     */
    public static final String DATASTORE_NAME= "bizcalendar.datastore.name";

    /**
     * Name of configuration parameter with days of week which are weekends.
     */
    public static final String WEEKENDS = "bizcalendar.weekends";

    private BizCalendarConfigParam() {
    }
}