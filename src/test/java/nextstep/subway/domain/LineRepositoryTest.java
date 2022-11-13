package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import nextstep.subway.util.PreDataUtil;

@DataJpaTest
@Import(PreDataUtil.class)
class LineRepositoryTest {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    LineRepository lineRepository;
    @Autowired
    PreDataUtil preDataUtil;

    @Test
    void 노선_역_fetch_join() {
        insertLineStations();
        List<Line> allLines = lineRepository.findAllLines();
        assertThat(allLines).hasSize(1);
        assertThat(allLines.get(0).getUpStation()).isNotNull();
        assertThat(allLines.get(0).getDownStation()).isNotNull();
    }

    private void insertLineStations() {
        preDataUtil.line(1L, "노선1");
        preDataUtil.station(1L, "역1", 1L);
        preDataUtil.station(2L, "역2", 1L);
    }

}
