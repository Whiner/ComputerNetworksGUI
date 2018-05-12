package org.donntu;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GregorianCalendar extends java.util.GregorianCalendar {

    public GregorianCalendar(){
        super();
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy года");
        return dateFormat.format(getTime());
    }


}
