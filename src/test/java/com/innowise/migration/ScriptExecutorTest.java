package com.innowise.migration;

import com.innowise.exception.ChecksumCalculationException;
import com.innowise.migration.model.DatabaseChangeLogScript;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScriptExecutorTest {
    @Mock
    private ChangeLogManager mockChangeLogManager;

    @Mock
    private Connection mockConnection;

    @Mock
    private Statement mockStatement;

    private ScriptExecutor scriptExecutor;

    @BeforeEach
    void setUp() {
        scriptExecutor = new ScriptExecutor(mockChangeLogManager);
    }

    @Test
    void testExecuteScript_AlreadyExecuted()  {

        DatabaseChangeLogScript script = new DatabaseChangeLogScript("test-script.sql", "1.0");

        when(mockChangeLogManager.isScriptExecuted(eq("test-script.sql"), eq("1.0"), anyString(), eq(mockConnection)))
                .thenReturn(true);

        scriptExecutor.executeScript(mockConnection, script);

        verify(mockChangeLogManager, never()).markScriptExecuted(anyString(), anyString(), anyString(), any());
    }

    @Test
    void testExecuteScript_NotExecuted_ShouldMarkExecuted() throws SQLException {

        DatabaseChangeLogScript script = new DatabaseChangeLogScript("test-script.sql", "1.0");

        when(mockChangeLogManager.isScriptExecuted(eq("test-script.sql"), eq("1.0"), anyString(), eq(mockConnection)))
                .thenReturn(false);

        when(mockConnection.createStatement()).thenReturn(mockStatement);

        when(mockStatement.execute(anyString())).thenReturn(true);

        scriptExecutor.executeScript(mockConnection, script);

        verify(mockChangeLogManager).markScriptExecuted(eq("test-script.sql"), eq("1.0"), anyString(), eq(mockConnection));

        verify(mockStatement, atLeastOnce()).execute(anyString());
    }

    @Test
    void testExecuteScript_FileNotFound() {

        DatabaseChangeLogScript script = new DatabaseChangeLogScript("no/such/file.sql", "1.0");

        assertThrows(ChecksumCalculationException.class, () ->
                scriptExecutor.executeScript(mockConnection, script)
        );
    }

}

