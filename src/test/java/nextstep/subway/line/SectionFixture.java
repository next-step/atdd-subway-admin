package nextstep.subway.line;

import static nextstep.subway.station.StationFixture.*;

import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionAddRequest;

public class SectionFixture {
	public static Section 강남역_역삼역_구간() {
		return Section.of(1L, 강남역(), 역삼역(), 1);
	}

	public static Section 역삼역_선릉역_구간() {
		return Section.of(2L, 역삼역(), 선릉역(), 2);
	}

	public static Section 선릉역_삼성역_구간() {
		return Section.of(3L, 선릉역(), 삼성역(), 3);
	}

	public static SectionAddRequest 구간_등록_요청값(Long upStationId, Long downStationId, int distance) {
		return new SectionAddRequest(upStationId, downStationId, distance);
	}
}
