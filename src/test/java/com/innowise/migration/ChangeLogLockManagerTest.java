package com.innowise.migration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeLogLockManagerTest {
    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private PreparedStatement mockPrepared;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private DatabaseMetaData mockMetaData;
    private ChangeLogLockManager lockManager;

    @BeforeEach
    void setUp() {
        lockManager = new ChangeLogLockManager();
        resetStaticFields();
    }

    @Test
    void testInitialize_WhenChangelogLockTableDoesNotExist() throws SQLException {

        when(mockConnection.getMetaData()).thenReturn(mockMetaData);

        when(mockMetaData.getTables(null, null, "changeloglock", null)).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        when(mockConnection.createStatement()).thenReturn(mockStatement);

        when(mockStatement.executeUpdate(contains("INSERT INTO changeloglock"))).thenReturn(1);

        lockManager.initialize(mockConnection);

        verify(mockStatement).execute(contains("CREATE TABLE IF NOT EXISTS changeloglock"));

        verify(mockStatement).executeUpdate(contains("INSERT INTO changeloglock"));

        assertTrue(lockManager.isLockTableExists(mockConnection));
    }

    @Test
    void testInitialize_WhenChangelogLockTableAlreadyExists() throws SQLException {

        when(mockConnection.getMetaData()).thenReturn(mockMetaData);

        when(mockMetaData.getTables(null, null, "changeloglock", null)).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        lockManager.initialize(mockConnection);

        verify(mockConnection, never()).createStatement();
    }

    @Test
    void testSetLock_Success() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPrepared);

        when(mockPrepared.executeUpdate()).thenReturn(1);

        boolean result = lockManager.setLock(mockConnection);

        assertTrue(result, "Should return true if lock is acquired");
        verify(mockPrepared).setTimestamp(eq(1), any(Timestamp.class));
        verify(mockPrepared).setString(eq(2), anyString());
    }

    @Test
    void testSetLock_AlreadyLocked() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPrepared);

        when(mockPrepared.executeUpdate()).thenReturn(0);

        boolean result = lockManager.setLock(mockConnection);

        assertFalse(result, "Should return false if row is not updated (already locked)");
    }

    @Test
    void testRemoveLock_Success() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPrepared);

        lockManager.removeLock(mockConnection);

        verify(mockPrepared).executeUpdate();

        verify(mockConnection).commit();
    }

    @Test
    void testIsLocked_True() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPrepared);
        when(mockPrepared.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getBoolean("locked")).thenReturn(true);

        boolean locked = lockManager.isLocked(mockConnection);

        assertTrue(locked, "Should be true if locked column is true");
    }

    @Test
    void testIsLocked_False() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPrepared);
        when(mockPrepared.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(false);

        boolean locked = lockManager.isLocked(mockConnection);

        assertFalse(locked, "Should be false if no rows found");
    }


    private void resetStaticFields() {
        try {

            java.lang.reflect.Field isLockTableCheckedField = ChangeLogLockManager.class
                    .getDeclaredField("isLockTableChecked");
            isLockTableCheckedField.setAccessible(true);
            isLockTableCheckedField.setBoolean(null, false);

            java.lang.reflect.Field isLockTableExistsField = ChangeLogLockManager.class
                    .getDeclaredField("isLockTableExists");
            isLockTableExistsField.setAccessible(true);
            isLockTableExistsField.setBoolean(null, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset static fields", e);
        }
    }

}