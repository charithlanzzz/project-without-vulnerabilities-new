package com.scalesec.vulnado;

import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
@EnableAutoConfiguration
public class CowController {
    @RequestMapping(value = "/cowsay", method = RequestMethod.GET)
    String cowsay(@RequestParam(defaultValue = "I love Linux!") String input) throws IOException {
        // Sanitize the input to prevent command injection
        input = input.replaceAll("[^a-zA-Z0-9]", ""); // Remove any characters that are not alphanumeric

        // Build and execute the cowsay command using ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder("cowsay", input);

        // Redirect error stream to output stream
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // Read the command's output
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // Wait for the process to finish
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return output.toString();
    }
}
