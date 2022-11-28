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
        insertLineAndStations();
        List<Line> allLines = lineRepository.findAllLines();
        assertThat(allLines).hasSize(1);
        assertThat(allLines.get(0).getLineStations().getStationsInOrder().get(0)).isNotNull();
        assertThat(allLines.get(0).getLineStations().getStationsInOrder().get(1)).isNotNull();
    }

    private void insertLineAndStations() {
        preDataUtil.line(1L, "노선1");
        preDataUtil.station(1L, "역1");
        preDataUtil.station(2L, "역2");
        preDataUtil.lineStation(1L, 2L, 1L, 1L, 10);
        preDataUtil.lineStation(2L, 1L, null, 1L, null);
    }

}
