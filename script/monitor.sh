#!/bin/bash

source ~/.bashrc

service_id=`ps -ef | grep hotwords-1.0-SNAPSHOT.jar | grep -v grep | awk '{print $2}'`
base_path=$(cd `dirname $0`; pwd)
cd $base_path
cd ..
pwd

if [ "$service_id" == "" ];then
  echo `date '+%Y%m%d %H:%M:%S'`" start again HotWord Import Service"
  nohup java -jar lib/hotwords-1.0-SNAPSHOT.jar &
else
  #echo $service_id
  echo `date '+%Y%m%d %H:%M:%S'`" HotWord Import Service is perfect"
fi
