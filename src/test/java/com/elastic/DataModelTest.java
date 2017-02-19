package com.elastic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import pl.solr.dm.DataModel;
import pl.solr.dm.DataType;
import pl.solr.dm.producers.JsonDataModelProducer;
import pl.solr.dm.producers.SolrDataModelProducer;
import pl.solr.dm.types.ArrayDataType;
import pl.solr.dm.types.DateDataType;
import pl.solr.dm.types.IPV4DataType;
import pl.solr.dm.types.IdentifierDataType;
import pl.solr.dm.types.ObjectDataType;

public class DataModelTest {

	@Test
	public void unserialize() {
		DataModel model = DataModel.builder().fromJson(DataModelTest.class.getResourceAsStream("/input.json"));
		assertNotNull(model.getValue().getIdentifier());
		
		DataType<?> id = model.getValue().getNewValue().get("id");
		assertTypeAndNotNull(id, IdentifierDataType.class);

		DataType<?> created = model.getValue().getNewValue().get("created"); 
		assertTypeAndNotNull(created, DateDataType.class);
		
		DataType<?> tags = model.getValue().getNewValue().get("tags");
		assertTypeAndNotNull(tags, ArrayDataType.class);
		
		DataType<?> ip = model.getValue().getNewValue().get("ip");
		assertTypeAndNotNull(ip, IPV4DataType.class);
		
		DataType<?> position = model.getValue().getNewValue().get("position");
		if (position != null) { //probability 50%
			assertTypeAndNotNull(position, ObjectDataType.class);
			try {
				((ObjectDataType) position).getIdentifier();
				fail("getIdentifier() should throw exception");
			} catch (RuntimeException re) {
				assertTrue(true); //It's ok
			}
		}

		for (int i = 0; i < 3; i++) {
			System.out.println(new JsonDataModelProducer().convert(model.getValue()));
		}
		for (int i = 0; i < 3; i++) {
			System.out.println(new SolrDataModelProducer().convert(model.getValue()));
		}
	}
	

	@Test
	public void unserializeToJsonWithNull() {
		DataModel model = DataModel.builder().fromJson(
				DataModelTest.class.getResourceAsStream("/null.json"));
		String result = new JsonDataModelProducer().convert(model.getValue());
		System.out.println(result);
		assertNotNull(result);
		assertFalse("null".equals(result));
		assertTrue("Field should not be available in json", !result.contains("field"));		

	}
	
	@Test(expected = RuntimeException.class)
	public void unserializeWithError() {
		ByteArrayInputStream bais = new ByteArrayInputStream("{ syntax_error: }".getBytes());
		
		DataModel.builder()
				.fromJson(bais);
	}

	private void assertTypeAndNotNull(DataType<?> obj, Class<?> type) {
		assertNotNull(obj);
		assertTrue("obj: " + obj + " should have type: " + type, obj.getClass().equals(type));
	}
}
