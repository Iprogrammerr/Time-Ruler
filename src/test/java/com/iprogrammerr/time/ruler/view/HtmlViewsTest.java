package com.iprogrammerr.time.ruler.view;

import com.iprogrammerr.time.ruler.ThrowsMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class HtmlViewsTest {

    private final File root = new File(HtmlViewsTest.class.getResource("/").getPath());

    @Test
    public void returnsProperView() {
        String view = "index";
        String path = root + File.separator + view + ".html";
        HtmlViews views = new HtmlViews(root);
        MatcherAssert.assertThat("Views are not equal", views.view(view), Matchers.equalTo(read(path)));
    }


    @Test
    public void throwsExceptionIfViewDoesNotExist() {
        HtmlViews views = new HtmlViews(root);
        MatcherAssert.assertThat("View should not exist", () -> views.view("user"), new ThrowsMatcher());
    }

    private String read(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
