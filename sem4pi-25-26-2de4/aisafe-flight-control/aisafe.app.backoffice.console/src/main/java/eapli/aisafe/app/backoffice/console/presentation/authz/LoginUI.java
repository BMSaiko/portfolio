package eapli.aisafe.app.backoffice.console.presentation.authz;

import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.app.common.console.AbstractUI;

import java.util.Scanner;

public class LoginUI extends AbstractUI {

    private final AuthService auth;

    public LoginUI(AuthService auth) {
        this.auth = auth;
    }

    @Override
    protected boolean doShow() {
        Scanner sc = scanner;
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String pwd = sc.nextLine();
        boolean ok = auth.login(email, pwd).isPresent();
        if (ok) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials or inactive user.");
        }
        return true;
    }
}