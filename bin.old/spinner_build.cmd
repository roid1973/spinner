#mysql --host=173.194.110.54 --user=spinner --password=dayan151 spinnerDB2 < db2_spinner.sql
call mvn clean install
#call mvn clean install -DskipTests
#call appcfg.cmd -A spinner-1026 update target\spinner-1.0