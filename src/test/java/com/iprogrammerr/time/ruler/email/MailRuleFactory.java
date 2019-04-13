package com.iprogrammerr.time.ruler.email;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetup;

public class MailRuleFactory {

    public GreenMailRule smtp(int port) {
        return new GreenMailRule(new ServerSetup(port, "127.0.0.1", "smtp"));
    }
}
