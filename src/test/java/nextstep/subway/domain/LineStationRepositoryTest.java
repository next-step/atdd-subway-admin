package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LineStationRepositoryTest {
    @Autowired
    private LineStationRepository lineStationRepository;

    @Test
    void 노선_지하철역_연관관계가_insert_되면_아이디가_부여되고_아이디로_select_할_수_있어야_한다() {
        // given
        final LineStation lineStation = new LineStation(new Line("신분당선", "bg-red-600"),
                new Station("강남역"));

        // when
        final LineStation insertedLineStation = lineStationRepository.save(lineStation);

        // then
        assertThat(insertedLineStation).isEqualTo(lineStation);
        assertThat(insertedLineStation).isEqualTo(lineStationRepository.findById(insertedLineStation.getId()).get());
    }
}
