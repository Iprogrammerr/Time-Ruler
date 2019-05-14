package com.iprogrammerr.time.ruler.respondent;

//TODO refactor to make this class redundant
public class HtmlResponseRedirection {

    public final HtmlResponse response;
    public final String redirection;

    public HtmlResponseRedirection(HtmlResponse response, String redirection) {
        this.response = response;
        this.redirection = redirection;
    }

    public HtmlResponseRedirection(HtmlResponse response) {
        this(response, "");
    }

    public HtmlResponseRedirection(String body) {
        this(new HtmlResponse(body));
    }
}
