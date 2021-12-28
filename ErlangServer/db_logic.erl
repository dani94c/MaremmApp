-module(db_logic).
-include("db_schema.hrl").
%05
-include_lib("stdlib/include/qlc.hrl").

-export([init/0, start_db/0, stop_db/0, insert_feedback/2, get_all_feedback/0, delete_feedback/1, get_feedback/1]).

% Initialize Database.
init() ->
    %01
    mnesia:create_schema([node()]),
    %02
    mnesia:start(),
    try
        % Check if the table exists, if it doesen't exists launch an exception
        % type: Returns the table type, which is bag, set, or ordered_set
        mnesia:table_info(type, feedback),
        mnesia:table_info(type, table_id)
    catch
        exit: _ ->
            %03
            mnesia:create_table(feedback, [{attributes, record_info(fields, feedback)},
                {type, ordered_set},{disc_copies, [node()]}]),
            mnesia:create_table(table_id, [{attributes, record_info(fields, table_id)},
                {type, ordered_set},{disc_copies, [node()]}])
    end.

start_db() ->
    mnesia:start().

stop_db() ->
    mnesia:stop().

% Store new feedback into the Database.
insert_feedback(Customer, Message) ->
    %Defines an anonymous function which will performs the storage operation into the DB
    AF = fun() ->
		% 06
        Id = mnesia:dirty_update_counter(table_id,feedback,1),
        %Sets feedback's time with system's local time
        {Time,_} = calendar:universal_time(),
        % Writes into the DB the tuple with all the elements specified as the method's arguments
        % #feedback is the target table (schema) of our query
        mnesia:write(#feedback{id=Id,customer=Customer,time=Time,message=Message})
    end,
    %Submit the query into Mnesia
    mnesia:transaction(AF).

%Returns feedback's id, customer, time and message searching by customer
get_feedback(Customer) ->
    AF = fun() ->
        % q() -> query function: returns a query handle for QLC (query List Comprehensions) -> qlc is a query language
        % Gets from the table "feedback" all the feedbacks of the specified customer
        Query = qlc:q([X || X <- mnesia:table(feedback),
            X#feedback.customer =:= Customer]),
        % e() -> execution of the query: evaluates a query handle in the calling process and collects all answers in a list.
       Results = qlc:e(Query),
	   %Iterates on the Results variable (containing a list) in order to take only the interested attributes from each record
	   %Return only the interested attributes, avoiding to take also the name of the table usually returned in each tuples of the result
	   lists:map(fun(Item) -> {Item#feedback.id, Item#feedback.time, Item#feedback.customer, Item#feedback.message} end, Results)

    end,
    %07
    {atomic, Feedbacks} = mnesia:transaction(AF),
    Feedbacks.


%Return all the Feedbacks in the DB
get_all_feedback()->
	 AF = fun() ->
        % q() -> query function: returns a query handle for QLC (query List Comprehensions) -> qlc is a query language
        % Gets from the table "feedback"" all the feedbacks
        Query = qlc:q([X || X <- mnesia:table(feedback)]),
        % e() -> execution of the query: evaluates a query handle in the calling process and collects all answers in a list.
        Results = qlc:e(Query),
		%Iterates on the Results variable (containing a list) in order to take only the interested attributes from each record
		%Return only the interested attributes, avoiding to take also the name of the table usually returned in each tuples of the result
		lists:map(fun(Item) -> {Item#feedback.id, Item#feedback.time, Item#feedback.customer, Item#feedback.message} end, Results)
    end,
	%07
    {atomic, Feedbacks} = mnesia:transaction(AF),
    Feedbacks.


delete_feedback(Id) ->
    % Defines an anonymous function which will find and delete the tuple in the db having
    % the Id specified into the method's arguments.
    AF = fun() ->
        mnesia:delete({feedback,Id})
    end,
    %Submit the query into Mnesia.
    case mnesia:transaction(AF) of 
       {atomic, ok} -> success;
        _ -> io:format("Error deleting a feedback with id: ~p~n", [Id]),
      error
  	end.


% 01 
    % Creates a new database on disc in the current node. Various files are created in the local Mnesia directory 
    % of each node. Notice that the directory must be unique for each node. Two nodes must 
    % never share the same directory. If possible, use a local disc device to improve performance.
% 02
    % Mnesia startup is asynchronous. The function call mnesia:start() returns the atom ok and then 
    % starts to initialize the different tables. Depending on the size of the database, this can take 
    % some time, and the application programmer must wait for the tables that the application needs before 
    % they can be used. This is achieved by using the function mnesia:wait_for_tables/2
% 03
    % create_table(Name :: table(), Arg :: [create_option()]) -> t_result(ok)
    % create a Mnesia table called Name. 
    % {attributes, record_info(fields, feedback)}: standard in cui bisogna mettere in record_info il nome del record da creare
    % {type, ordered_set}: Type must be either of the atoms set, ordered_set, or bag
    % {disc_copies, [node()]}: we have to specify a list of the Erlang nodes where this table is supposed to have disc copies (in this case only one node). 
	% All updates of the table are performed in the actual table and are also logged to disc. 
	% If a table is of type disc_copies at a certain node, the entire table is resident in RAM memory and on disc. 
    %   If a table replica is of type disc_copies, all write operations on this particular replica of the table are written to disc and to the RAM copy of the table.
%05
    % https://erlang.org/doc/man/qlc.html
    % This module provides a query interface to Mnesia
%06
    % Mnesia has no special counter records. However, records of the form {Tab, Key, Integer} can be used as 
    % (possibly disc-resident) counters when Tab is a set. This function updates a counter with a positive or 
    % negative number. mnesia:dirty_update_counter/3 is performed as an atomic operation although it is not protected by a transaction.
	%If two processes perform mnesia:dirty_update_counter/3 simultaneously, both updates take 
    % effect without the risk of losing one of the updates. The new value NewVal of the counter is returned. 
    % If Key does not exist, a new record is created with value Incr if it is larger than 0, otherwise it is set to 0.
%07 
	% All Mnesia transactions return value {atomic, Val} where Val is the value of the last expression 
    % in Fun() or {aborted, Reason}. atomic means that the query was successful.
	%The code that executes inside a transaction can consist of a series of table manipulation functions. 
	%If something goes wrong inside the transaction as a result of a user error or a certain table not being available, 
	%the entire transaction is terminated and the function transaction/1 returns the tuple {aborted, Reason}.
	%If all is going well, {atomic, ResultOfFun} is returned, where ResultOfFun is the value of the last expression in Fun().
	%Thus, the situation where half data are added can never occur.
	%It is also useful to update the database within a transaction if several processes can concurrently update the same records.
	%When this function executes within a transaction, several processes running on different nodes can concurrently execute the 
	%update function without interfering with each other.
