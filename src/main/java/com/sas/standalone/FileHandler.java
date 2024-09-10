package com.sas.standalone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHandler {

    public String readFileAsString(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public void writeStringToFile(String content, String filePath) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes());
    }

    public void backupFile(String filePath) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String backupFilePath = filePath + "." + sdf.format(new Date()) + ".bak";
        Files.copy(Paths.get(filePath), Paths.get(backupFilePath), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Backup created at: " + backupFilePath);
    }
}
