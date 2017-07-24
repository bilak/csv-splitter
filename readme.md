# CSV SPLITTER 
---
### Utility for splitting csv by distinct csv's data

### Configuration parameters
TODO

### Splitter steps 
* csv file `app.split.inputFile` is imported to H2 database's table `app.split.importTableName`
* `SplitDefinition`s are generated selecting distinct columns `app.split.splitBy`
* based on `SplitDefinition`s output csv files are generated in `app.split.outputDirectory`


### Example
with following csv `/tmp/test-data.csv`
```
first_name  |  last_name  |  location
------------|-------------|-----------------
Tom         |  Peterson   |  New Yorg
Tom         |  Perry      |  Paris
Martin      |  Yullien    |  Boston
Suzanne     |  Vega       |  Boston
```

and configuration:
```
app:
  split:
    importTableName: T_IMPORT
    inputFile: /tmp/test-data.csv
    outputDirectory: /tmp/csv-split-output
    splitBy: first_name
    splitByType: COLUMN_NAME
    inputCsv:
      fieldSeparator: ';'
    outputCsv:
      fieldSeparator: ';'
```

there will be output:
```
/tmp/csv-split-output/FIRST_NAME_0.csv
"FIRST_NAME";"LAST_NAME";"LOCATION"
"Tom";"Peterson";"New Yorg"
"Tom";"Perry";"Paris"

/tmp/csv-split-output/FIRST_NAME_1.csv
"FIRST_NAME";"LAST_NAME";"LOCATION"
"Martin";"Yullien";"Boston"

/tmp/csv-split-output/FIRST_NAME_2.csv
"FIRST_NAME";"LAST_NAME";"LOCATION"
"Suzanne";"Vega";"Boston"
```

---
##### TODO
* SPEL functionality for output file names
* JUNIT tests
* better documentation
