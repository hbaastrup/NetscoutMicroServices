#!/bin/sh

java -jar MicroServices-tac.1.0.0.jar &
sleep 2
java -jar MicroServices-subscriber.1.0.0.jar &
sleep 2
java -jar MicroServices-cdr.1.0.0.jar &
echo "DONE!"

