package com.example.urlshortner.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {
    @Test
    void shouldEncodeNumbersCorrectly(){
        assertEquals("0", Base62Encoder.encode(0));
        assertEquals("a", Base62Encoder.encode(10));
        assertEquals("Z", Base62Encoder.encode(61));
        assertEquals("10", Base62Encoder.encode(62));
        assertEquals("21", Base62Encoder.encode(125));
        assertEquals("g8", Base62Encoder.encode(1000));
    }
}
