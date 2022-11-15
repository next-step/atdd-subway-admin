package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SectionCommandServiceTest {
    @Autowired
    private SectionCommandService sectionCommandService;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    private Station 신사역;
    private Station 광교역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        신사역 = stationRepository.save(Station.from("신사역"));
        광교역 = stationRepository.save(Station.from("광교역"));
        신분당선 = lineRepository.save(Line.of("신분당선", "red", Section.of(신사역, 광교역, 10)));
    }

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        Station 강남역 = stationRepository.save(Station.from("강남역"));
        sectionCommandService.addSection(신분당선.getId(), SectionRequest.of(신사역.getId(), 강남역.getId(), 5));

        Assertions.assertThat(신분당선.getStationsInOrder()).containsExactly(신사역, 강남역, 광교역);
    }

    @DisplayName("지하철 노선에서 상행종점역을 제거하면 다음역이 상행종점역이 된다.")
    @Test
    void removeUpStation() {
        Station 강남역 = stationRepository.save(Station.from("강남역"));
        sectionCommandService.addSection(신분당선.getId(), SectionRequest.of(신사역.getId(), 강남역.getId(), 5));

        sectionCommandService.removeSection(신분당선.getId(), 신사역.getId());

        Assertions.assertThat(신분당선.getStationsInOrder()).containsExactly(강남역, 광교역);
    }
}
