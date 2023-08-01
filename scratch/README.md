## POC : Opentemetry Tracing Sample Instrumentation, Collector and ZipKin

### Overview
This is a sample project to demonstrate how to use OpenTelemetry to instrument a simple application and send the telemetry data to a collector and then to a ZipKin server.

### Prerequisites
- Docker
- Docker Compose
- Java 11

### How to run
- Clone the project
- Change Directory to the project root/scratch. This is where the docker-compose.yml file is located.
- Run `docker-compose up` to start the collector and ZipKin server
- Run Main.java in the scratch module to start the application.
- Open http://localhost:9411/zipkin/ to see the traces
- Run `docker-compose down` to stop the collector and ZipKin server

### References
- https://opentelemetry.io/docs/java/manual_instrumentation/