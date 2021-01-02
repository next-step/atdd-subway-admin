package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineCreateResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
        Station station1 = new Station(1L, "역삼역");
        Station station2 = new Station(2L, "강남역");
        LineService lineService = new LineService(lineRepository, stationRepository);

        when(stationRepository.findAllById(any()))
                .thenReturn(Arrays.asList(station2, station1));

        when(lineRepository.save(any()))
                .thenReturn(new Line(1L, "신분당선", "bg-red-600", station2, station1, 7));

        //when
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station2.getId(), station1.getId(),
                7);
        LineCreateResponse lineCreateResponse = lineService.saveLine(lineRequest);

        //then
        assertThat(lineCreateResponse).isNotNull();
        assertThat(lineCreateResponse.getName()).isEqualTo("신분당선");
        assertThat(lineCreateResponse.getColor()).isEqualTo("bg-red-600");
        assertThat(lineCreateResponse.getUpStationId()).isEqualTo(2L);
        assertThat(lineCreateResponse.getDownStationId()).isEqualTo(1L);
        assertThat(lineCreateResponse.getDistance()).isEqualTo(7);
    }
}