# Simple library backend service

This project is a simple SpringBoot Kotlin backend service for managing books and authors.
This service is responsible for managing two major domain entities - authors (described by name) and
books (described by title, ISBN, genre). An author can naturally publish several books, while a book
can have multiple authors. The service exposes a REST API that supports managing authors and
publishing books as well as exposing them to the public audience.

## Installation

To run this project, you need to have the following tools installed:
- [Maven](https://maven.apache.org/)
- [JDK](https://adoptium.net/temurin/releases/?version=21&os=any&arch=any)
- [docker](https://www.docker.com/)
- [docker-compose](https://docs.docker.com/compose/install/)

After installing these tools, run the following command to build the environment necessary for running the project:

`docker-compose up -d -f tools/docker-compose.yml`