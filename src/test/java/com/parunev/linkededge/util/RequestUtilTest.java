package com.parunev.linkededge.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestUtilTest {

    @Test
    void getCurrentRequest_ReturnsRequestURI() {
        MockHttpServletRequest mockRequest = MockMvcRequestBuilders.get("/test-uri")
                .buildRequest(new MockServletContext());

        ServletRequestAttributes mockAttributes = mock(ServletRequestAttributes.class);
        when(mockAttributes.getRequest()).thenReturn(mockRequest);

        RequestContextHolder.setRequestAttributes(mockAttributes);

        String currentRequest = RequestUtil.getCurrentRequest();

        assertEquals("/test-uri", currentRequest);
    }
}
