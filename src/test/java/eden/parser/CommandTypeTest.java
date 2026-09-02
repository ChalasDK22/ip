package eden.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests recognition of user command words.
 */
public class CommandTypeTest {
    @Test
    public void from_findCommandWithMixedCaseAndWhitespace_returnsFind() {
        assertAll(
                () -> assertEquals(CommandType.FIND, CommandType.from("find book")),
                () -> assertEquals(CommandType.FIND, CommandType.from("  FiNd   BOOK  ")),
                () -> assertEquals(CommandType.FIND, CommandType.from("find")));
    }
}
