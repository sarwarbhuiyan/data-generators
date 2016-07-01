package com.elastic.agplayer.sinks;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.sematext.ag.PlayerConfig;
import com.sematext.ag.event.ComplexEvent;
import com.sematext.ag.exception.InitializationFailedException;
import com.sematext.ag.sink.Sink;

import pl.solr.dm.producers.JsonDataModelProducer;

public class ComplexJSONDataFileSink extends Sink<ComplexEvent> {

	public static final String ES_BASE_URL_KEY = "complexJSONDataEsSink.esBaseUrl";
	public static final String ES_INDEX_NAME_KEY = "complexJSONDataEsSink.indexName";
	public static final String ES_TYPE_NAME_KEY = "complexJSONDataEsSink.typeName";
	
	private static final Logger LOG = Logger
			.getLogger(ComplexJSONDataFileSink.class);
	private String esBaseUrl;
	private String indexName;
	private String typeName;
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	
	@Override
	public void init(PlayerConfig config) throws InitializationFailedException {
		super.init(config);
		try {
			this.indexName = config.get(ES_INDEX_NAME_KEY);
			this.typeName = config.get(ES_TYPE_NAME_KEY);
			fileWriter = new FileWriter("output.txt", true);
			bufferedWriter = new BufferedWriter(fileWriter);
		} catch (IOException e) {
			throw new InitializationFailedException("Could not create output file");
		}
	}

	@Override
	public boolean write(ComplexEvent event) {
		String jsonLine = new JsonDataModelProducer().convert(event.getObject());
		try {
			System.out.println("WRITING JSON LINE:\n"+jsonLine);
			bufferedWriter.write(jsonLine+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void close() {
		
		try {
			if(bufferedWriter!=null)
				bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		super.close();
	}

}
