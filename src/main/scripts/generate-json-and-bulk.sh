#!/bin/sh
java -cp ".:lib/*" -Dconfigurable.file=configurable-es.properties com.elastic.agplayer.apps.BulkComplexDataFilePlayerMain "$@"