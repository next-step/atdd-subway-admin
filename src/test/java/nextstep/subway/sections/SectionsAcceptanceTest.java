package nextstep.subway.sections;


import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.util.UrlConstants;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionsAcceptanceTest extends AcceptanceTest {

	@Test
	void 지하철_역을_상행선에서_하행선_순서로_가져오기() {
		// when
		// 노선 생성
		Line line = new Line("8호선", "bg-green-600");

		// 지하철 역 생성
		Station station1 = new Station("송파역");
		Station station2 = new Station("석촌역");
		Station station3 = new Station("잠실역");
		Station station4 = new Station("몽촌토성역");
		Station station5 = new Station("강동구청역");
		int distance = 10;

		// Section 생성
		Sections sections = new Sections();
		sections.addSection(line, station1, station2, distance);
		sections.addSection(line, station2, station3, distance);
		sections.addSection(line, station3, station4, distance);
		sections.addSection(line, station4, station5, distance);

		// 지하철_노선_생성_요청
		List<StationResponse> stations = sections.getStations();
		assertAll(
				() -> 두_지하철_역의_이름이_같은지_확인(stations.get(0).getName(), station1.getName()),
				() -> 두_지하철_역의_이름이_같은지_확인(stations.get(1).getName(), station2.getName()),
				() -> 두_지하철_역의_이름이_같은지_확인(stations.get(2).getName(), station3.getName()),
				() -> 두_지하철_역의_이름이_같은지_확인(stations.get(3).getName(), station4.getName()),
				() -> 두_지하철_역의_이름이_같은지_확인(stations.get(4).getName(), station5.getName())
		);
	}

	private void 두_지하철_역의_이름이_같은지_확인(String actual, String expected) {
		assertThat(actual).isEqualTo(expected);
	}

	private LineRequest 지하철_노선요청_객체_가져오기(String name, String color, Long upstreamId, Long donwStreamId, int distance) {
		return new LineRequest(name, color, upstreamId, donwStreamId, distance);
	}

	private ExtractableResponse<Response> 지하철_노선_생성요청(LineRequest line) {
		return RestAssured.given().log().all()
				.body(line)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post(UrlConstants.LINE_DEFAULT_URL)
				.then().log().all()
				.extract();
	}

	private ExtractableResponse<Response> 지하철_역_생성요청(String stationName) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("name", stationName);
		return RestAssured.given().log().all()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post(UrlConstants.STATION_DEFAULT_URL)
				.then().log().all()
				.extract();
	}

	private StationResponse 지하철_역_가져오기(ExtractableResponse<Response> response) {
		return response.jsonPath().getObject(".", StationResponse.class);
	}
}
