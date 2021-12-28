Move inside the folder ErlangServer
>cd <DIR_PATH>

Create a folder which will contain all the database data (to do only at the first start up of the erlang node):
mkdir MnesiaDB

Open an erlang shell configuring parameters for the distributed communication and the MnesiaDB usage:
> erl -name <SERVER_REGISTERED_NAME>@<SERVER_IP> -setcookie <MAGIC_COOKIE> -mnesia dir '"MnesiaDB"'

From the erlang shell compile the files db_logic.erl and db_server.erl (only on the first start up):
> c(db_logic).
> c(db_server).

Create the database and start the server (only on the first startup):
> db_logic:init().
> db_server:start().

Start the server and the MnesiaDB (not on the first startup)
> db_logic:start_db().
> db_server:start().

Stop the server:
> db_server:stop().
> db_logic:stop_db().