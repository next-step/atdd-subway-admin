package nextstep.subway.util;

import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PreDataUtil {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PreDataUtil(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void truncate() {
        jdbcTemplate.update("SET REFERENTIAL_INTEGRITY FALSE", new EmptySqlParameterSource());
        jdbcTemplate.update("truncate table station", new EmptySqlParameterSource());
        jdbcTemplate.update("truncate table line", new EmptySqlParameterSource());
        jdbcTemplate.update("SET REFERENTIAL_INTEGRITY TRUE", new EmptySqlParameterSource());
    }

    public void station(Long id, String name) {
        jdbcTemplate.update(
            "insert into station (id, name, created_date, modified_date) values (:id, :name, sysdate, sysdate)",
            new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("name", name));
    }

    public void line(Long id, String name) {
        jdbcTemplate.update(
            "insert into line (id, created_date, modified_date, color, name) values (:id, sysdate, sysdate, 'color', :name)",
            new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("name", name));
    }

    public void lineStation(Long id, Long stationId, Long preStationId, Long lineId, Integer distance) {
        jdbcTemplate.update(
            "insert into line_station (id, station_id, pre_station_id, distance, line_id) values (:id, :stationId, :preStationId, :distance, :lineId)",
            new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("stationId", stationId)
                .addValue("preStationId", preStationId)
                .addValue("distance", distance)
                .addValue("lineId", lineId));
    }
}
