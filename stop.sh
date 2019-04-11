#!/bin/sh

pids=`ps -ef | grep MicroServices- | awk '{print $2}'`
kill -9 $pids

echo "DONE!"
