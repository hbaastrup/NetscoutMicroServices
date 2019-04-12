#!/bin/sh

pids=`ps -ef | grep MicroServices.1.0.0.jar | awk '{print $2}'`
#pids=`ps -ef | grep MicroServices- | awk '{print $2}'`
kill -2 $pids
#kill -9 $pids

echo "DONE!"
