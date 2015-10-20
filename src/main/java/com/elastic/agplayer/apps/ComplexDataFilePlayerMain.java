package com.elastic.agplayer.apps;

import com.elastic.agplayer.sinks.ComplexJSONDataFileSink;
import com.sematext.ag.PlayerConfig;
import com.sematext.ag.PlayerRunner;
import com.sematext.ag.source.FiniteEventSource;
import com.sematext.ag.source.SimpleSourceFactory;
import com.sematext.ag.source.dictionary.ComplexEventSource;

/**
 * Elastic search data generator using complex definition of record from JSON file.
 *
 * @author negativ
 *
 */
public final class ComplexDataFilePlayerMain {
  private ComplexDataFilePlayerMain() {
  }

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out
          .println("Usage: esBaseUrl esIndexName esTypeName eventsCount schemaFile");
      System.out.println("Example: 100 schema.json");
      System.exit(1);
    }

    String eventsCount = args[0];
    String schemaFile = args[1];

    PlayerConfig config = new PlayerConfig(SimpleSourceFactory.SOURCE_CLASS_CONFIG_KEY,
        ComplexEventSource.class.getName(), FiniteEventSource.MAX_EVENTS_KEY, eventsCount,
        ComplexEventSource.SCHEMA_FILE_NAME_KEY, schemaFile, 
        PlayerRunner.SINK_CLASS_CONFIG_KEY, ComplexJSONDataFileSink.class.getName());
    PlayerRunner.play(config);
  }
}
