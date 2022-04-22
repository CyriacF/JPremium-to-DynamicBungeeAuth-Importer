
# JPremium to DynamicBungeeAuth Importer

That tools can convert all JPremium data to DynamicBungeeAuth (by using SHA512 in DBA)




## Installation

How to compil and use the Converter / Importer :

```bash
  mvn package
  java -jar Converter.jar
```

## Usage
You need to enter the 2 SQL database information in the Function : getJPREMDatabaseConnection() and getDBADatabaseConnection()

Like this :
```java
    String host = "0.0.0.0";
    String database = "database";
    String user = "user";
    String password = "password";
```


## Authors

- [@SiriHack](https://www.github.com/SiriHackYT)