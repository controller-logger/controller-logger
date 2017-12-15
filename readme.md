# Controller Logger

        createUser() called with arguments: id: [4], name: [Foo] called via url [http://localhost:8080/createUser], username [admin]

### What is it?

A Java library to ease life of web developers by providing automatic logging 
of input-output for controllers. Logged items include all parameters, returned value
and some context data such as web request URL and user's username.
 
### Why does it exist?

While working on a project, a Spring based web application, it quickly became annoying adding
the same log statements for each new controller method being created. Working in team
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

#### Setup

1. Add aspect bean in one of the `@Configuration` classes

        @Bean
        public GenericControllerAspect genericControllerAspect() {
            return new GenericControllerAspect();
        }
        
2. Enable logging by adding `@Logging` annotation on controller classes(s)

        @RestController
        @Logging
        public class MyController {
            ...
        }

#### Customization

Logging is controlled by two annotations `@Logging` and `@NoLogging`. The two can be used together to achieve 
fine-grain control over which methods are logged and which aren't.

Both `@Logging` and `@NoLogging` annotations can be used on class as well as methods. Method level annotation takes
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
        
Further customizations can be done by extending `GenericControllerAspect` class, or create your own aspect by 
implementing `ControllerAspect` interface. 