package nextstep.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SectionRepositoryTest {

    private static final Station 강남역 = new Station("강남역");
    private static final Station 잠실역 = new Station("잠실역");
    private static final Station 삼성역 = new Station("삼성역");

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private LineRepository lineRepository;

    private Line line;

    @BeforeEach
    void setUp() {
        line = lineRepository.save(new Line("2호선", "green"));
    }

    @Test
    void save() {
        // given
        final Section expected = new Section(강남역, 잠실역, 5);
        expected.setLine(line);

        // when
        final Section actual = sectionRepository.save(expected);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void update() {
        // given
        final Section expected = new Section(강남역, 잠실역, 10);
        sectionRepository.save(expected);

        final Section newSection = new Section(삼성역, 잠실역, 5);

        // when
        expected.changeStationInfo(newSection);
        List<Station> actual = expected.getLineStations();

        // then
        assertThat(actual).containsExactly(강남역, 삼성역);
    }

    @Test
    void delete() {
        // given
        Section expected = new Section(강남역, 잠실역, 5);

        // when
        sectionRepository.delete(expected);
        List<Section> actual = sectionRepository.findAll();

        // then
        assertThat(actual).isEmpty();
    }

}
