## Erlang Server Setup

Move inside the folder ErlangServer
```sh
>cd <DIR_PATH>
```
Create a folder which will contain all the database data (to do only at the first start up of the erlang node):
```sh
>mkdir MnesiaDB
```
Open an erlang shell configuring parameters for the distributed communication and the MnesiaDB usage:
```sh
> erl -name <SERVER_REGISTERED_NAME>@<SERVER_IP> -setcookie <MAGIC_COOKIE> -mnesia dir '"MnesiaDB"'
```
From the erlang shell compile the files db_logic.erl and db_server.erl (only on the first start up):
```sh
> c(db_logic).
> c(db_server).
```
Create the database and start the server (only on the first startup):
```sh
> db_logic:init().
> db_server:start().
```
Start the server and the MnesiaDB (not on the first startup)
```sh
> db_logic:start_db().
> db_server:start().
```
In order to stop the server:
```sh
> db_server:stop().
> db_logic:stop_db().
```
