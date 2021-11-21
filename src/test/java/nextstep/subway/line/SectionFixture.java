package nextstep.subway.line;

import nextstep.subway.line.dto.SectionAddRequest;

public class SectionFixture {
	public static SectionAddRequest 구간_등록_요청값(Long upStationId, Long downStationId, int distance) {
		return new SectionAddRequest(upStationId, downStationId, distance);
	}
}
