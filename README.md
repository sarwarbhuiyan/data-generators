# Introduction
This project includes a number of scripts to generate JSON data or index to Elasticsearch using Sematext's ActionGenerator library

# Build

There are some dependencies on the data-models project. Since the original repo is no longer maintained, some changes have been made
in a fork [here](https://github.com/sarwarbhuiyan/data-models). Download that first and build by doing 

```
> mvn clean install
```

Then download or clone this repository and run

```
> mvn clean install
> cd target
> unzip data-generators-1.0-SNAPSHOT-distribution.zip
> cd data-generators-1.0-SNAPSHOT
```

# Run

To generate a JSON file with 100 events using the schema-sample schema file,

```
> ./generate-json-data-file.sh 100 schema-sample.json
```

TO generate events and index directly into Elasticsearch

```
> ./generate-json-and-index.sh http://localhost:9200 myindex mytype 100 schema-sample.json
```

# Schema definition

Using JSON format the following information can be defined:

- field name
- type (currently supported: integer, long, date, string, text, array, object, enum)
- probability - number from 0 to 100, indicating the probability that the defined field will be present in the generated document (100 means that the field will be present in all the documents, 50 means that about 50% of the documents will contain given field)
- size (for arrays)

Example of input file:

```
{
	"data" : {
		"id" : {
			"type" : "identifier"
		 },
		"tags" : {
			"type" : "array",
			"subtype" : {
				"type": "integer"
			},
			"size" : 10
		},
		"position" : {
			"type" : "object",
			"fields" : {
				"lon" : {
					"type" : "long"
				},
				"lat" : {
					"type" : "long"
				}
			},
			"probability" : 50
		},
		"created" : {
			"type" : "date"
		},
		"name" : {
			"type" : "string",
			"probability" : 30
		},
		"status" : {
			"type" : "enum",
			"available" : ["OPEN", "WAITING", "RUN", "CLOSED"]
		}
	}
}
```
