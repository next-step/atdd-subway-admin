package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.step.LineAcceptanceStep.*;
import static nextstep.subway.line.step.SectionAcceptanceStep.새로운_지하철_구간_추가_요청;
import static nextstep.subway.line.step.SectionAcceptanceStep.지하철_구간내_역_삭제됨;
import static nextstep.subway.station.step.StationAcceptanceStep.지하철역_생성됨;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionDeleteAcceptanceTest extends AcceptanceTest {
    @DisplayName("시나리오1: 존재하는 지하철 노선의 종점역이 아닌 역을 구간에서 삭제한다.")
    @Test
    void deleteNormalStationInSectionTest() {
        String station1 = "강남";
        String station2 = "역삼";
        String station3 = "선릉";
        String lineName = "2호선";
        String lineColor = "녹색";
        // given
        // 지하철 역이 생성되어 있다.
        ExtractableResponse<Response> station1Response = 지하철역_생성됨(new StationRequest(station1));
        Long station1Id = 응답_헤더에서_ID_추출(station1Response);
        ExtractableResponse<Response> station2Response = 지하철역_생성됨(new StationRequest(station2));
        Long station2Id = 응답_헤더에서_ID_추출(station2Response);
        ExtractableResponse<Response> station3Response = 지하철역_생성됨(new StationRequest(station3));
        Long station3Id = 응답_헤더에서_ID_추출(station3Response);
        // and 지하철 노선에 구간이 등록되어 있다.
        ExtractableResponse<Response> lineResponse = 새로운_지하철_노선_생성_요청(
                new LineRequest(lineName, lineColor, station1Id, station2Id, 100L));
        Long lineId = 응답_헤더에서_ID_추출(lineResponse);
        새로운_지하철_구간_추가_요청(station2Id, station3Id, 50L, lineId);

        // when
        // 사용자가 종점이 아닌 구간의 지하철역 삭제 요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + station2Id)
                .then()
                .log().all()
                .extract();

        // 지하철 역 삭제 성공
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        지하철_구간내_역_삭제됨(lineId, station2Id, 2);
    }

    @DisplayName("시나리오2: 존재하는 지하철 역의 종점역을 삭제한다.")
    @Test
    void deleteEndStationTest() {
        String station1 = "강남";
        String station2 = "역삼";
        String station3 = "선릉";
        String lineName = "2호선";
        String lineColor = "녹색";
        // given
        // 지하철 역이 생성되어 있다.
        ExtractableResponse<Response> station1Response = 지하철역_생성됨(new StationRequest(station1));
        Long station1Id = 응답_헤더에서_ID_추출(station1Response);
        ExtractableResponse<Response> station2Response = 지하철역_생성됨(new StationRequest(station2));
        Long station2Id = 응답_헤더에서_ID_추출(station2Response);
        ExtractableResponse<Response> station3Response = 지하철역_생성됨(new StationRequest(station3));
        Long station3Id = 응답_헤더에서_ID_추출(station3Response);
        // and 지하철 노선에 구간이 등록되어 있다.
        ExtractableResponse<Response> lineResponse = 새로운_지하철_노선_생성_요청(
                new LineRequest(lineName, lineColor, station1Id, station2Id, 100L));
        Long lineId = 응답_헤더에서_ID_추출(lineResponse);
        새로운_지하철_구간_추가_요청(station2Id, station3Id, 50L, lineId);

        // when
        // 사용자가 종점이 아닌 구간의 지하철역 삭제 요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + station1Id)
                .then()
                .log().all()
                .extract();

        // 지하철 역 삭제 성공
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        지하철_구간내_역_삭제됨(lineId, station1Id, 2);
    }

    @DisplayName("시나리오3: 지하철 노선에 등록되지 않은 지하철 역을 삭제 요청")
    @Test
    void deleteEndStationFailWheNotExistStationTest() {
        String station1 = "강남";
        String station2 = "역삼";
        String station3 = "선릉";
        Long notInSectionStationId = 4L;
        String lineName = "2호선";
        String lineColor = "녹색";
        // given
        // 지하철 역이 생성되어 있다.
        ExtractableResponse<Response> station1Response = 지하철역_생성됨(new StationRequest(station1));
        Long station1Id = 응답_헤더에서_ID_추출(station1Response);
        ExtractableResponse<Response> station2Response = 지하철역_생성됨(new StationRequest(station2));
        Long station2Id = 응답_헤더에서_ID_추출(station2Response);
        ExtractableResponse<Response> station3Response = 지하철역_생성됨(new StationRequest(station3));
        Long station3Id = 응답_헤더에서_ID_추출(station3Response);
        // and 지하철 노선에 구간이 등록되어 있다.
        ExtractableResponse<Response> lineResponse = 새로운_지하철_노선_생성_요청(
                new LineRequest(lineName, lineColor, station1Id, station2Id, 100L));
        Long lineId = 응답_헤더에서_ID_추출(lineResponse);
        새로운_지하철_구간_추가_요청(station2Id, station3Id, 50L, lineId);

        // when
        // 사용자가 구간에 등록되지 않은 지하철역을 삭제 요청함
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + notInSectionStationId)
                .then()
                .log().all()
                .extract();

        // 지하철 역 삭제 실패”
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
