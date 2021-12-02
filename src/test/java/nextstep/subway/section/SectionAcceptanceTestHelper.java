package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTestHelper.*;
import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public class SectionAcceptanceTestHelper {
    private SectionAcceptanceTestHelper() {
    }

    public static ExtractableResponse<Response> 지하철_노선에_구간_등록_요청(long lineId, Map<String, String> params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/" + lineId + "/sections")
            .then().log().all().extract();

    }

    public static void 지하철_노선에_구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        assertThat(response.header("Location"))
            .isNotBlank();
    }

    public static void 지하철_노선에_지하철역_포함됨(long lineId, Station station) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        LineResponse lineResponse = response.as(LineResponse.class);

        assertThat(lineResponse.getStations()).contains(StationResponse.from(station));
    }

    public static void 지하철_노선에_포함된_역들이_일치함(long lineId, List<Station> stations) {
        List<StationResponse> expected = stations.stream()
            .map(StationResponse::from)
            .collect(Collectors.toList());

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        LineResponse lineResponse = response.as(LineResponse.class);

        assertThat(lineResponse.getStations()).isEqualTo(expected);
    }

    public static void 지하철_노선에_구간_추가_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선에_구간_등록되어_있음(long lineId, Station upStation, Station downStation, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(upStation.getId()));
        params.put("downStationId", String.valueOf(downStation.getId()));
        params.put("distance", distance);

        지하철_노선에_구간_등록_요청(lineId, params);
    }

    public static ExtractableResponse<Response> 지하철_노선예_구간_삭제_요청(long lineId, long stationId) {
        return RestAssured
            .given().log().all()
            .when()
            .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
            .then().log().all()
            .extract();
    }

    public static void 지하철_노선에_구간_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.NO_CONTENT.value());
    }


}
