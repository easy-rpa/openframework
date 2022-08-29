# Business Calendar

### Table of Contents
* [Description](#description)
* [Usage](#usage)
* [Instance creation and base functionality](#api-client-service-authorization-and-instantiation)
* [Other examples](#other-examples)

### Description

EasyRPA Open Framework **Business Calendar** library provides functionality to perform authentication, authorization and
instantiation of [Google Workspace API client services](https://developers.google.com/workspace/products) like Drive, 
Sheets, Calendar etc. It hides lots of implementation and configuration details behind that is very important in case of
using it within RPA process. When the business logic of the process should be easy to read and perceive and 
implementation details should not interfere to do it.  

### Usage

To start use the library first you need to add corresponding Maven dependency to your project.

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-google-services)
```xml
<dependency>
       <groupId>eu.easyrpa</groupId>
       <artifactId>easy-rpa-openFramework-biz-calendar</artifactId>
       <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Additionally, to let the library collaborate with RPA platform make sure that Maven dependency to corresponding adapter 
is added also. 

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-adapter-for-openframework)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-adapter-for-openframework</artifactId>
    <version>2.3.1</version>
</dependency>
```

### Instance creation and base functionality

The key class of this library is the `BizCalendar` class. This class provides base functionality to work with dates such
as checking whether the date is a working day or a holiday, how many working days and holidays are there in the certain
range, count working days in range and so on.

Below the example of creating an instance `BizCalendar` and counting the working days in range.
```java
@Inject
private HolidayRepository holidayRepository;

public void execute() {
    ...        
    BizCalendar bizCalendar = new BizCalendar(holidayRepository);
    int numberOfWorkingDaysInRange = bizCalendar.countWorkingDaysInRange(LocalDate.of(2022,2,3)
        , LocalDate.of(2022,4,6));
    ...
}
```  
To create the `BizCalendar` class you must pass another class of this library as a constructor parameter - `HolidayRepository`
class. `HolidayRepository` is a class that is used to simplify and organize DB related operations. It provides basic 
operations like saving or deleting an object as well as custom query creation via @Query annotation. In our case we use 
this class to connect with DataStore where we hold all information about holidays of different countries.

### Other examples

Please refer to [Business calendar Examples](../../examples#google-services) to see more examples of using this library.

