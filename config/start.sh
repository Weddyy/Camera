#!/bin/sh

sudo java -server -Dfile.encoding=UTF-8 -Xms256m -Xmx512m -cp config:./* com.lolStone.Main >&stdout.log &