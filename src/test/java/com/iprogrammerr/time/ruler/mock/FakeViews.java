package com.iprogrammerr.time.ruler.mock;

import com.iprogrammerr.time.ruler.model.Initialization;
import com.iprogrammerr.time.ruler.view.Views;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FakeViews implements Views {

    private final Initialization<String> view;

    public FakeViews(File file) {
        this.view = new Initialization<>(() -> {
            try {
                return new String(Files.readAllBytes(Paths.get(file.getPath())));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public FakeViews(String resource) {
        this(new File(FakeViews.class.getResource("/" + resource).getPath()));
    }

    @Override
    public String view(String name) {
        return view.value();
    }
}
