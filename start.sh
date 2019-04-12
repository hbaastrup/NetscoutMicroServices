#!/bin/sh

java -jar MicroServices.1.0.0.jar &
#java -cp MicroServices.1.0.0.jar playground.micro.web.tac.WebTAC &
#sleep 2
#java -cp MicroServices.1.0.0.jar playground.micro.web.subscriber.WebSubscriber &
## slowdown response sometimes
##java -cp MicroServices.1.0.0.jar playground.micro.web.subscriber.WebSubscriber -s &
#sleep 2
#java -cp MicroServices.1.0.0.jar playground.micro.web.cdr.database.WebCdr &
#sleep 2
#java -cp MicroServices.1.0.0.jar playground.micro.producers.CdrProducer &
#sleep 2
#java -cp MicroServices.1.0.0.jar playground.micro.monitor.Monitor &
echo "DONE!"

