package com;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LibraryApp {
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        LibraryManager manager = new LibraryManager();

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            new MainMenu(manager, scanner).run();
        }

        // ✅ เพิ่มตรงนี้
        manager.saveData();
    }
}