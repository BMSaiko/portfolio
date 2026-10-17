package eapli.aisafe.app.backoffice.console.presentation.authz;

import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.app.common.console.AbstractUI;

public class LogoutUI extends AbstractUI {

    private final AuthService auth;

    public LogoutUI(AuthService auth) {
        this.auth = auth;
    }

    @Override
    protected boolean doShow() {
        auth.logout();
        System.out.println("Logged out.");
        return true;
    }
}