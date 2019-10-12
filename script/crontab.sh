#!/bin/bash
crontab -l > /opt/tmp.txt && cat /data/hotwords/hotwords-1.0-SNAPSHOT/bin/crontab.txt >> /opt/tmp.txt && crontab /opt/tmp.txt
rm -f /opt/tmp.txt
