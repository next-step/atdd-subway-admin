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
class SectionTest {

    @Autowired
    LineRepository lineRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    StationRepository stationRepository;

    @DisplayName("라인 생성시 구간 추가")
    @Test
    public void createLineWithSection() {
        Station seoulStation = stationRepository.findById(1L).get();
        Station yongsanStation = stationRepository.findById(2L).get();

        assertAll(() -> {
            assertThat(seoulStation.getName()).isEqualTo("서울역");
            assertThat(yongsanStation.getName()).isEqualTo("용산역");
        });

        Section section1 = sectionRepository.save(new Section(10, 1, seoulStation, SectionType.UP));
        Section section2 = sectionRepository.save(new Section(10, 2, yongsanStation, SectionType.DOWN));

        Line line = lineRepository.save(new Line("1호선", "blue"));
        line.addSections(Arrays.asList(section1, section2));

        //쿼리 확인
        lineRepository.flush();

        Line findLine = lineRepository.findById(line.getId()).get();
        assertThat(findLine.getSections().size()).isEqualTo(2);

    }


}