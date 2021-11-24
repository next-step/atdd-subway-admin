package nextstep.subway.line;

import static nextstep.subway.common.SectionAcceptanceFixture.*;
import static nextstep.subway.common.ServiceApiFixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.utils.RestApiFixture;

public class SectionDeleteAcceptanceTest extends AcceptanceTest {

	private long 강남역_ID;
	private long 판교역_ID;
	private long 광교역_ID;

	@BeforeEach
	public void setUp() {
		super.setUp();
		강남역_ID = createStationId("강남역");
		판교역_ID = createStationId("판교역");
		광교역_ID = createStationId("광교역");
	}

	@DisplayName("구간제거/상행종착역")
	@Test
	void deleteSection_upTerminal() {
		final long 신분당선_ID = createLineId("신분당선", "bg-red-600", 강남역_ID, 판교역_ID, 6);
		createSection(신분당선_ID, 판교역_ID, 광교역_ID, 4);

		assertThat(deleteSections(신분당선_ID, 강남역_ID).statusCode())
			.isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("구간제거/하행종착역")
	@Test
	void deleteSection_downTerminal() {
		final long 신분당선_ID = createLineId("신분당선", "bg-red-600", 강남역_ID, 판교역_ID, 6);
		createSection(신분당선_ID, 판교역_ID, 광교역_ID, 3);

		assertThat(deleteSections(신분당선_ID, 광교역_ID).statusCode())
			.isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("구간제거/구간사이역")
	@Test
	void deleteSection_between() {
		final long 신분당선_ID = createLineId("신분당선", "bg-red-600", 강남역_ID, 판교역_ID, 6);
		createSection(신분당선_ID, 판교역_ID, 광교역_ID, 5);

		assertThat(deleteSections(신분당선_ID, 판교역_ID).statusCode())
			.isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("구간제거/유일한 구간의 역 제거시 실패")
	@Test
	void deleteSection_the_only_1_remaining() {
		final long 신분당선_ID = createLineId("신분당선", "bg-red-600", 강남역_ID, 판교역_ID, 6);

		assertThat(deleteSections(신분당선_ID, 판교역_ID).statusCode())
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("구간제거/노선에 존재하지 않는 역 제거시 실패")
	@Test
	void deleteSection_not_found_station_in_line() {
		final long 신분당선_ID = createLineId("신분당선", "bg-red-600", 강남역_ID, 판교역_ID, 6);
		createSection(신분당선_ID, 판교역_ID, 광교역_ID, 4);
		final long 신분당선_아닌역_ID = createStationId("잠실역");

		assertThat(deleteSections(신분당선_ID, 신분당선_아닌역_ID).statusCode())
			.isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@DisplayName("구간제거/요청시 삭제하려는 역 정보 누락시 실패")
	@Test
	void deleteSection_not_found_station_in_request() {
		final long 신분당선_ID = createLineId("신분당선", "bg-red-600", 강남역_ID, 판교역_ID, 6);
		createSection(신분당선_ID, 판교역_ID, 광교역_ID, 2);

		assertThat(RestApiFixture.delete("/lines/{lineId}/sections", 신분당선_ID).statusCode())
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private void createSection(Long lineId, Long upStationId, Long downStationId, int distance) {
		postSections(lineId, sectionAddRequest(upStationId, downStationId, distance));
	}
}
