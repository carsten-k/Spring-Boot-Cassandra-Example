# Spring-Boot-Cassandra-Example

Simple Spring application to demonstrate how Spring Boot works with Apache Cassandra. It contains two tables that usually would be joined in a relational database. It also shows how to use [lightweight transactions](https://blog.pythian.com/lightweight-transactions-cassandra/) in Cassandra to ensure that an action can be executed only once.

This project uses a demo Cassandra database, hosted on Google Cloud Platform and managed by [Datastax Astra](https://astra.datastax.com/) 