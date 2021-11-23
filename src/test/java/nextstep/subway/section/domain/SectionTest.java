package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:scripts/sectionTestData.sql")
public class SectionTest {

    private static final Integer TEST_DISTANCE = 10;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    StationRepository stationRepository;

    @DisplayName("라인 생성시 구간 추가")
    @Test
    void createLineWithSection() {
        Station seoulStation = stationRepository.findById(1L).get();
        Station yongsanStation = stationRepository.findById(2L).get();

        assertAll(() -> {
            assertThat(seoulStation.getName()).isEqualTo("서울역");
            assertThat(yongsanStation.getName()).isEqualTo("용산역");
        });

        Section upSection = sectionRepository.save(Section.ofUpStation(new Distance(TEST_DISTANCE), seoulStation, yongsanStation));
        Section downSection = sectionRepository.save(Section.fromDownStation(yongsanStation));

        Line line = lineRepository.save(new Line("1호선", "blue"));
        line.addSections(Arrays.asList(upSection, downSection));

        //쿼리 확인
        lineRepository.flush();

        Line findLine = lineRepository.findById(line.getId()).get();
        assertThat(findLine.getSortedSections().size()).isEqualTo(2);

    }


}