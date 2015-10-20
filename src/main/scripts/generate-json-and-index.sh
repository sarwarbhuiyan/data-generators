#!/bin/sh
java -cp ".:lib/*" -Dconfigurable.file=configurable-es.properties com.sematext.ag.es.ComplexDataEsPlayerMain "$@"