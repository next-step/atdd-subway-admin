delete from line;
alter table line alter column id restart with 1;
delete from station;
alter table station alter column id restart with 1;
