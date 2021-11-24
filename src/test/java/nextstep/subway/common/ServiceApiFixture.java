package nextstep.subway.common;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineAddRequest;
import nextstep.subway.line.dto.LineEditRequest;
import nextstep.subway.line.dto.SectionAddRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.utils.RestApiFixture;

public class ServiceApiFixture {

	public static SectionAddRequest sectionAddRequest(Long upStationId, Long downStationId, int distance) {
		return new SectionAddRequest(upStationId, downStationId, distance);
	}

	public static ExtractableResponse<Response> postSections(Long lineId, SectionAddRequest sectionAddRequest) {
		return RestApiFixture.post(sectionAddRequest, "/lines/{id}/sections", lineId);
	}

	public static ExtractableResponse<Response> deleteSections(Long lineId, Long stationId) {
		return RestApiFixture.delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId);
	}

	public static LineAddRequest lineAddRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
		return new LineAddRequest(name, color, upStationId, downStationId, distance);
	}

	public static LineEditRequest lineEditRequest(String name, String color) {
		return new LineEditRequest(name, color);
	}

	public static ExtractableResponse<Response> postLines(LineAddRequest lineAddRequest) {
		return RestApiFixture.post(lineAddRequest, "/lines");
	}

	public static ExtractableResponse<Response> postStations(String name) {
		return RestApiFixture.post(new StationRequest(name), "/stations");
	}
}
