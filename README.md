# Jockes-dependency-plugin

Jocke ville ha ett plugin för IntelliJ där han kunde kopiera Maven dependencies eller gamla Gradle Groovy-style
dependencies in i ett Gradle Kotlin-style dependency-block. Med detta pluginet så kan du lätt kopiera ett godtyckligt
antal dependencies från ett Gradle/Groovy eller Maven projekt in i ditt Gradle/Kotlin projekt utan att behöva göra
något annat!

Prova att kopiera följande in i ett Gradle projekt med Kotlin-DSL:
```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.14.2</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.14.2</version>
    <scope>test</scope>
</dependency>
```

eller följande:
```groovy
implementation group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-xml', version: '2.14.2'
testImplementation 'com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.14.2'
```

så får du se magi hända, allt tack vare Jocke! Och lite Hiiipen också.