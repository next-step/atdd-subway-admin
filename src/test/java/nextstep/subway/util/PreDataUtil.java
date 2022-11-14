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

    public void station(Long id, String name, Long lineId) {
        jdbcTemplate.update(
            "insert into station (id, name, line_id, created_date, modified_date) values (:id, :name, :line_id, sysdate, sysdate)",
            new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("name", name)
                .addValue("line_id", lineId));
    }

    public void line(Long id, String name) {
        jdbcTemplate.update(
            "insert into line (id, created_date, modified_date, color, name) values (:id, sysdate, sysdate, 'color', :name)",
            new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("name", name));
    }
}
