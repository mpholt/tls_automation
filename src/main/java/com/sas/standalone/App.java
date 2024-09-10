package com.sas.standalone;

import java.nio.file.Files;
import java.nio.file.Paths;

public class App {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: <server.xml path> <keystore path> <keystore password>");
            System.exit(1);
        }

        String serverXmlPath = args[0]; // /Users/mpholt/development/interviews/sas/apache-tomcat-11.0.0-M24/conf/server.xml
        String keystorePath = args[1]; // /Users/mpholt/development/interviews/sas/my_keystore
        String keystorePassword = args[2]; // password

        if (!isValidPath(serverXmlPath) || !isValidPath(keystorePath)) {
            System.out.println("Error: One or more file paths are invalid or do not exist.");
            System.exit(1);
        }

        if (!isValidPassword(keystorePassword)) {
            System.out.println("Error: The keystore password does not meet the security criteria.");
            System.exit(1);
        }
        
        ConfigurationManager configManager = new ConfigurationManager();
        FileHandler fileHandler = new FileHandler();
        
        AppService appService = new AppService(configManager, fileHandler, keystorePath, keystorePassword);
        appService.modifyServerXml(serverXmlPath);

        System.out.println("Server XML processing completed.");
    }

    private static boolean isValidPath(String path) {
        return Files.exists(Paths.get(path)) && Files.isReadable(Paths.get(path));
    }

    private static boolean isValidPassword(String password) {
        // Example validation: Password length
        return password.length() >= 8;  // Ensures the password is at least 8 characters long
    }
}