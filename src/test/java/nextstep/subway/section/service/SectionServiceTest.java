package nextstep.subway.section.service;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
import java.util.stream.Stream;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class SectionServiceTest {

    @Autowired
    StationRepository stationRepository;
    @Autowired
    LineRepository lineRepository;
    SectionService sectionService;

    private Station upStation;
    private Station downStation;
    private Station newDownStation;
    private Line line;


    @BeforeEach
    public void setUp() {
        sectionService = new SectionService(lineRepository, stationRepository);

        upStation = new Station("신림역");
        downStation = new Station("서울대입구역");
        newDownStation = new Station("봉천역");
        stationRepository.saveAll(Arrays.asList(upStation, downStation, newDownStation));

        line = new Line("2호선", "bg-green-500", new Section(upStation, downStation, new Distance(10L)));
        lineRepository.save(line);
    }

    @TestFactory
    @DisplayName("구간 추가시 입력값에 따른 예외상황 체크")
    Stream<DynamicTest> providerAddSection_failCase() {
        return Stream.of(
            DynamicTest.dynamicTest("노선이 존재하지 않는 경우", () -> {
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> sectionService.addSection(line.getId() + 1,
                        new SectionRequest(upStation.getId(), newDownStation.getId(), 5L)));
            }),
            DynamicTest.dynamicTest("하행역이 존재하지 않는 경우", () -> {
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> sectionService.addSection(line.getId(),
                        new SectionRequest(upStation.getId(), newDownStation.getId() + 1, 5L)));
            })
            ,
            DynamicTest.dynamicTest("상행역이 존재하지 않는 경우", () -> {
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> sectionService.addSection(line.getId(),
                        new SectionRequest(newDownStation.getId() + 1, downStation.getId(), 5L)));
            })
        );
    }

}
