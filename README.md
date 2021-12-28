# MaremmApp

## Introduction
Maremmapp is a distributed application aimed to promote Maremma's territory. 
Maremmapp allows event planners organize every kind of event and customers to reserve their participation.​
Event planners can register a new event specifying available seats number, description and total seats in order 
to organize respecting Covid-19 security measures.​ Customers can book one or more seats and delete their reservation at any time.​
Both customers and event planners can leave a feedback regarding their experience in Maremma via the dedicated feedback board.

## Architecture of the system
Maremmapp is built as Spring Web Application that is formed by the following nodes:
-WebServerRMI: It is the node which provides Tomcat as web server. It receives frontend’s requests and forwards them to the Erlang Node 
 (feedback’s board) or performs remote method invocation exposed by the RemoteServerRMI node, depending on the precedents requests.
-RemoteServerRMI: is the remote server, exposes the remote interface used by the web server in order to retrieve the data from the Mysql DB. 
 The remote server accepts the requests from the web server and query the DB.
-MySQL DBMS: Maintain the following tables: User, Event and Reservation.
-Erlang Node: Use Mnesia DB to manage users feedback.

## Deployment
In order to deploy the application configure and start the nodes in the following order:
1) MySQL DB and Erlang Node -> Erlang execution instructions are described [here](https://github.com/dani94c/MaremmApp/blob/main/ErlangServer/README.md).
2) RemoteServerRMI -> Execution instructions are described [here](https://github.com/dani94c/MaremmApp/blob/main/RemoteServerRMI/README.md).
3) WebServerRMI -> Execution instructions are described [here](https://github.com/dani94c/MaremmApp/blob/main/WebServerRMI/README.md).

## Credits
This application has been developed by D.Comola, E. Petrangeli, A. Di Marco as part of the Distributed Systems And Middleware Technologies course for the MSc in Computer Engineering @ Univerisity of Pisa in 2020.
