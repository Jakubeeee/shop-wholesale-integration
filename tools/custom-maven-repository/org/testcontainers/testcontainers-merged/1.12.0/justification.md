This artifact is a merged version of below org.testcontainers modules:
- org.testcontainers:testcontainers:1.12.0
- org.testcontainers:jdbc:1.12.0
- org.testcontainers:postgresql:1.12.0

It's used as a temporary workaround to use testcontainers library with java module system.
Standard modules cannot be used because of two reasons:
- All three modules contain classes in the package  ***org.testcontainers.containers***. Github issue:
https://github.com/testcontainers/testcontainers-java/issues/1524
- The META-INF/services directory contains entries that should be removed. Github issue: 
https://github.com/testcontainers/testcontainers-java/issues/1424

This workaround can be removed after appropriate fixes are introduced in testcontainers library.