package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선의 섹션 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	StationResponse 강남역;

	StationResponse 광교역;

	StationResponse B역;

	LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();

		// given
		강남역 = postStation(new StationRequest("강남역"));
		광교역 = postStation(new StationRequest("광교역"));
		B역 = postStation(new StationRequest("B역"));

		LineRequest createParams = new LineRequest(
			"신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 7
		);

		신분당선 = postLine(createParams);
	}

	@Test
	@DisplayName("역_사이에_새로운_역을_등록할_경우")
	void 역_사이에_새로운_역을_등록할_경우() {
		// when
		// 지하철_노선에_지하철역_등록_요청
		postSection(신분당선, new SectionRequest(강남역.getId(), B역.getId(), 4));

		LineResponse line = getLine(신분당선.getId());

		// then
		// 섹션 검증
		assertThat(line.getSections())
			.extracting(s -> s.getUpStation().getName(), SectionResponse::getDistance, s -> s.getDownStation().getName())
			.containsExactly(
				tuple("강남역", 4, "B역"),
				tuple("B역", 3, "광교역")
			);
	}

	@Test
	@DisplayName("새로운_역을_상행_종점으로_등록할_경우")
	void 새로운_역을_상행_종점으로_등록할_경우() {
		// when
		// 지하철_노선에_지하철역_등록_요청
		postSection(신분당선, new SectionRequest(B역.getId(), 강남역.getId(), 4));

		LineResponse line = getLine(신분당선.getId());

		// then
		// 섹션 검증
		assertThat(line.getSections())
			.extracting(s -> s.getUpStation().getName(), SectionResponse::getDistance, s -> s.getDownStation().getName())
			.containsExactly(
				tuple("B역", 4, "강남역"),
				tuple("강남역", 7, "광교역")
			);
	}

	@Test
	@DisplayName("새로운_역을_하행_종점으로_등록할_경우")
	void 새로운_역을_하행_종점으로_등록할_경우() {
		// when
		// 지하철_노선에_지하철역_등록_요청
		postSection(신분당선, new SectionRequest(광교역.getId(), B역.getId(), 4));

		LineResponse line = getLine(신분당선.getId());

		// then
		// 섹션 검증
		assertThat(line.getSections())
			.extracting(s -> s.getUpStation().getName(), SectionResponse::getDistance, s -> s.getDownStation().getName())
			.containsExactly(
				tuple("강남역", 7, "광교역"),
				tuple("광교역", 4, "B역")
			);
	}
}
