package nextstep.subway.line;

import static nextstep.subway.common.ServiceApiFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 내 구간 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	private long 강남역_ID;
	private long 판교역_ID;
	private long 광교역_ID;

	@BeforeEach
	public void setUp() {
		super.setUp();

		강남역_ID = postStations("강남역").as(StationResponse.class).getId();
		판교역_ID = postStations("판교역").as(StationResponse.class).getId();
		광교역_ID = postStations("광교역").as(StationResponse.class).getId();
	}

	@DisplayName("구간추가/구간사이")
	@Test
	void createSection_between() {
		final long 신분당선_ID = createLineId("신분당선", "bg-red-600", 강남역_ID, 광교역_ID, 10);

		final ExtractableResponse<Response> response = postSections(
			신분당선_ID, sectionAddRequest(판교역_ID, 광교역_ID, 4)
		);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();

		final SectionResponse sectionResponse = response.as(SectionResponse.class);
		assertAll(
			() -> assertThat(sectionResponse.getId()).isNotNull(),
			() -> assertThat(sectionResponse.getUpStation().getId()).isEqualTo(판교역_ID),
			() -> assertThat(sectionResponse.getDownStation().getId()).isEqualTo(광교역_ID),
			() -> assertThat(sectionResponse.getDistance()).isEqualTo(4),
			() -> assertThat(sectionResponse.getCreatedDate()).isNotNull(),
			() -> assertThat(sectionResponse.getModifiedDate()).isNotNull()
		);
	}

	@DisplayName("구간추가/구간사이/기존구간 길이보다 크거나 같을 경우 실패")
	@Test
	void createSection_betweenStations_tooLongDistance() {
		final long 신분당선_ID = createLineId("신분당선", "bg-red-600", 강남역_ID, 광교역_ID, 10);
		assertThat(postSections(신분당선_ID, sectionAddRequest(판교역_ID, 광교역_ID, 10)).statusCode()).isEqualTo(
			HttpStatus.BAD_REQUEST.value());
		assertThat(postSections(신분당선_ID, sectionAddRequest(판교역_ID, 광교역_ID, 20)).statusCode()).isEqualTo(
			HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("구간추가/상행종착")
	@Test
	void createSection_ok_upTerminal() {
	}

	@DisplayName("구간추가/하행종착")
	@Test
	void createSection_ok_downTerminal() {
	}

	@DisplayName("구간추가/중복이면 실패")
	@Test
	void createSection_fail_duplicated() {
	}

	@DisplayName("구간 추가/노선에 포함되지 않은 역으로만 요청한 경우 실패")
	@Test
	void createSection_fail_notFoundStations() {
	}

	private long createLineId(String name, String color, Long upStationId, Long downStationId, int distance) {
		return postLines(lineAddRequest(name, color, upStationId, downStationId, distance))
			.as(LineResponse.class).getId();
	}
}
