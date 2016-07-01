package com.elastic.agplayer.sinks;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;

import com.sematext.ag.PlayerConfig;
import com.sematext.ag.es.sink.SimpleJSONDataESSink;
import com.sematext.ag.event.ComplexEvent;
import com.sematext.ag.exception.InitializationFailedException;
import com.sematext.ag.sink.AbstractHttpSink;
import com.yammer.metrics.core.TimerContext;

import pl.solr.dm.producers.JsonDataModelProducer;

public class ComplexJSONDataESSink extends AbstractHttpSink<ComplexEvent> {

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

	public void init(PlayerConfig config) throws InitializationFailedException {
		super.init(config);
		esBaseUrl = config.get(ES_BASE_URL_KEY);
		indexName = config.get(ES_INDEX_NAME_KEY);
		typeName = config.get(ES_TYPE_NAME_KEY);
		stringBuilder = new StringBuilder();
		if ((esBaseUrl == null) || ("".equals(esBaseUrl.trim()))) {
			throw new IllegalArgumentException(
					getClass().getName() + " expects configuration property " + "complexJSONDataEsSink.esBaseUrl");
		}

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
			HttpPost postMethod = new HttpPost(esBaseUrl + "/_bulk");
			StringEntity postEntity;
			TimerContext timerContext = null;
			try {
				postEntity = new StringEntity(getBulkData(eventsCopy), "UTF-8");
				postMethod.setEntity(postEntity);
				postMethod.expectContinue();
				timerContext = startRequestTimer();
				boolean returnValue = execute(postMethod);
				return returnValue;
			} 
			
			catch (UnsupportedEncodingException uee) {
				LOG.error("Error sending event: " + uee);
				return false;
			} 
			catch(Exception e)
			{
				LOG.error("Error sending event: ", e);
				return false;
			
			}
			finally {
				if (timerContext != null) {
					stopRequestTimer(timerContext);
				}
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
			builder.append(getElasticSearchBulkHeader(event, this.indexName, this.typeName));
			builder.append("\n");
			String jsonLine = new JsonDataModelProducer().convert(event.getObject());
			builder.append(jsonLine);
			builder.append("\n");
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
