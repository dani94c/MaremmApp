% table's name: feedback
% tuples: id, customer, time and message. The first specified attribute is the key
-record(feedback, {id,customer,time,message}).
% This table is needed in order to take track of the last id generated for a specific table
-record(table_id, {table_name, last_id}).