package com.innowise.migration;

import com.innowise.exception.ChangeLogException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeLogManagerTest {
    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private DatabaseMetaData mockMetaData;
    private ChangeLogManager changeLogManager;

    @BeforeEach
    void setUp() {
        changeLogManager = new ChangeLogManager();
        resetStaticFields();
    }

    @Test
    void testInitialize_WhenChangelogTableDoesNotExist() throws SQLException {

        when(mockConnection.getMetaData()).thenReturn(mockMetaData);

        when(mockMetaData.getTables(null, null, "changelog", null))
                .thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        when(mockConnection.createStatement()).thenReturn(mockStatement);

        changeLogManager.initialize(mockConnection);

        verify(mockStatement).execute(contains("CREATE TABLE IF NOT EXISTS changelog"));

        assertTrue(isStaticChangeLogTableExist(), "Should be marked as exist after creation");
    }

    @Test
    void testInitialize_WhenChangelogTableAlreadyExists() throws SQLException {

        when(mockConnection.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getTables(null, null, "changelog", null))
                .thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        changeLogManager.initialize(mockConnection);

        verify(mockConnection, never()).createStatement();
    }

    @Test
    void testIsScriptExecuted_True() throws SQLException {

        String scriptName = "create_table.sql";
        String version = "1.0";
        String checksum = "checksum";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        boolean executed = changeLogManager.isScriptExecuted(scriptName, version, checksum, mockConnection);

        assertTrue(executed);
        verify(mockPreparedStatement).setString(1, scriptName);
        verify(mockPreparedStatement).setString(2, version);
        verify(mockPreparedStatement).setString(3, checksum);
    }

    @Test
    void testIsScriptExecuted_False() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(0);

        boolean executed = changeLogManager.isScriptExecuted("foo.sql", "1.0", "1234", mockConnection);

        assertFalse(executed);
    }

    @Test
    void testMarkScriptExecuted() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        changeLogManager.markScriptExecuted("insert.sql", "1.0", "checksum", mockConnection);

        verify(mockPreparedStatement).setString(eq(1), anyString());
        verify(mockPreparedStatement).setString(eq(2), eq("insert.sql"));
        verify(mockPreparedStatement).setString(eq(3), eq("1.0"));
        verify(mockPreparedStatement).setString(eq(4), eq("checksum"));
        verify(mockPreparedStatement).setTimestamp(eq(5), any(Timestamp.class));
        verify(mockPreparedStatement).setString(eq(6), eq("SUCCESS"));
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testMarkScriptExecuted_ThrowsSQLException() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doThrow(new SQLException("DB error")).when(mockPreparedStatement).executeUpdate();

        assertThrows(ChangeLogException.class, () -> {
            changeLogManager.markScriptExecuted("script.sql", "1.0", "checksum", mockConnection);
        });
    }

    private void resetStaticFields() {
        try {
            var isChangeLogTableCheckedField = ChangeLogManager.class.getDeclaredField("isChangeLogTableChecked");
            isChangeLogTableCheckedField.setAccessible(true);
            isChangeLogTableCheckedField.setBoolean(changeLogManager, false);

            var isChangeLogTableExistField = ChangeLogManager.class.getDeclaredField("isChangeLogTableExist");
            isChangeLogTableExistField.setAccessible(true);
            isChangeLogTableExistField.setBoolean(changeLogManager, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset static fields", e);
        }
    }

    private boolean isStaticChangeLogTableExist() {
        try {
            var isChangeLogTableExistField = ChangeLogManager.class.getDeclaredField("isChangeLogTableExist");
            isChangeLogTableExistField.setAccessible(true);
            return isChangeLogTableExistField.getBoolean(changeLogManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}