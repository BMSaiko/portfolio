package org.example;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import com.example.production.UI.MainInterface;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        try {
            MainInterface interfaceTexto = new MainInterface();
            interfaceTexto.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}