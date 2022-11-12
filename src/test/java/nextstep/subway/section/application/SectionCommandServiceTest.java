package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SectionCommandServiceTest {
    @Autowired
    private SectionCommandService sectionCommandService;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;


    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        Station 신사역 = stationRepository.save(Station.from("신사역"));
        Station 광교역 = stationRepository.save(Station.from("광교역"));
        Line line = lineRepository.save(Line.of("신분당선", "red", Section.of(신사역, 광교역, 10)));

        Station 강남역 = stationRepository.save(Station.from("강남역"));
        sectionCommandService.addSection(line.getId(), SectionRequest.of(신사역.getId(), 강남역.getId(), 5));

        Assertions.assertThat(line.getStationsInOrder())
                .containsExactly(
                        Station.from("신사역"),
                        Station.from("강남역"),
                        Station.from("광교역")
                );
    }
}
