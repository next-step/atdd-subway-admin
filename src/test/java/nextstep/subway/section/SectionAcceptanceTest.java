package nextstep.subway.section;

import static nextstep.subway.utils.CommonSettings.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Long newStationId;
    private Long newStationId2;
    private Long newStationId3;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        //given
        //지하철 역이 등록되어 있다.
        ExtractableResponse<Response> createdStationResponse1 = 생성_요청(new StationRequest("강남역"), "/stations");
        ExtractableResponse<Response> createdStationResponse2 = 생성_요청(new StationRequest("역삼역"), "/stations");
        ExtractableResponse<Response> createdStationResponse3 = 생성_요청(new StationRequest("판교역"), "/stations");
        ExtractableResponse<Response> createdStationResponse4 = 생성_요청(new StationRequest("은계역"), "/stations");
        ExtractableResponse<Response> createdStationResponse5 = 생성_요청(new StationRequest("사랑역"), "/stations");

        upStationId = createdStationResponse1.as(StationResponse.class).getId();
        downStationId = createdStationResponse2.as(StationResponse.class).getId();
        newStationId = createdStationResponse3.as(StationResponse.class).getId();
        newStationId2 = createdStationResponse4.as(StationResponse.class).getId();
        newStationId3 = createdStationResponse5.as(StationResponse.class).getId();

        //지하철 line이 등록되어 있다.
        ExtractableResponse<Response> createdLineResponse = 생성_요청(
            new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 20), "/lines");

        lineId = createdLineResponse.as(LineResponse.class).getId();
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 - 상행역 + 새로운 하행역이 추가될 때")
    void addSectionUp() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        생성_요청(new SectionRequest(upStationId, newStationId, 10), String.format("/lines/%d/sections", lineId));

        ExtractableResponse<Response> createdSectionResponse = 생성_요청(
            new SectionRequest(newStationId, newStationId2, 5),
            String.format("/lines/%d/sections", lineId));
        // 지하철 목록 조회
        ExtractableResponse<Response> existsLineList = 조회_요청("/lines/" + lineId);

        // then
        // 지하철_노선에_지하철역_등록됨
        List<Long> stationIds = 지하철역_ID_추출(createdSectionResponse);
        // 지하철_노선_목록_포함됨
        List<Long> resultLineIds = LineAcceptanceTest.찾은_노선에서_Sations_ID추출(existsLineList);

        assertThat(createdSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stationIds).containsExactly(upStationId, newStationId, newStationId2, downStationId);
        assertThat(stationIds).containsExactlyElementsOf(resultLineIds);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 - 하행역 + 새로운 상행역이 추가될 때")
    void addSectionDown() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> createdSectionResponse = 생성_요청(
            new SectionRequest(newStationId, downStationId, 10),
            String.format("/lines/%d/sections", lineId));

        // then
        // 지하철_노선에_지하철역_등록됨
        List<Long> stationIds = 지하철역_ID_추출(createdSectionResponse);
        assertThat(createdSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stationIds).containsExactly(upStationId, newStationId, downStationId);
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    void addSectionBefore() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> createdSectionResponse = 생성_요청(
            new SectionRequest(newStationId, upStationId, 10),
            String.format("/lines/%d/sections", lineId));

        // then
        // 지하철_노선에_지하철역_등록됨
        List<Long> stationIds = 지하철역_ID_추출(createdSectionResponse);
        assertThat(createdSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stationIds).containsExactly(newStationId, upStationId, downStationId);
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    void addSectionAfter() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> createdSectionResponse = 생성_요청(
            new SectionRequest(downStationId, newStationId, 10),
            String.format("/lines/%d/sections", lineId));

        // then
        // 지하철_노선에_지하철역_등록됨
        List<Long> stationIds = 지하철역_ID_추출(createdSectionResponse);
        assertThat(createdSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stationIds).containsExactly(upStationId, downStationId, newStationId);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    void errorOverDistance() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> createdSectionResponse = 생성_요청(
            new SectionRequest(upStationId, newStationId, 20),
            String.format("/lines/%d/sections", lineId));

        // then
        // 에러가 발생한다
        assertThat(createdSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    void overridingStation() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> createdSectionResponse = 생성_요청(
            new SectionRequest(upStationId, downStationId, 5),
            String.format("/lines/%d/sections", lineId));

        // then
        // 에러가 발생한다
        assertThat(createdSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    void notExist() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> createdSectionResponse = 생성_요청(
            new SectionRequest(newStationId, newStationId2, 5),
            String.format("/lines/%d/sections", lineId));
        // then
        // 에러가 발생한다
        assertThat(createdSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("up - new3 - new2 - new -down 중  new2 > down > new3 삭제 테스트")
    void remove() {
        //given
        // 구간이 등록되어 있다.
        생성_요청(new SectionRequest(upStationId, newStationId, 19), String.format("/lines/%d/sections", lineId));
        생성_요청(new SectionRequest(upStationId, newStationId2, 15), String.format("/lines/%d/sections", lineId));
        생성_요청(new SectionRequest(upStationId, newStationId3, 5), String.format("/lines/%d/sections", lineId));

        removeNew2();
        removeDown();
        removeNew();
    }

    @Test
    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 때 제거할 수 없다.")
    void removeOnlyOne() {
        //when
        //지하철 삭제요청을 보낸다
        ExtractableResponse<Response> removedSectionResponse = 역_삭제_요청(
            String.format("/lines/%d/sections", lineId), upStationId);

        //then
        //삭제가 불가능하다.
        assertThat(removedSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("없는 구간은 제거할 수없다.")
    void removeNoSection() {
        //when
        //지하철 삭제요청을 보낸다
        ExtractableResponse<Response> removedSectionResponse = 역_삭제_요청(
            String.format("/lines/%d/sections", lineId), newStationId);

        //then
        //삭제가 불가능하다.
        assertThat(removedSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private List<Long> 지하철역_ID_추출(ExtractableResponse<Response> createdSectionResponse) {
        return createdSectionResponse.as(LineResponse.class).getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
    }

    private void removeNew() {
        //when
        //지하철 삭제요청을 보낸다
        ExtractableResponse<Response> removedSectionResponse = 역_삭제_요청(
            String.format("/lines/%d/sections", lineId), newStationId);

        //then
        // 정상적으로 삭제가 된다
        assertThat(removedSectionResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(LineAcceptanceTest.찾은_노선에서_Sations_ID추출(조회_요청("/lines/" + lineId))).containsExactly(upStationId,
            newStationId3);
    }

    private void removeDown() {
        //when
        //지하철 삭제요청을 보낸다
        ExtractableResponse<Response> removedSectionResponse = 역_삭제_요청(
            String.format("/lines/%d/sections", lineId), downStationId);

        //then
        // 정상적으로 삭제가 된다
        assertThat(removedSectionResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(LineAcceptanceTest.찾은_노선에서_Sations_ID추출(조회_요청("/lines/" + lineId))).containsExactly(upStationId,
            newStationId3, newStationId);
    }

    private void removeNew2() {
        //when
        //지하철 삭제요청을 보낸다
        ExtractableResponse<Response> removedSectionResponse = 역_삭제_요청(
            String.format("/lines/%d/sections", lineId), newStationId2);

        //then
        // 정상적으로 삭제가 된다
        assertThat(removedSectionResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(LineAcceptanceTest.찾은_노선에서_Sations_ID추출(조회_요청("/lines/" + lineId))).containsExactly(upStationId,
            newStationId3, newStationId, downStationId);
    }

    public static ExtractableResponse<Response> 역_삭제_요청(String path, Long stationId) {
        return RestAssured.given().log().all()
            .queryParam("stationId", stationId)
            .when()
            .delete(path)
            .then().log().all()
            .extract();
    }

}
