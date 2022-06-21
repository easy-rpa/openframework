# WORD

### Table of Contents
* [Description](#description)
* [Usage](#usage)
* [Reading of Word files](#reading-of-word-files)
* [Creating of Word files](#creating-of-word-files)
* [Other examples](#other-examples)

### Description

Word package is a library for working with word documents. It wraps [Docx4j](https://www.docx4java.org/)
library and provides more easy to use interface that is adapted to work within EasyRPA platform or any other RPA
platform where processes are built using Java program language.

### Usage

To start use the library first you need to add corresponding Maven dependency to your project.

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-excel)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-openframework-word</artifactId>
    <version>1.0</version>
</dependency>
```

Additionally, to let the library collaborate with RPA platform make sure that Maven dependency to corresponding adapter
is added also.

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-adapter-for-openframework)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-adapter-for-openframework</artifactId>
    <version>1.0</version>
</dependency>
```

### Reading of Word files

Together with direct reading of Word files, this library supports a way to work with Word file data through
plain Java objects.

Usually, data on Word pages is presented in the form of paragraphs or tables. 
A paragraph is a piece of text within a section of a document that contains a complete provision or rule and has an independent meaning, that is, a set of text that makes up sentences. 
The table, in turn, is one of the ways to structure data. Represents the distribution of data over the same type of rows and columns that store cells with information. 
Tables are widely used in various research and data analysis. Lets take a following example that create a new paragraph with simple text:

<p align="center">
  <img src="https://i.postimg.cc/qBY7jj53/word-example.png">
</p>

Using provided `p` object of docx4j P class we can easily insert text into any paragraph we need.
 ```Java
Paragraph parapraph = new Paragraph;
paragraph.insertText(p, "simple text");
```

Also we can very easy to read data from Word file and work with it.
```Java
Path path = Paths.get("docs/example.docx");
WordDocument document = new WordDocument(path);
document.findTable(MatchMethod.EXACT, "john");
document.findParagraphByText(MatchMethod.CONTAINS, "salary");
```

### Creating of Word files

The same java class that was used for reading Word file above can be used for inserting new paragraphs:
 ```Java
Path path = Paths.get("docs/example.docx");
WordDocument document = new WordDocument(path);

Paragraph parapraph = new Paragraph;
paragraph.insertText(p, "Hello world!");

document.save();
```

Or creating a table and save as new Word file.
```Java
Path path = Paths.get("docs/example.docx");
WordDocument document = new WordDocument(path);

Table table = new Table();
table.createTable(document.getWordPackage(), 3, 4, true);

doc.saveAs("output.docx");
```
By default all new paragraphs will be rendered with default font and without borders and colors. This can be changed using
the method `insertStyledText()`. There are three properties `p`, `resolver` and `style`. 
The first parameter indicates which particular paragraph you want to style, 
the second is a functional object of the class with settings necessary for the structure of the Word file to immediately apply the required style,
the third is the style for which there is a separate class for ease of use.

Lets do some styling:

 ```Java
Path path = Paths.get("docs/example.docx");
WordDocument document = new WordDocument(path);

Paragraph parapraph = new Paragraph;
paragraph.insertStyledText(p, document.getPropertyResolver(), ParagraphStyles.HEADING_1);

document.save();
```

The following result should be gotten after calling of `save()`:

<p align="center">
  <img src="https://i.postimg.cc/nVTWJWJX/word-table-example.png">
</p>

### Other examples

Please refer to [Word Examples](../../examples#word) to see more examples of using this library.
