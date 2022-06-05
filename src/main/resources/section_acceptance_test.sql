insert into station(id, name, created_date, modified_date)
values (1, '신논현역', now(), now());
insert into station(id, name, created_date, modified_date)
values (2, '강남역', now(), now());
insert into station(id, name, created_date, modified_date)
values (3, '양재역', now(), now());
insert into station(id, name, created_date, modified_date)
values (4, '정자역', now(), now());
insert into station(id, name, created_date, modified_date)
values (5, '미금역', now(), now());

insert into line(id, name, color, created_date, modified_date)
values (1, '신분당선', 'bg-red-600', now(), now());

insert into line_station(id, line_id, station_id, previous_station_id, next_station_id, created_date, modified_date)
values (1, 1, 2, null, 4, now(), now());
insert into line_station(id, line_id, station_id, previous_station_id, next_station_id, created_date, modified_date)
values (2, 1, 4, 2, null, now(), now());

insert into section(id, line_id, up_station_id, down_station_id, distance, created_date, modified_date)
values (1, 1, 2, 4, 30, now(), now());
