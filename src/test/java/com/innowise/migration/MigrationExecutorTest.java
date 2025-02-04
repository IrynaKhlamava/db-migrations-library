package com.innowise.migration;

import com.innowise.exception.MigrationException;
import com.innowise.exception.MigrationScriptException;
import com.innowise.migration.model.DatabaseChangeLogScript;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MigrationExecutorTest {

    @Mock
    private ChangeLogLockManager mockLockManager;

    @Mock
    private ScriptExecutor mockScriptExecutor;

    @Mock
    private Connection mockConnection;

    private MigrationExecutor migrationExecutor;

    @BeforeEach
    void setUp() {

        migrationExecutor = new MigrationExecutor(mockLockManager, mockScriptExecutor);
    }

    @Test
    void testExecuteMigration_Successful() throws SQLException {

        List<DatabaseChangeLogScript> scripts = List.of(
                new DatabaseChangeLogScript("path/script1.sql", "1.0"),
                new DatabaseChangeLogScript("path/script2.sql", "1.1")
        );

        when(mockLockManager.isLocked(mockConnection)).thenReturn(false);

        migrationExecutor.executeMigration(mockConnection, scripts);

        verify(mockLockManager).isLocked(mockConnection);
        verify(mockLockManager).setLock(mockConnection);

        verify(mockScriptExecutor, times(scripts.size()))
                .executeScript(eq(mockConnection), any(DatabaseChangeLogScript.class));

        verify(mockConnection, times(scripts.size())).commit();

        verify(mockLockManager).removeLock(mockConnection);

        verifyNoMoreInteractions(mockLockManager, mockScriptExecutor);
    }

    @Test
    void testExecuteMigration_AlreadyLocked() throws SQLException {

        List<DatabaseChangeLogScript> scripts = List.of(
                new DatabaseChangeLogScript("script.sql", "2.0")
        );

        when(mockLockManager.isLocked(mockConnection)).thenReturn(true);

        MigrationException ex = assertThrows(MigrationException.class, () ->
                migrationExecutor.executeMigration(mockConnection, scripts)
        );
        assertTrue(ex.getMessage().contains("Database is already locked"));

        verify(mockLockManager, never()).setLock(mockConnection);

        verify(mockScriptExecutor, never()).executeScript(any(), any());

        verify(mockConnection, never()).commit();

        verify(mockLockManager).removeLock(mockConnection);

        verifyNoMoreInteractions(mockLockManager, mockScriptExecutor, mockConnection);
    }

    @Test
    void testExecuteMigration_ScriptExecutorThrowsSQLException() throws SQLException {

        List<DatabaseChangeLogScript> scripts = List.of(
                new DatabaseChangeLogScript("fail.sql", "3.0")
        );
        when(mockLockManager.isLocked(mockConnection)).thenReturn(false);

        doThrow(new MigrationScriptException("Migration failed, rollback executed"))
                .when(mockScriptExecutor).executeScript(eq(mockConnection), any(DatabaseChangeLogScript.class));

        MigrationException e = assertThrows(MigrationException.class, () ->
                migrationExecutor.executeMigration(mockConnection, scripts)
        );
        assertTrue(e.getMessage().contains("Migration failed, rollback executed"));

        verify(mockConnection).rollback();

        verify(mockLockManager).removeLock(mockConnection);
    }

    @Test
    void testExecuteMigration_FailedToCommit() throws SQLException {

        List<DatabaseChangeLogScript> scripts = List.of(
                new DatabaseChangeLogScript("script.sql", "4.0")
        );
        when(mockLockManager.isLocked(mockConnection)).thenReturn(false);

        doThrow(new SQLException("Commit error"))
                .when(mockConnection).commit();

        assertThrows(MigrationException.class, () ->
                migrationExecutor.executeMigration(mockConnection, scripts)
        );

        verify(mockConnection).rollback();

        verify(mockLockManager).removeLock(mockConnection);
    }

}