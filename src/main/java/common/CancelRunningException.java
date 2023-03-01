package common;

import org.omg.CORBA.portable.ApplicationException;

public final class CancelRunningException extends Exception {
    public CancelRunningException() {
        super("Operation is cancelled");
    }
}
