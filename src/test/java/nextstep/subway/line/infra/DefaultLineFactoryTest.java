package nextstep.subway.line.infra;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayName("기본 지하철 노선 공장에 대한 테스트")
class DefaultLineFactoryTest {

    @Mock
    private StationRepository stationRepository;

    @DisplayName("기본 지하철 노선 공장은 지하철 노선을 생성합니다.")
    @Test
    void create() {
        // given
        String name = "신분당선";
        String color = "red";
        long upStationId = 1L;
        long downStationId = 2L;
        Station upStation = new Station("청량리역");
        Station downStation = new Station("신도림역");
        when(stationRepository.findById(upStationId)).thenReturn(Optional.of(upStation));
        when(stationRepository.findById(downStationId)).thenReturn(Optional.of(downStation));
        int distance = 10;
        LineRequest lineRequest = LineRequest.builder()
                .name(name)
                .color(color)
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();

        // when
        DefaultLineFactory lineFactory = new DefaultLineFactory(stationRepository);
        Line line = lineFactory.create(lineRequest);

        // then
        assertThat(line).isNotNull();
        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
        assertThat(line.getStationsOrderByUp()).containsExactly(upStation, downStation);
    }
}
