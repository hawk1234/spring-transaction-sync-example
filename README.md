Spring transaction synchronization example
==========

This repository contains Spring Boot (https://spring.io/projects/spring-boot) 
application example with transactions synchronization.

Requirements
----------
* Java 1.8 latest update installed
* Access to maven central

Features
----------
* Application itself does nothing
* Test that reproduces the problem of transaction synchronization `EmailDistributionServiceTest#testSendingEmailsWaitingToSendIsThreadSafe`

Run tests
----------
```bash
gradlew test
```