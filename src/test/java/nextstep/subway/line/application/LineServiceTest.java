package nextstep.subway.line.application;

import nextstep.subway.exception.ApiException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nextstep.subway.exception.ApiExceptionMessge.NOT_FOUND_STATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@DisplayName("노선 관리 서비스 계층 테스트")
@ExtendWith(MockitoExtension.class)
class LineServiceTest {

	@Mock
	private LineRepository lines;

	@Mock
	private StationRepository stations;

	@InjectMocks
	private LineService lineService;


	@Test
	@DisplayName("노선 등록 테스트")
	void saveLineTest() {
		// given
		Station 강남역 = new Station("강남역");
		Station 역삼역 = new Station("역삼역");
		Line 신분당선 = new Line("신분당선", "bg-red-600");
		신분당선.addSection(new Section(강남역, 역삼역, 10));

		LineRequest request = new LineRequest("신분당선", "bg-red-600", 1l, 2l, 10);
		Mockito.when(stations.findById(1l)).thenReturn(Optional.of(강남역));
		Mockito.when(stations.findById(2l)).thenReturn(Optional.of(역삼역));
		Mockito.when(lines.save(any(Line.class))).thenReturn(신분당선);

		// when
		LineResponse response = lineService.saveLine(request);

		// then
		assertAll(() -> {
			assertThat(response).isNotNull();
			assertThat(response.getStations()).hasSize(2);
			assertThat(response.getStations().get(0).getName()).isEqualTo(강남역.getName());
			assertThat(response.getStations().get(1).getName()).isEqualTo(역삼역.getName());
		});

		Mockito.verify(stations).findById(1l);
		Mockito.verify(stations).findById(2l);
		Mockito.verify(lines).save(any(Line.class));
	}

	@Test
	@DisplayName("노선 등록 테스트, 지하철역 정보가 없는 경우")
	void saveLineTestWithOutStation() {
		// given
		Station 강남역 = new Station("강남역");
		Station 역삼역 = new Station("역삼역");
		Line 신분당선 = new Line("신분당선", "bg-red-600");
		신분당선.addSection(new Section(강남역, 역삼역, 10));

		LineRequest request = new LineRequest("신분당선", "bg-red-600", 1l, 2l, 10);
		Mockito.when(stations.findById(1l)).thenReturn(Optional.of(강남역));
		Mockito.when(stations.findById(2l)).thenReturn(Optional.empty());

		// when
		assertThatThrownBy(() -> lineService.saveLine(request))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining(NOT_FOUND_STATION.message());

		Mockito.verify(lines, never()).save(any(Line.class));
	}
}
