package com.innowise.migration;

import com.innowise.exception.ConfigurationFileNotFoundException;
import com.innowise.exception.DatabaseConfigurationException;
import com.innowise.migration.model.DatabaseChangeLogScript;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChangeLogParserTest {

    private ChangeLogParser parser;

    @BeforeEach
    void setUp() {
        parser = new ChangeLogParser();
    }

    @Test
    void testParse_ValidChangelog() {

        String filePath = "valid-scripts.yml";

        List<DatabaseChangeLogScript> scripts = parser.parse(filePath);

        assertFalse(scripts.isEmpty(), "Should load scripts from valid file");
        assertEquals(2, scripts.size(), "Expected 2 scripts in valid-changelog.yml");

        DatabaseChangeLogScript first = scripts.get(0);
        assertEquals("changelog/sql/create_db_bike_rental.sql", first.getFile());
        assertEquals("1.1.1", first.getVersion());
    }

    @Test
    void testParse_FileNotFound() {

        String filePath = "no-such-file.yml";

        assertThrows(ConfigurationFileNotFoundException.class, () -> {
            parser.parse(filePath);
        }, "Expected exception if file is missing");
    }

    @Test
    void testParse_EmptyFile() {

        String filePath = "empty.yml";

        assertThrows(DatabaseConfigurationException.class, () -> {
            parser.parse(filePath);
        });
    }
}