package com.iprogrammerr.time.ruler.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class HtmlViews implements Views {

    private static final String EXTENSION = ".html";
    private final File root;

    public HtmlViews(File root) {
        this.root = root;
    }

    @Override
    public String view(String name) {
        try {
            try (BufferedReader reader = new BufferedReader(
            new FileReader(htmlPath(name)))) {
                StringBuilder builder = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
                return builder.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String htmlPath(String name) {
        return root.getPath() + File.separator + name + EXTENSION;
    }
}
