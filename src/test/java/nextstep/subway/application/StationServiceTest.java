package nextstep.subway.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class StationServiceTest {
    @Autowired
    private StationRepository stationRepository;

    private StationService stationService;

    private String givenStationName = "강남역";
    private Station givenStation = new Station(givenStationName);

    @BeforeEach
    void setUp() {
        stationService = new StationService(stationRepository);

        stationRepository.save(givenStation);
    }

    @Test
    void StationRequest_객체로_지하철역을_생성할_수_있어야_한다() {
        // given
        final StationRequest stationRequest = new StationRequest("양재역");

        // when
        final StationResponse stationResponse = stationService.saveStation(stationRequest);

        // then
        final Optional<Station> createdStation = stationRepository.findById(stationResponse.getId());
        assertThat(createdStation.isPresent()).isTrue();
    }

    @Test
    void 지하철역_목록을_조회할_수_있어야_한다() {
        // given
        final String newStationName1 = "양재역";
        final String newStationName2 = "판교역";
        stationService.saveStation(new StationRequest(newStationName1));
        stationService.saveStation(new StationRequest(newStationName2));

        // when
        final List<StationResponse> stations = stationService.findAllStations();

        // then
        assertThat(
                stations
                        .stream()
                        .map(StationResponse::getName)
                        .collect(Collectors.toList())
        ).containsExactly(givenStationName, newStationName1, newStationName2);
    }

    @Test
    void 아이디로_지하철역을_삭제할_수_있어야_한다() {
        // given
        final Long givenStationId = givenStation.getId();

        // when
        stationService.deleteStationById(givenStationId);

        // then
        final Optional<Station> deletedStation = stationRepository.findById(givenStationId);
        assertThat(deletedStation.isPresent()).isFalse();
    }

    @Test
    void 아이디로_지하철역_엔티티_1개를_조회할_수_있어야_한다() {
        // given
        final Long givenStationId = givenStation.getId();

        // when
        final Station station = stationService.getStationOrElseThrow(givenStationId);

        // then
        assertThat(station).isEqualTo(station);
    }
}
