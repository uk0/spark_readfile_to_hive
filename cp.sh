#!/usr/bin/env bash

#!/usr/bin/env bash
# owner 张建新


PASS=root2758

scp_jar=$(expect -c "
set timeout 10
spawn scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null  target/ORC2PREQUET-1.0-SNAPSHOT.jar root@172.17.0.195:/home/hdfs
expect \"*password:\"
send \"${PASS}\r\"
send \"exit \r\"
expect eof
")

echo "$scp_jar"



scp_sh=$(expect -c "
set timeout 10
spawn scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null  test_job.sh root@172.17.0.195:/home/hdfs
expect \"*password:\"
send \"${PASS}\r\"
send \"exit \r\"
expect eof
")

echo "$scp_sh"