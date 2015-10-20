#!/bin/sh
java -cp ".:lib/*" -Dconfigurable.file=configurable-file.properties com.elastic.agplayer.apps.ComplexDataFilePlayerMain "$@"