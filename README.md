#Scheelite
[![Build Status](https://travis-ci.org/headstar/scheelite-library.svg?branch=master)](https://travis-ci.org/headstar/scheelite-library) [![Coverage Status](https://img.shields.io/coveralls/headstar/scheelite-library.svg)](https://coveralls.io/r/headstar/scheelite-library?branch=master)

Lightweight Java finite state machine library.

##Features

* Entry/exit actions
* Sub states
* Triggerless transitions
* Immutable state machine logic (no more than one instance needed) 
* Final states
* Completion transition when a composite state is finished (reaches a final state)
* [PlanUML](http://plantuml.com/state.html) diagram writer

##Concepts
See  http://en.wikipedia.org/wiki/UML_state_machine.

###Maven

Current version is 2.1.

```xml
<dependency>
    <groupId>com.headstartech.scheelite</groupId>
    <artifactId>scheelite-core</artifactId>
    <version>2.1.0.RELEASE</version>
</dependency>
```

```xml
<dependency>
    <groupId>com.headstartech.scheelite</groupId>
    <artifactId>scheelite-diagram</artifactId>
    <version>2.1.0.RELEASE</version>
</dependency>
```

##Changes

###2.1
* Explicit support for final states
* Completion transition when a composite state is finished (reaches a final state)
* [PlantUML](http://plantuml.com/state.html) diagram writer

###2.0

#### New features
* Added support for getting state machine configuration from state machine instance.

#### Breaking changes
* Possibility to throw checked exceptions from states, actions and guards.
* Guard interface changed

##License

Scheelite is Open Source software released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
