package utils;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.TimeZone;

public class TimeZoneData {
    private static final Log log = new Log(TimeZoneData.class);

    private static TimeZone timezone = TimeZone.getTimeZone("EST");

    private static boolean checkedTimezone = false;

    static void checkTimezoneEnv() {
        if (!checkedTimezone) {
            final String env = System.getenv("TIMEZONE");
            if (env != null) {
                final boolean isRealTimezone = Arrays.asList(TimeZone.getAvailableIDs()).contains(System.getenv("TIMEZONE"));
                if (isRealTimezone) {
                    log.debug("Valid timezone set.", false);
                    timezone = TimeZone.getTimeZone(env);
                } else {
                    log.debug("Invalid timezone set.  Falling back to default - EST.", false);
                }
            } else {
                log.debug("No timezone set.  Falling back to default - EST.", false);
            }

            checkedTimezone = true;
        }
    }

    public static TimeZone getTimezone() {
        checkTimezoneEnv();
        return timezone;
    }

    public static ZoneId getZoneId() {
        checkTimezoneEnv();
        return timezone.toZoneId();
    }
}
