package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
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

	private StationResponse 교대역_응답;
	private StationResponse 강남역_응답;
	private StationResponse 역삼역_응답;
	private StationResponse 선릉역_응답;
	private StationResponse 삼성역_응답;
	private StationResponse 잠실역_응답;
	private StationResponse 천호역_응답;
	private StationResponse 군자역_응답;
	private LineResponse 이호선_응답;
	private LineResponse 오호선_응답;

	@BeforeEach
	void saveInitDataWithStations() {

		교대역_응답 = stationService.saveStation(new StationRequest("교대역"));
		강남역_응답 = stationService.saveStation(new StationRequest("강남역"));
		역삼역_응답 = stationService.saveStation(new StationRequest("역삼역"));
		선릉역_응답 = stationService.saveStation(new StationRequest("선릉역"));
		삼성역_응답 = stationService.saveStation(new StationRequest("삼성역"));
		잠실역_응답 = stationService.saveStation(new StationRequest("잠실역"));
		천호역_응답 = stationService.saveStation(new StationRequest("천호역"));
		군자역_응답 = stationService.saveStation(new StationRequest("군자역"));

		이호선_응답 = lineService.saveLine(new LineRequest("2호선", "green", 강남역_응답.getId(), 삼성역_응답.getId(), 10));
		오호선_응답 = lineService.saveLine(new LineRequest("5호선", "purple", 천호역_응답.getId(), 군자역_응답.getId(), 15));

		System.out.println("\n>> saveBeforeEach 종료\n");
	}

	@Test
	void findAllLines() {
		List<LineResponse> lineResponses = lineService.findAllLines();
		assertThat(lineResponses).map(LineResponse::getStations)
			.anySatisfy(stationResponses -> assertThat(stationResponses)
				.map(StationResponse::getName)
				.contains("강남역", "삼성역"))
			.anySatisfy(stationResponses -> assertThat(stationResponses)
				.map(StationResponse::getName)
				.contains("천호역", "군자역"));
	}

	@Test
	void findOne() {
		LineResponse lineResponse = lineService.findOne(이호선_응답.getId());

		assertThat(lineResponse.getStations())
			.map(StationResponse::getName)
			.contains("강남역", "삼성역");
	}

	@DisplayName("Section 저장 테스트 (happy path)")
	@Test
	void saveSection_shouldSuccess1() {
		lineService.saveSection(이호선_응답.getId(), new SectionRequest(강남역_응답.getId(), 역삼역_응답.getId(), 5));

		assertThat(lineService.findOne(이호선_응답.getId()).getStations())
			.map(StationResponse::getName)
			.contains("강남역", "역삼역", "삼성역");

		lineService.saveSection(이호선_응답.getId(), new SectionRequest(역삼역_응답.getId(), 선릉역_응답.getId(), 4));

		assertThat(lineService.findOne(이호선_응답.getId()).getStations())
			.map(StationResponse::getName)
			.contains("강남역", "역삼역", "선릉역", "삼성역");
	}

	@DisplayName("Section 저장 테스트 (happy path : 상행 확장)")
	@Test
	void saveSection_shouldSuccess2() {
		lineService.saveSection(이호선_응답.getId(), new SectionRequest(교대역_응답.getId(), 강남역_응답.getId(), 100));

		assertThat(lineService.findOne(이호선_응답.getId()).getStations())
			.map(StationResponse::getName)
			.contains("교대역", "강남역", "삼성역");

	}

	@DisplayName("Section 저장 테스트 (happy path : 하행 확장)")
	@Test
	void saveSection_shouldSuccess3() {
		lineService.saveSection(이호선_응답.getId(), new SectionRequest(삼성역_응답.getId(), 잠실역_응답.getId(), 100));

		assertThat(lineService.findOne(이호선_응답.getId()).getStations())
			.map(StationResponse::getName)
			.contains("강남역", "삼성역", "잠실역");
	}

	@DisplayName("Section 저장 테스트 (예외 케이스 : 기존 역 사이 길이와 같다)")
	@Test
	void saveSection_shouldException1() {
		lineService.saveSection(이호선_응답.getId(), new SectionRequest(강남역_응답.getId(), 역삼역_응답.getId(), 4));

		assertThat(lineService.findOne(이호선_응답.getId()).getStations())
			.map(StationResponse::getName)
			.contains("강남역", "역삼역", "삼성역");

		assertThatThrownBy(() -> lineService.saveSection(이호선_응답.getId(),
			new SectionRequest(역삼역_응답.getId(), 선릉역_응답.getId(), 6)))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("Section 저장 테스트 (예외 케이스 : 기존 역 사이 길이보다 크다)")
	@Test
	void saveSection_shouldException2() {
		lineService.saveSection(이호선_응답.getId(), new SectionRequest(강남역_응답.getId(), 역삼역_응답.getId(), 4));

		assertThat(lineService.findOne(이호선_응답.getId()).getStations())
			.map(StationResponse::getName)
			.contains("강남역", "역삼역", "삼성역");

		assertThatThrownBy(() -> lineService.saveSection(이호선_응답.getId(),
			new SectionRequest(역삼역_응답.getId(), 선릉역_응답.getId(), 7)))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("Section 저장 테스트 (예외 케이스 : 이미 노선에 모두 등록)")
	@Test
	void saveSection_shouldException3() {
		assertThatThrownBy(() -> lineService.saveSection(이호선_응답.getId(),
			new SectionRequest(강남역_응답.getId(), 삼성역_응답.getId(), 7)))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("Section 저장 테스트 (예외 케이스 : 상행역과 하행역 둘 중 하나도 포함되어있지 않음)")
	@Test
	void saveSection_shouldException4() {
		assertThatThrownBy(() -> lineService.saveSection(이호선_응답.getId(),
			new SectionRequest(교대역_응답.getId(), 잠실역_응답.getId(), 7)))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("Section 저장 테스트 (예외 케이스 : 상행역과 하행역이 같음)")
	@Test
	void saveSection_shouldException5() {
		assertThatThrownBy(() -> lineService.saveSection(이호선_응답.getId(),
			new SectionRequest(강남역_응답.getId(), 강남역_응답.getId(), 7)))
			.isInstanceOf(IllegalArgumentException.class);
	}
}