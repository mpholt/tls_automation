package com.sas.standalone; 

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import com.sas.standalone.FileHandler;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FileHandlerTest {

    private FileHandler fileHandler = new FileHandler();

    @Test
    void testReadFileAsString() throws Exception {
        // Mocking Files.readAllBytes to return some dummy content
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            String expectedContent = "sample content";
            mockedFiles.when(() -> Files.readAllBytes(any(Path.class))).thenReturn(expectedContent.getBytes());

            String result = fileHandler.readFileAsString("dummyPath");

            assertEquals(expectedContent, result); // Assert that the content returned is as expected
            mockedFiles.verify(() -> Files.readAllBytes(any(Path.class)), times(1)); // Verify readAllBytes was called once
        }
    }

    @Test
    void testWriteStringToFile() throws Exception {
        // Mocking Files.write to ensure it's called with the correct arguments
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            String contentToWrite = "content to write";

            fileHandler.writeStringToFile(contentToWrite, "dummyPath");

            mockedFiles.verify(() -> Files.write(any(Path.class), eq(contentToWrite.getBytes())), times(1)); // Verify the write method was called once
        }
    }
}
