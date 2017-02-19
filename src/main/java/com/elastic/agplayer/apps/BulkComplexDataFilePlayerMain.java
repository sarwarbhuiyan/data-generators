package com.elastic.agplayer.apps;

import com.elastic.agplayer.sinks.BulkComplexJSONDataSink;
import com.sematext.ag.PlayerConfig;
import com.sematext.ag.PlayerRunner;
import com.sematext.ag.es.sink.BulkJSONDataESSink;
import com.sematext.ag.es.sink.ThreadedBulkJSONDataESSink;
import com.sematext.ag.source.FiniteEventSource;
import com.sematext.ag.source.SimpleSourceFactory;
import com.sematext.ag.source.dictionary.ComplexEventSource;

/**
 * Elastic search data generator using complex definition of record from JSON file.
 *
 */
public final class BulkComplexDataFilePlayerMain {
  private BulkComplexDataFilePlayerMain() {
  }

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out
          .println("Usage: esBaseUrl esIndexName esTypeName eventsCount batchSize schemaFile useThreading");
      System.out.println("Example: http://localhost:9200 logstash log 1000000 1000 schema.json true");
      System.exit(1);
    }
    
    String esBaseUrl = args[0];
    String esIndexName = args[1];
    String esTypeName = args[2];
    String eventsCount = args[3];
    String batchSize = args[4];
    String schemaFile = args[5];
    String useThreading = args[6];
    
    String sinkClassName = BulkJSONDataESSink.class.getName();
    if (Boolean.parseBoolean(useThreading)) {
      sinkClassName = ThreadedBulkJSONDataESSink.class.getName();
    }
    

//    PlayerConfig config = new PlayerConfig(SimpleSourceFactory.SOURCE_CLASS_CONFIG_KEY,
//        ComplexEventSource.class.getName(), FiniteEventSource.MAX_EVENTS_KEY, eventsCount,
//        ComplexEventSource.SCHEMA_FILE_NAME_KEY, schemaFile, 
//        PlayerRunner.SINK_CLASS_CONFIG_KEY, ComplexJSONDataFileSink.class.getName());
    
    PlayerConfig config = new PlayerConfig(SimpleSourceFactory.SOURCE_CLASS_CONFIG_KEY, ComplexEventSource.class.getName(), 
    		 FiniteEventSource.MAX_EVENTS_KEY, eventsCount,
    		 ComplexEventSource.SCHEMA_FILE_NAME_KEY, schemaFile, 
    		 PlayerRunner.SINK_CLASS_CONFIG_KEY, BulkComplexJSONDataSink.class.getName(), 
    		 BulkComplexJSONDataSink.ES_BASE_URL_KEY,
    		 esBaseUrl, BulkComplexJSONDataSink.ES_INDEX_NAME_KEY, esIndexName, BulkComplexJSONDataSink.ES_TYPE_NAME_KEY,
    		 esTypeName, BulkJSONDataESSink.ES_BATCH_SIZE_KEY, batchSize);
    
    PlayerRunner.play(config);
  }
}
