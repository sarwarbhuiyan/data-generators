package com.elastic.agplayer.sinks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;

import com.sematext.ag.PlayerConfig;
import com.sematext.ag.es.sink.BulkJSONDataESSink;
import com.sematext.ag.es.sink.SimpleJSONDataESSink;
import com.sematext.ag.event.ComplexEvent;
import com.sematext.ag.exception.InitializationFailedException;
import com.sematext.ag.sink.AbstractHttpSink;
import com.yammer.metrics.core.TimerContext;

import pl.solr.dm.producers.JsonDataModelProducer;

public class BulkComplexJSONDataSink extends ComplexJSONDataESSink {

	public static final String ES_BASE_URL_KEY = "complexJSONDataEsSink.esBaseUrl";
	public static final String ES_INDEX_NAME_KEY = "complexJSONDataEsSink.indexName";
	public static final String ES_TYPE_NAME_KEY = "complexJSONDataEsSink.typeName";
	private static final Logger LOG = Logger.getLogger(SimpleJSONDataESSink.class);

	protected String esBaseUrl;

	protected String indexName;

	protected String typeName;

	protected StringBuilder stringBuilder;
	protected int bulkSize = 1000;
	protected List<ComplexEvent> events = new ArrayList<ComplexEvent>();
	protected String outputDir = "output";

	public void init(PlayerConfig config) throws InitializationFailedException {
		super.init(config);
		
		indexName = config.get(ES_INDEX_NAME_KEY);
		typeName = config.get(ES_TYPE_NAME_KEY);
		bulkSize = Integer.parseInt(config.get(BulkJSONDataESSink.ES_BATCH_SIZE_KEY));
		File outputDirFile = new File(this.outputDir);
		
		outputDirFile.mkdir();

		stringBuilder = new StringBuilder();
		
		if ((indexName == null) || ("".equals(indexName.trim()))) {
			throw new IllegalArgumentException(
					getClass().getName() + " expects configuration property " + "complexJSONDataEsSink.indexName");
		}

		if ((typeName == null) || ("".equals(typeName.trim()))) {
			throw new IllegalArgumentException(
					getClass().getName() + " expects configuration property " + "complexJSONDataEsSink.typeName");
		}
		
	}

	public boolean write(ComplexEvent event) {

		events.add(event);
		if (events.size() >= bulkSize) {
			List<ComplexEvent> eventsCopy = new ArrayList<ComplexEvent>(this.events);
			events.clear();
			LOG.info("Sending ES bulk index event with " + eventsCopy.size() + " events");
			File newFile = new File(outputDir + "/file-" + System.currentTimeMillis() + ".json");

			try {
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newFile));
				bufferedWriter.append(getBulkData(eventsCopy));
				return true;
			} catch (Exception e) {
				LOG.error("Error writing event: ", e);
				return false;

			} finally {
			}
		}
		return true;
	}

	/**
	 * Returns ES bulk data.
	 * 
	 * @return ES bulk data
	 */
	protected String getBulkData(List<ComplexEvent> events) {

		StringBuilder builder = new StringBuilder();
		for (ComplexEvent event : events) {
			if(event != null) {
				builder.append(getElasticSearchBulkHeader(event, this.indexName, this.typeName));
				builder.append("\n");
				String jsonLine = new JsonDataModelProducer().convert(event.getObject());
				builder.append(jsonLine);
				builder.append("\n");
			}

		}
		return builder.toString();
	}

	public static String getElasticSearchBulkHeader(ComplexEvent event, String index, String type) {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"index\":{\"_index\":\"");
		builder.append(index);
		builder.append("\",\"_type\":\"");
		builder.append(type);
		builder.append("\"}}");
		return builder.toString();
	}
}
