![Controller Logger](docs/banner.png?raw=true "Banner")


    createUser() called with arguments: id: [4], name: [Foo] called via url [http://localhost:8080/createUser], username [admin]


<div align="center">

[![Discord](https://user-images.githubusercontent.com/7288322/34429152-141689f8-ecb9-11e7-8003-b5a10a5fcb29.png?style=popout)](https://discord.gg/putPJpQ)
[![CircleCI](https://circleci.com/gh/harshilsharma63/controller-logger.svg?style=svg)](https://circleci.com/gh/harshilsharma63/controller-logger)
![](https://sonarcloud.io/api/project_badges/measure?project=io.github.harshilsharma63%3Acontroller-logger&metric=alert_status)
[![codecov](https://codecov.io/gh/harshilsharma63/controller-logger/branch/master/graph/badge.svg)](https://codecov.io/gh/harshilsharma63/controller-logger)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fharshilsharma63%2Fcontroller-logger.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fharshilsharma63%2Fcontroller-logger?ref=badge_shield)

</div>

### Upcoming release features
1. Performance improvements
1. Dependency updates

### What is it?

A Java library to ease the life of web developers by providing automatic logging of
input-output for controllers. Logged items include all parameters, returned value
and some context data such as web request URL and user's username.

### Features

1. Automatically logs all APIs including input and output.
1. Automatically logs errors occurring in API.
1. No side-effect in actual API implementation due to AOP logic.
1. Automatically binds to new APIs thanks to AOP weaving.
1. Scrubs sensitive information in logs to maintain security and privacy.
1. Displays file size if one of the API input or output is any file object.
1. Works with integration testing.
1. Detects mocked objects in input and output and displays them accordingly, as can happen during integration testing.
1. Logging behaviour is easily customizable.

### Performance

The code has been through multiple profiling cycles. Each part of the code is intended for maximum performance.
No object it serialized if it didn't need to.
 
### Why does it exist?

While working on a project, a Spring-based web application, it quickly became annoying adding
the same log statements for each new controller method being created. Working in a team
made it inconsistent too with people forgetting or adding unformatted logs. This was
the perfect use case for AOP (Aspect Oriented Programming). This library is re-built
based on experience gained during my original implementation of AOP based logging.

### How can I use this awesome library?

#### Requirements -
* Java 8
* Spring Boot

Through requirements specify Spring Boot, you will be able to use it with classic Spring Framework
as well but you'll have to implement weaving manually.
[This StackOverflow question](https://stackoverflow.com/questions/41373745/spring-cache-with-couchbase-using-loadtimeweaving-strangely-not-working)
of mine will be helpful in doing so.

### Usage

Example usage of this library can be found here - https://github.com/harshilsharma63/controller-logger-example

#### Setup

1. Add dependency from http://www.mvnrepository.com/artifact/io.github.harshilsharma63/controller-logger

  * Gradle
        
        compile group: 'io.github.harshilsharma63', name: 'controller-logger', version: '1.2.0'
    
  * Maven
 
        <dependency>
            <groupId>io.github.harshilsharma63</groupId>
            <artifactId>controller-logger</artifactId>
            <version>1.2.0</version>
        </dependency>

2. Add aspect bean in one of the `@Configuration` classes

        @Bean
        public GenericControllerAspect genericControllerAspect() {
            return new GenericControllerAspect();
        }
        
3. Enable logging by adding `@Logging` annotation on controller classes(s)

        @RestController
        @Logging
        public class MyController {
            ...
        }
        
### Data Scrubbing

This library supports hiding sensitive information from being logged. As of now, this works only with
method arguments but support for arbitrary fields in objects is on the way.

Data scrubbing is enabled by default and it's recommended to keep it that way.
 
A method parameter is scrubbed if its name falls withing following criteria:

* Is one of following values (case insensitive):
    * password
    * passwd
    * secret
    * authorization
    * api_key
    * apikey
    * access_token
    * accesstoken

* Is contained in custom blacklist provided to `setCustomParamBlacklist()`

* Matches custom regex provided to `setParamBlacklistRegex()`

Value of any param matching above mentioned criteria is scrubbed and replaced by "xxxxx". The
scrubbed value can be customized as well by passing in the desired value to
`setDefaultScrubbedValue()` method.

A full example with all customization options used:

    @Bean
    public GenericControllerAspect genericControllerAspect() {
        GenericControllerAspect aspect = new GenericControllerAspect();

        aspect.setEnableDataScrubbing(true);
        aspect.setDefaultScrubbedValue("*******");
        aspect.setParamBlacklistRegex("account.*");
        aspect.setCustomParamBlacklist(new HashSet<>(Arrays.asList("securityProtocol")));
        return aspect;
    }

#### Customization

Logging is controlled by two annotations `@Logging` and `@NoLogging`. The two can be used together to achieve
fine-grain control over which methods are logged and which aren't.

Both `@Logging` and `@NoLogging` annotations can be used on class as well as methods. Method-level annotation takes
priority over class-level annotation. This can be used to enable logging for all controller methods and exclude some,
or vice-versa.

        @RestController
        @Logging
        public class MyController {
            
            @RequestMapping("/hello")
            public String hello() {
                // logging will work for this method
            }
            
            @RequestMapping("/bye")
            @NoLogging
            public String bye() {
                // logging will not work for this method
            }
            
        }
        
Further customizations can be done by extending `GenericControllerAspect` class, or create your own aspect by implementing
`ControllerAspect` interface.

### Future Scope

- [x] Avoid logging sensitive information such as passwords, cookie data, session information.
- [ ] Test with Spring 5
- [ ] Test with Java 9
- [x] Add unit tests
