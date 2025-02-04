package com.innowise.migration;

import com.innowise.migration.model.DatabaseChangeLogScript;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MigrationManagerTest {

    @Mock
    private ChangeLogManager mockChangeLogManager;

    @Mock
    private ChangeLogLockManager mockLockManager;

    @Mock
    private MigrationExecutor mockExecutor;

    @Mock
    private ChangeLogParser mockParser;

    @Mock
    private Connection mockConnection;

    private MigrationManager migrationManager;

    @BeforeEach
    void setUp() {
        migrationManager = new MigrationManager(
                mockChangeLogManager,
                mockLockManager,
                mockExecutor,
                mockParser
        );
    }

    @Test
    void testRunMigrations_Successful() {

        List<DatabaseChangeLogScript> scripts = List.of(
                new DatabaseChangeLogScript("script1.sql", "1.0"),
                new DatabaseChangeLogScript("script2.sql", "1.1")
        );
        when(mockParser.parse(anyString())).thenReturn(scripts);

        migrationManager.runMigrations(mockConnection);

        verify(mockLockManager).initialize(mockConnection);
        verify(mockChangeLogManager).initialize(mockConnection);
        verify(mockParser).parse(anyString());
        verify(mockExecutor).executeMigration(mockConnection, scripts);

        verifyNoMoreInteractions(mockLockManager, mockChangeLogManager, mockExecutor, mockParser);
    }


}