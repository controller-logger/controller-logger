# Controller Logger

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

WIP