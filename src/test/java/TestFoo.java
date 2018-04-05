import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FirstJUnit5Tests {
    @Test
    void myFirstTest() {
        assertEquals(2, 1 + 1);
    }
}