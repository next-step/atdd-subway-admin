package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    LineRepository lineRepository;

    @Mock
    StationRepository stationRepository;

    @Test
    void saveLine() {
        //given
        Long 역삼역Id = 1L;
        Long 강남역Id = 2L;
        Station 역삼역 = new Station(역삼역Id, "역삼역");
        Station 강남역 = new Station(강남역Id, "강남역");
        LineService lineService = new LineService(lineRepository, stationRepository);

        when(stationRepository.findAllById(any()))
                .thenReturn(Arrays.asList(강남역, 역삼역));

        when(lineRepository.save(any()))
                .thenReturn(new Line(1L, "신분당선", "bg-red-600"));

        //when
        LineRequest 신분당선LineRequest = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(),
                7);
        LineResponse 신분당선LineCreateResponse = lineService.saveLine(신분당선LineRequest);

        //then
        assertThat(신분당선LineCreateResponse).isNotNull();
        assertThat(신분당선LineCreateResponse.getName()).isEqualTo("신분당선");
        assertThat(신분당선LineCreateResponse.getColor()).isEqualTo("bg-red-600");
        assertThat(
                신분당선LineCreateResponse.getStations()
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList())
        ).contains(역삼역Id, 강남역Id);
    }
}