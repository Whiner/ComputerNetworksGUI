package org.donntu.databaseworker;

import java.text.SimpleDateFormat;

public class GregorianCalendar extends java.util.GregorianCalendar {
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/DD/yyyy");
        return dateFormat.format(getTime());
    }
}
