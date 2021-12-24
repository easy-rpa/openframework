# Excel

### Table of Contents
* [Description](#description)
* [Usage](#usage)
* [Reading of Excel files](#reading-of-excel-files)
* [Creating of Excel files](#creating-of-excel-files)
* [Other Examples](#other-examples)

### Description

Excel package is a library for working with spreadsheet documents. It wraps Apache POI library and provides more easy 
to use interface that is adapted to work within EasyRPA platform or any other RPA platform where processes are built 
using Java program language.

### Usage

To start use the library first you need to add corresponding Maven dependency to your project:

![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/easy-rpa-openframework-excel)

```java
<dependency>
    <groupId>eu.ibagroup</groupId>
    <artifactId>easy-rpa-openframework-excel</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Reading of Excel files

Alongside direct reading values of cells or cells range this library supports a way to work with Excel file data via 
plain java objects. 

Often, the data on Excel sheet is presented using tables. Its a specific range of cells where each cell belongs to some
column that is titled with value of its top row or rows. Lets take a following example that contains table with list of 
persons:

<p align="center">
  <img src="https://i.postimg.cc/jdchRRd5/excel-file-sample.png">
</p>

Using provided `@ExcelColumn` annotation it's possible to tie java class attributes with values in specific columns.             
 ```Java
@Data
public class Person{
    @ExcelColumn(name="Person Id")
    private String id;

    @ExcelColumn(name="Name")
    private String name;

    @ExcelColumn(name="Age")
    private Integer age;

    @ExcelColumn(name="Sex")
    private String sex;
}     
```
After annotating of necessary attributes it's very easy to read data from Excel file and work with it.
```Java
ExcelDocument doc = new ExcelDocument("docs/persons.xslx");
Table<Person> personsTable = doc.getActiveSheet().getTable("B3", Person.class);
for (Person p : personsTable) {
    String personName = p.getName();
    //handling of person data
}    
```

### Creating of Excel files

The same java class that was used for reading Excel file above can be used for inserting new rows into the table:
 ```Java
List<Person> newPersons = getPersonsToAdd();

ExcelDocument doc = new ExcelDocument("docs/persons.xslx");
Table<Person> personsTable = doc.getActiveSheet().getTable("B3", Person.class);
personsTable.addRecords(newPersons);

doc.save();
```

Or building such table from scratch in the new Excel file:
```Java
List<Person> persons = getPersonsList();

ExcelDocument doc = new ExcelDocument();
doc.getActiveSheet().insertTable("B3", persons);

doc.saveAs("output.xlsx");
```
By default all new rows will be rendered with default font and without borders and colors. This can be changed using 
the same annotation `@ExcelColumn` and addition annotation `@ExcelTable`. The difference between `@ExcelColumn` and 
`@ExcelTable` annotations is that `@ExcelColumn` is applied only to cells of corresponding column whereas `@ExcelTable` 
is applied to the whole table.There are two properties `headerStyle` and `cellStyle` for header cell and ordinary cells 
respectively. These properties accepts another annotation `@ExcelCellStyle` that helps to specify specific cell styles 
parameters. 

Lets do some styling:
 
 ```Java
@Data
@ExcelTable(
        headerStyle = @ExcelCellStyle(
                bold = true, background = ExcelColors.GREY_25_PERCENT,
                hAlign = HorizontalAlignment.CENTER, vAlign = VerticalAlignment.CENTER
        ),
        cellStyle = @ExcelCellStyle(
                hAlign = HorizontalAlignment.CENTER, vAlign = VerticalAlignment.TOP
        )     
)
public class Person{
    @ExcelColumn(name="Person Id", width = 10)
    private String id;

    @ExcelColumn(name="Name", width = 20,
            cellStyle = @ExcelCellStyle(
                    hAlign = HorizontalAlignment.LEFT, vAlign = VerticalAlignment.TOP
            ) 
    )
    private String name;

    @ExcelColumn(name="Age", width = 10)
    private Integer age;

    @ExcelColumn(name="Sex", width = 10)
    private String sex;
}     
```

The following result should be gotten after calling of `insertTable()`:

<p align="center">
  <img src="https://i.postimg.cc/y8SWvT5H/excel-file-creating.png">
</p>

### Other Examples

Please refer to [Excel Examples](../../examples#excel) to see more examples of using this library.
