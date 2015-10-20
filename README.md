# Introduction
This project includes a number of scripts to generate JSON data or index to Elasticsearch using Sematext's ActionGenerator library

# Build

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
