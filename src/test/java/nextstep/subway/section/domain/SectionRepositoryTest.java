package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@DataJpaTest
public class SectionRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private SectionRepository sectionRepository;

    private final int distance = 100;
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        upStation = testEntityManager.persist(new Station("강남역"));
        downStation = testEntityManager.persist(new Station("역삼역"));
    }

    @DisplayName("구간 저장 테스트")
    @Test
    void save() {
        // given
        final Section expected = new Section(upStation, downStation, distance, new Line("1호선", "blue"));

        // when
        final Section actual = sectionRepository.save(expected);

        // then
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual).isEqualTo(expected)
        );
    }

    @DisplayName("상행역과 하행역, 노선으로 등록된 구간이 있는지 조회 테스트")
    @Test
    void findIdByStationsAndLine() {
        // given
        final List<Station> stations = Arrays.asList(upStation, downStation);
        final Line line = testEntityManager.persist(new Line("1호선", "bg-blue-600"));
        final Section expected = sectionRepository.save(new Section(stations, distance, line));

        // when
        final Section actual = sectionRepository.findIdByStationsAndLine(upStation, downStation, line);

        // then
        assertThat(actual.getId()).isEqualTo(expected.getId());
    }
}
