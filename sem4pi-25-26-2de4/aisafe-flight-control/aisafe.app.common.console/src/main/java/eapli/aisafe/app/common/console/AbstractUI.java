package eapli.aisafe.app.common.console;

import java.util.Scanner;

/**
 * Base class for console user interfaces.
 *
 * <p>
 * This class provides a shared {@link Scanner} for reading console input and
 * defines the common template method used by all console UI classes.
 * </p>
 */
public abstract class AbstractUI {
    protected final Scanner scanner = new Scanner(System.in);

    /**
     * Shows the UI screen.
     *
     * <p>
     * The concrete screen behavior is implemented by subclasses in
     * {@link #doShow()}.
     * </p>
     */
    public void show() {
        doShow();
    }

    /**
     * Executes the concrete UI behavior.
     *
     * @return a boolean value that may be used by subclasses to indicate
     * whether the UI completed normally or should return/back out
     */
    protected abstract boolean doShow();
}
