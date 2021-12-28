## Web Server Setup

Modify the src/main/resources/application.properties in order to specify connections parameters.\
**** Pay attention to not add spaces between equals and at the end of each line!! ****

More in detail:
- Set the connection parameters for the RemoteServerRMI node that exports the RMI
utils.remoteServerIP=<REMOTE_SERVER_IP>
utils.remoteServerPort=<REMOTE_SERVER_PORT>

- Set the connection parameters for the ErlangClient node that connects to the Erlang Server node. 
  The cookie MUST be the same used on the Erlang Server node
utils.cookie=<MAGIC_COOKIE>
utils.serverNodeName=<SERVER_REGISTERED_NAME>@<SERVER_IP_ADDRESS>
utils.serverRegisteredName=<SERVER_REGISTERED_NAME>
utils.clientNodeName=<CLIENT_REGISTERED_NAME>@<CLIENT_IP_ADDRESS>
utils.mBoxName=<MAILBOX_NAME>

- Parameters for WebServer Controller
- Specify the number of events per page shown in the Event Table
utils.numEventsPerPage=<NUM_EVENTS_PER_PAGE>

How to run WebServerRMI
>cd WebServerRMI
>erl -name <CLIENT_REGISTERED_NAME>@<CLIENT_IP_ADDRESS> -setcookie '<MAGIC_COOKIE>'

inside the erl shell run the command 
>q().

>mvn spring-boot:run
