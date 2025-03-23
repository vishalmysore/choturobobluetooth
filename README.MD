## ChotuRoboServer
ChotuRoboServer is a Java-based implementation of the Model Integration Protocol (MIP) using the Neurocaster Server. This server facilitates the conversion of existing Java services, methods, and classes into tools that can be integrated with AI systems through simple JSON-RPC calls. It aims to make it easier to integrate existing code into AI workflows without writing new services from scratch.  

Read the full documentation [here](https://medium.com/@visrow/model-context-protocol-alternative-spring-and-java-ai-tools-integration-10357d798f09)


## Features
Full Java Integration: ChotuRoboServer is written in 100% Java, making it easy to integrate with existing Java applications and services.

Model Integration Protocol (MIP): Using MIP, ChotuRoboServer converts Java methods, services, and classes into JSON-RPC tools that can be accessed by AI models.

**Neurocaster:** The framework utilizes Neurocaster for managing the conversion of Java methods to JSON-RPC.

**Scalable and Extensible:** Easily add new services or methods to ChotuRoboServer and integrate them into the AI system with minimal effort.

## How It Works
ChotuRoboServer leverages Java's reflection capabilities and annotations to automate the conversion of Java methods into tools that can be used by AI models. It supports a variety of method parameters, including simple types, collections, and even custom objects, to make the integration process flexible and straightforward

Tools4AI is here [Tools4AI](https://github.com/vishalmysore/Tools4AI)  
Server is here [Neurocaster-Server](https://github.com/vishalmysore/neurocaster-server)   
Client is here [Neurocaster-Client](https://github.com/vishalmysore/neurocaster-client)

## Getting Started

Start the server as a Spring Boot application. The server will expose the converted Java methods as JSON-RPC tools that can be accessed by AI models.
Connect the AI model to the server using the Neurocaster-Client library. The client library provides an easy-to-use interface for making JSON-RPC calls to the server and executing the Java methods.
