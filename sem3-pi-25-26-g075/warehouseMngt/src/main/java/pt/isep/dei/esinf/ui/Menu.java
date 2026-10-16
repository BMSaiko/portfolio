package pt.isep.dei.esinf.ui;

import java.util.ArrayList;
import java.util.List;

public class Menu implements Runnable {
    private final List<String> options;

    public Menu(List<String> options) {
        this.options = options;
    }

    @Override
    public void run() {
        int optionNumber = 1;

        for (String option : options) {
            System.out.printf("\n%d - %s", optionNumber++, option);
        }

        System.out.print("\n0 - Exit");
        System.out.print("\n\nOption: ");
    }
}
