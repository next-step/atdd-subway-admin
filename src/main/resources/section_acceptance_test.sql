insert into station(id, name, created_date, modified_date)
values (1, '강남역', now(), now());
insert into station(id, name, created_date, modified_date)
values (2, '양재역', now(), now());
insert into station(id, name, created_date, modified_date)
values (3, '판교역', now(), now());
insert into station(id, name, created_date, modified_date)
values (4, '정자역', now(), now());

insert into line(id, name, color, created_date, modified_date)
values (1, '신분당선', 'bg-red-600', now(), now());

insert into line_station(id, line_id, station_id, previous_station_id, created_date, modified_date)
values (1, 1, 1, null, now(), now());
insert into line_station(id, line_id, station_id, previous_station_id, created_date, modified_date)
values (2, 1, 4, 1, now(), now());

insert into section(id, line_id, up_station_id, down_station_id, distance, created_date, modified_date)
values (1, 1, 1, 4, 30, now(), now());
