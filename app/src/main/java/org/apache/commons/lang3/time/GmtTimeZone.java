package org.apache.commons.lang3.time;

import java.util.Date;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
class GmtTimeZone extends TimeZone {
    private static final int HOURS_PER_DAY = 24;
    private static final int MILLISECONDS_PER_MINUTE = 60000;
    private static final int MINUTES_PER_HOUR = 60;
    static final long serialVersionUID = 1;
    private final int offset;
    private final String zoneId;

    GmtTimeZone(boolean negate, int hours, int minutes) {
        if (hours >= 24) {
            throw new IllegalArgumentException(hours + " hours out of range");
        }
        if (minutes >= 60) {
            throw new IllegalArgumentException(minutes + " minutes out of range");
        }
        int milliseconds = ((hours * 60) + minutes) * MILLISECONDS_PER_MINUTE;
        this.offset = negate ? -milliseconds : milliseconds;
        StringBuilder sb = new StringBuilder(9);
        sb.append(TimeZones.GMT_ID);
        sb.append(negate ? '-' : '+');
        StringBuilder sbTwoDigits = twoDigits(sb, hours);
        sbTwoDigits.append(':');
        this.zoneId = twoDigits(sbTwoDigits, minutes).toString();
    }

    private static StringBuilder twoDigits(StringBuilder sb, int n) {
        sb.append((char) ((n / 10) + 48));
        sb.append((char) ((n % 10) + 48));
        return sb;
    }

    @Override // java.util.TimeZone
    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {
        return this.offset;
    }

    @Override // java.util.TimeZone
    public void setRawOffset(int offsetMillis) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.TimeZone
    public int getRawOffset() {
        return this.offset;
    }

    @Override // java.util.TimeZone
    public String getID() {
        return this.zoneId;
    }

    @Override // java.util.TimeZone
    public boolean useDaylightTime() {
        return false;
    }

    @Override // java.util.TimeZone
    public boolean inDaylightTime(Date date) {
        return false;
    }

    public String toString() {
        return "[GmtTimeZone id=\"" + this.zoneId + "\",offset=" + this.offset + ']';
    }

    public int hashCode() {
        return this.offset;
    }

    public boolean equals(Object other) {
        return (other instanceof GmtTimeZone) && this.zoneId == ((GmtTimeZone) other).zoneId;
    }
}
