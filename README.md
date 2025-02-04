<h1 style="font-family: 'Arial';">DB Migration Library</h1> 
<p><b>1.	Reading Configuration and Scripts</b>
<p>DB Migration Library loads the main application settings from ‘application.yml’ file. It reads the YAML file once and store the values in ‘config’. Then the library reads a section ‘scripts’ from ‘config’. This section contains the path and file name of the scripts(changelog/scripts.yml) that must be executed to migrate the database. ‘Scripts.yml’ lists the SQL scripts and their versions. A parser (ChangeLogParser) looks for a section named databaseChangeLog. Each entry becomes an object (DatabaseChangeLogScript) with a file path and a version.
<p><b>2.	Setting Up the Connection and Lock</b>
<p>DB Migration Library opens a database connection using ‘DatabaseConnectionManager’. Then a lock manager (ChangeLogLockManager) checks if the database is already locked by another migration. If the database is free, it sets a lock in the ‘CHANGELOGLOCK’ table, preventing other processes from running migrations at the same time.
<p><b>3.	Initializing the CHANGELOG</b>
<p>A 'ChangeLogManager' checks if the 'CHANGELOG' table exists. If not, the library creates it. This table stores which scripts (version and checksum) have been applied.
<p><b>4.	Running Each Script</b>
<p>The library processes the scripts listed in the ‘scripts.yaml’ file one by one. For each script:
o	It calculates the file’s checksum (using SHA-256).
o	It checks the ‘CHANGELOG’ table to see if that script was already applied (matching script name, version, and checksum).
o	If it’s already done, the library skips it.
o	If not, it reads the .sql file line by line, ignoring comments and splitting commands on the ;. Then it executes each command. 
o	When finished, it writes a record into ‘CHANGELOG’, marking the script as executed.
<p><b>5.	Commit or Roll Back</b>
<p>DB Migration Library uses a transaction for each script. If the script finishes without errors, it calls commit(). If there is an error, it calls rollback(), undoing changes for that.
<p><b>6.	Releasing the Lock</b>
<p>After all scripts finish, or if an error occurs, the library unlocks the database by calling removeLock(). This allows other processes to run migrations later
