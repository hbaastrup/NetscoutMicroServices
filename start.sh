#!/bin/sh

java -jar MicroServices-tac.1.0.0.jar &
sleep 2
java -jar MicroServices-subscriber.1.0.0.jar &
# slowdown response sometimes
#java -jar MicroServices-subscriber.1.0.0.jar -s &
sleep 2
java -jar MicroServices-cdr.1.0.0.jar &
sleep 2
java -jar MicroServices-producers.1.0.0.jar &
sleep 2
java -jar MicroServices-monitor.1.0.0.jar &
echo "DONE!"

