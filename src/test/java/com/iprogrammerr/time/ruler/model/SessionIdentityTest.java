package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.matcher.ThrowsMatcher;
import com.iprogrammerr.time.ruler.model.session.SessionIdentity;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionIdentityTest {

    @Test
    public void throwsExceptionIfThereIsNoSession() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getSession()).thenReturn(null);
        String message = "There is no validView session";
        MatcherAssert.assertThat(
            "Should throw exception with message",
            () -> new SessionIdentity().value(request),
            new ThrowsMatcher(message)
        );
    }

    @Test
    public void throwsExceptionIfThereIsNoIdInSession() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);

        String attr = "";
        Mockito.when(session.getAttribute(ArgumentMatchers.anyString())).thenReturn(attr);
        Mockito.when(request.getSession(false)).thenReturn(session);

        String message = String.format("%s is not proper session id", attr);
        MatcherAssert.assertThat(
            "Should throw exception with message",
            () -> new SessionIdentity().value(request),
            new ThrowsMatcher(message)
        );
    }
}
