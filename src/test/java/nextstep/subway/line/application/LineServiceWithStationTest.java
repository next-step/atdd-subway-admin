package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철역을 포함한 LineService 테스트")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class LineServiceWithStationTest {

	@Autowired
	LineService lineService;

	@Autowired
	StationService stationService;

	private List<LineResponse> savedlineResponses;

	@BeforeEach
	void saveInitDataWithStations() {

		StationResponse 강남역 = stationService.saveStation(new StationRequest("강남역"));
		StationResponse 역삼역 = stationService.saveStation(new StationRequest("역삼역"));
		StationResponse 천호역 = stationService.saveStation(new StationRequest("천호역"));
		StationResponse 군자역 = stationService.saveStation(new StationRequest("군자역"));

		savedlineResponses = Arrays.asList(
			lineService.saveLine(new LineRequest("2호선", "green", 강남역.getId(), 역삼역.getId(), 10)),
			lineService.saveLine(new LineRequest("5호선", "purple", 천호역.getId(), 군자역.getId(), 15))
		);
		System.out.println("\n>> saveBeforeEach 종료\n");
	}

	@Test
	void findAllLines() {
		List<LineResponse> lineResponses = lineService.findAllLines();
		assertThat(lineResponses).map(LineResponse::getStations)
			.anySatisfy(stationResponses -> assertThat(stationResponses)
													.map(StationResponse::getName)
													.contains("강남역", "역삼역"))
			.anySatisfy(stationResponses -> assertThat(stationResponses)
													.map(StationResponse::getName)
													.contains("천호역", "군자역"));
	}

	@Test
	void findOne() {
		LineResponse 저장해둔_이호선_응답 = savedlineResponses.get(0);
		LineResponse lineResponse = lineService.findOne(저장해둔_이호선_응답.getId());

		assertThat(lineResponse.getStations())
			.map(StationResponse::getName)
			.contains("강남역", "역삼역");
	}
}