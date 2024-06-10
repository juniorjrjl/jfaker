package net.jfaker;

import net.jfaker.util.CustomFaker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SampleTest {

    private final CustomFaker faker = new CustomFaker();

    @Test
    void sample(){
        assertTrue(true);
    }

}
