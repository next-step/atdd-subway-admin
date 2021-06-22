package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private long 서울역;
    private long 안산역;
    private long 사당역;
    private long 의정부역;
    private long 오이도역;
    private String 사호선;

    @BeforeEach
    public void setup() {
        서울역 = getStationId("서울역");
        안산역 = getStationId("안산역");
        사당역 = getStationId("사당역");
        의정부역 = getStationId("의정부역");
        오이도역 = getStationId("오이도역");
        사호선 = 지하철_노선_등록되어_있음(new LineRequest("4호선", "blue", 서울역, 안산역, 10)).split("/")[2];
    }

    @DisplayName("상행 역 다음 위치에 지하철 구간을 추가한다. - 상행 역 기준 추가")
    @Test
    void addSectionUpStationAfter() {
        //given
        SectionRequest sectionRequest = new SectionRequest(서울역, 사당역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가됨(response);
        지하철_노선에_구간_순서_정렬됨(Arrays.asList(서울역, 사당역, 안산역), 사호선);
    }

    @DisplayName("새로운 상행 역 종점으로 시작되는 지하철 구간을 추가한다.")
    @Test
    void addSectionUpStationBefore() {
        //given
        SectionRequest sectionRequest = new SectionRequest(의정부역, 서울역, 50);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가됨(response);
        지하철_노선에_구간_순서_정렬됨(Arrays.asList(의정부역, 서울역, 안산역), 사호선);
    }

    @DisplayName("하행 역 이전 위치에 지하철 구간을 추가한다. - 하행 역 기준")
    @Test
    void addSectionDownStationBefore() {
        //given
        SectionRequest sectionRequest = new SectionRequest(사당역, 안산역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가됨(response);
        지하철_노선에_구간_순서_정렬됨(Arrays.asList(서울역, 사당역, 안산역), 사호선);
    }

    @DisplayName("하행 역 다음 위치에 새로운 지하철 종점 구간을 추가한다.")
    @Test
    void addSectionDownStationAfter() {
        //given
        SectionRequest sectionRequest = new SectionRequest(안산역, 오이도역, 50);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가됨(response);
        지하철_노선에_구간_순서_정렬됨(Arrays.asList(서울역, 안산역, 오이도역), 사호선);
    }

    @DisplayName("지하철 구간을 추가 실패 - 이미 등록 된 구간 추가.")
    @Test
    void alreadyAddedSectionAddFail() {
        //given
        SectionRequest sectionRequest = new SectionRequest(서울역, 안산역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가_실패(response);
    }

    @DisplayName("지하철 구간을 추가 실패 - 추가 하려는 구간이 크거나 같을 경우")
    @Test
    void longDistanceSectionAddFail() {
        //given
        SectionRequest sectionRequest = new SectionRequest(의정부역, 오이도역, 100);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가_실패(response);
    }

    @DisplayName("지하철 구간을 추가 실패 - 추가되지 않은 두 역을 새로운 구간으로 추가 할 경우")
    @Test
    void notExistsSectionAddFail() {
        //given
        SectionRequest sectionRequest = new SectionRequest(서울역, 사당역, 10);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가_실패(response);
    }

    @DisplayName("시작 구간을 삭제한다.")
    @Test
    void deleteSectionFirstSection() {
        //given
        지하철_구간_추가되어_있음(안산역, 오이도역, 50);

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(사호선, Long.toString(서울역));

        //then
        지하철_구간_삭제됨(response);
        지하철_노선에_구간_순서_정렬됨(Arrays.asList(안산역, 오이도역), 사호선);
    }

    @DisplayName("구간과 구간 사이의 구간을 삭제한다.")
    @Test
    void deleteSectionUpStationAfter() {
        //given
        String 안산역 = 지하철_구간_추가되어_있음(this.안산역, 오이도역, 50).body().jsonPath().getString("upStationId");

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(사호선, 안산역);

        //then
        지하철_구간_삭제됨(response);
        지하철_노선에_구간_순서_정렬됨(Arrays.asList(서울역, 오이도역), 사호선);
    }

    @DisplayName("마지막 구간을 삭제한다.")
    @Test
    void deleteSectionLastSection() {
        //given
        지하철_구간_추가되어_있음(안산역, 오이도역, 50);

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(사호선, Long.toString(오이도역));

        //then
        지하철_구간_삭제됨(response);
        지하철_노선에_구간_순서_정렬됨(Arrays.asList(서울역, 안산역), 사호선);
    }

    @DisplayName("구간을 삭제 실패 - 기존에 없는 구간 삭제 할 경우")
    @Test
    void deleteNotExistsSection() {
        //given
        지하철_구간_추가되어_있음(안산역, 오이도역, 50);

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(사호선, Long.toString(사당역));

        //then
        지하철_구간_삭제_실패(response);
    }

    @DisplayName("구간을 삭제 실패 - 기존에 구간이 하나일 때 상행역을 삭제 할 경우")
    @Test
    void deleteSectionUpStationFailByOneSection() {
        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(사호선, Long.toString(서울역));

        //then
        지하철_구간_삭제_실패(response);
    }

    @DisplayName("구간을 삭제 실패 - 기존에 구간이 하나일 때 하행역을 삭제 할 경우")
    @Test
    void deleteSectionDownStationFailByOneSection() {
        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(사호선, Long.toString(안산역));

        //then
        지하철_구간_삭제_실패(response);
    }


    public static ExtractableResponse<Response> 지하철_구간_추가(String lineId, SectionRequest section) {
        return RestAssured.given().log().all()
                .body(section)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_삭제(String lineId, String stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();
    }

    private void 지하철_구간_추가됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_구간_추가_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_구간_추가되어_있음(long upStation, long downStation, int distance) {
        SectionRequest sectionRequest = new SectionRequest(upStation, downStation, distance);
        return 지하철_구간_추가(사호선, sectionRequest);
    }

    private void 지하철_구간_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선에_구간_순서_정렬됨(List<Long> stationIds, String lineId) {
        ExtractableResponse<Response> response = 지하철_노선_조회("/lines/" + lineId);
        List<Long> resultStationIds = response.jsonPath().getList("stations", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsExactlyElementsOf(stationIds);
    }

    private void 지하철_구간_삭제_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
