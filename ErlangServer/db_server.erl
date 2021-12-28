-module(db_server).

-behaviour(gen_server).
-import(db_logic,[init/0, start_db/0, stop_db/0, insert_feedback/2, get_all_feedback/0, delete_feedback/1, get_feedback/1]).

%% API
-export([start/0, stop/0]).

%% gen_server
-export([init/1, handle_call/3, handle_cast/2, handle_info/2, terminate/2, code_change/3]).

%% API
start() ->
    % Creates a standalone gen_server process, that is, a gen_server process that is not part of a supervision 
    % tree and thus has no supervisor.
    gen_server:start({local, db_server}, ?MODULE, [], []).

init(_Args) ->
    {ok, {}}.

stop() ->
  gen_server:stop(db_server).

handle_call({insert_feedback, Customer, Message}, _From, State) ->
    Response = db_logic:insert_feedback(Customer, Message),
    io:format("Feedback has been added for Customer ~p~n",[Customer]),
    {reply,Response,State};

handle_call({get_all_feedback}, _From, State) ->
    Response = db_logic:get_all_feedback(),
    {reply,Response,State};

handle_call({get_feedback, Customer}, _From, State) ->
    Response = db_logic:get_feedback(Customer),
    {reply,Response,State};

handle_call({delete_feedback, Id}, _From, State) ->
    Response = db_logic:delete_feedback(Id),
    case Response of 
        success ->  io:format("Feedback with id: ~p has been removed from DB~n",[Id]),
                    {reply,Response,State};
        _ ->Response,
            {reply,Response,State}
    end;
    
handle_call(_Request, _From, _) ->
    {reply, bad_request, {}}.

handle_cast(_Request, State) ->
    {noreply, State}.

handle_info(_Info, State) ->
    {noreply, State}.

terminate(_Reason, _State) ->
    ok.

code_change(_OldVan, State, _Extra) ->
    {ok, State}.

