package nextstep.subway.controller;

import static nextstep.subway.controller.LineAcceptanceTest.노선_정보;
import static nextstep.subway.util.AcceptanceFixture.결과에_존재한다;
import static nextstep.subway.util.AcceptanceFixture.목록_아이디_조회;
import static nextstep.subway.util.AcceptanceFixture.목록_이름_조회;
import static nextstep.subway.util.AcceptanceFixture.아이디_조회;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.dto.ErrorResponse;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.util.AcceptanceTest;
import nextstep.subway.value.ErrMsg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    //상행 -> 하행
    private Long 강남역ID;
    private Long 미금역ID;
    private Long 정자역ID;
    private Long 광교역ID;

    private Long 신분당선ID;
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역ID = 아이디_조회(StationAcceptanceTest.지하철_역_생성("강남역"));
        미금역ID = 아이디_조회(StationAcceptanceTest.지하철_역_생성("미금역"));
        정자역ID = 아이디_조회(StationAcceptanceTest.지하철_역_생성("정자역"));
        광교역ID = 아이디_조회(StationAcceptanceTest.지하철_역_생성("광교역"));
        신분당선ID =아이디_조회(LineAcceptanceTest.지하철_노선_생성(노선_정보("신분당선", "빨간색")));

    }

    /**
     * Given 노선에 구간이 등록되어 있지 않을때
     * When 강남역-광교역 구간을 등록하면
     * Then 구간이 등록되고, 지하철역(신분당선) 목록 조회시 강남역과 광교역을 찾을 수 있다.
     */
    @DisplayName("구간을 최초 생성한다.")
    @Test
    void createFirstSection() {
        Map 구간_정보 = 구간_정보_맵(강남역ID, 광교역ID, 10);
        assertTrue(목록_아이디_조회(지하철_구간_목록_조회(신분당선ID)).isEmpty());

        ExtractableResponse<Response> 생성_결과 = 지하철_구간_생성(신분당선ID, 구간_정보);
        ExtractableResponse<Response> 역_조회_결과 = 지하철_역_목록_조회(신분당선ID);

        assertAll(
                ()->assertThat(생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                ()->assertTrue(결과에_존재한다(역_조회_결과, "강남역", "광교역"))
        );
    }
    /**
     * Given 노선에 구간이 등록되어 있지 않을때
     * When 강남역-광교역 구간을 등록하면
     * Then 구간이 등록되고, 지하철역(신분당선) 목록 조회시 강남역과 광교역을 찾을 수 있다.
     */
    @DisplayName("구간을 생성할때 상행역 또는 하행역이 null 이면 400 BAD_REQUEST 응답을 받는다.")
    @Test
    void createSectionNull() {
        Map 구간_정보_상행역_없음 = 구간_정보_맵(null, 광교역ID, 10);
        Map 구간_정보_하행역_없음 = 구간_정보_맵(강남역ID, null, 10);
        ExtractableResponse<Response> 생성_결과_상행역_없음 = 지하철_구간_생성(신분당선ID, 구간_정보_상행역_없음);
        ExtractableResponse<Response> 생성_결과_하행역_없음 = 지하철_구간_생성(신분당선ID, 구간_정보_하행역_없음);
        assertAll(
                ()-> assertThat(생성_결과_상행역_없음.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                ()-> assertThat(생성_결과_하행역_없음.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );
    }


    /**
     * Given 구간이 하나 등록되어 있을때
     * When 그것과 하행역(광교역)이 같으며, 거리가 더 짧은(5) 구간을 등록하면
     * Then 구간이 등록되고, 지하철역(신분당선) 목록 조회시 새로 추가된 역을 찾을 수 있으며,
     *      기존역 ID로 구간 조회시 거리가 수정되었음을 확인 할 수 있다.
     */
    @DisplayName("하행역 기준으로 중간 구간 생성")
    @Test
    void createSectionInMiddleMatchingDownStation() {
        Map 사전_구간 = 구간_정보_맵(강남역ID, 광교역ID, 10);
        지하철_구간_생성(신분당선ID, 사전_구간);
        assertThat(목록_아이디_조회(지하철_구간_목록_조회(신분당선ID))).hasSize(1);

        Map 구간_정보 = 구간_정보_맵(미금역ID, 광교역ID, 5);

        ExtractableResponse<Response> 생성_결과 = 지하철_구간_생성(신분당선ID, 구간_정보);
        ExtractableResponse<Response> 역_조회_결과 = 지하철_역_목록_조회(신분당선ID);
        SectionResponse 구간_조회_결과 = 지하철_구간_조회(신분당선ID, 아이디_조회(생성_결과)).as(SectionResponse.class);
        assertAll(
                ()->assertThat(생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                ()->assertThat(구간_조회_결과.getDistance().getValue()).isEqualTo(5),
                ()->assertTrue(결과에_존재한다(역_조회_결과, "미금역"))
        );
    }

    /**
     * Given 구간이 하나 등록되어 있을때
     * When 그것과 하행역(광교역)이 같으며, 거리가 같거나 더 긴 구간을 등록하면
     * Then 400 BAD_REQUEST 응답을 받는다.
     */
    @DisplayName("더 긴 길이의 구간을 중간에 추가하려 하면 400 BAD_REQUEST 응답을 받는다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void createSectionInMiddleDistanceException(int distance) {
        Map 사전_구간 = 구간_정보_맵(강남역ID, 광교역ID, 10);
        지하철_구간_생성(신분당선ID, 사전_구간);
        assertThat(목록_아이디_조회(지하철_구간_목록_조회(신분당선ID))).hasSize(1);

        Map 구간_정보 = 구간_정보_맵(미금역ID, 광교역ID, distance);

        ExtractableResponse<Response> 생성_결과 = 지하철_구간_생성(신분당선ID, 구간_정보);
        assertAll(
                ()-> assertThat(생성_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                ()-> assertThat(생성_결과.as(ErrorResponse.class).getMessage()).isEqualTo(ErrMsg.INAPPROPRIATE_DISTANCE)
        );
    }


    /**
     * Given 구간이 하나 등록되어 있을때
     * When 그것과 상행역(강남역)이 같으며, 거리가 더 짧은(5) 구간을 등록하면
     * Then 구간이 등록되고, 지하철역(신분당선) 목록 조회시 새로 추가된 역을 찾을 수 있다.
     */
    @DisplayName("상행역 기준으로 중간 구간 생성")
    @Test
    void createSectionInMiddleMatchingUpStation() {
        Map 사전_구간 = 구간_정보_맵(강남역ID, 광교역ID, 10);
        지하철_구간_생성(신분당선ID, 사전_구간);

        Map 구간_정보 = 구간_정보_맵(강남역ID, 미금역ID, 5);

        ExtractableResponse<Response> 생성_결과 = 지하철_구간_생성(신분당선ID, 구간_정보);
        ExtractableResponse<Response> 역_조회_결과 = 지하철_역_목록_조회(신분당선ID);
        assertAll(
                ()->assertThat(생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                ()->assertTrue(결과에_존재한다(역_조회_결과, "미금역"))
        );
    }

    /**
     * Given 구간이 하나 등록되어 있을때
     * When 새로 등록할 양수의 거리를 갖춘 구간의 상행역이, 기존 등록된 구간의 하행종점과 같으면
     * Then 구간이 등록되고, 지하철역(신분당선)의 하행종점이 바뀌었음을 확인할 수 있다.
     */
    @DisplayName("하행 종점을 생성한다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void createLastStation(int value) {
        지하철_구간_생성(신분당선ID, 구간_정보_맵(강남역ID, 미금역ID, 10));
        assertThat(하행_종점(목록_이름_조회(지하철_역_목록_조회(신분당선ID)))).isEqualTo("미금역");


        Map 신규_구간_정보 = 구간_정보_맵(미금역ID, 광교역ID, value);
        ExtractableResponse<Response> 생성_결과 = 지하철_구간_생성(신분당선ID, 신규_구간_정보);

        assertAll(
                ()->assertThat(생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                ()->assertThat(하행_종점(목록_이름_조회(지하철_역_목록_조회(신분당선ID)))).isEqualTo("광교역")
        );
    }



    /**
     * Given 구간이 하나 등록되어 있을때
     * When 새로 등록할 구간의 하행역이, 기존 등록된 구간의 상행종점과 같으면
     * Then 구간이 등록되고, 지하철역(신분당선)의 상행종점이 바뀌었음을 확인할 수 있다.
     */
    @DisplayName("상행 종점을 생성한다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void createFirstStation(int value) {
        지하철_구간_생성(신분당선ID, 구간_정보_맵(미금역ID, 광교역ID, 10));
        assertThat(상행_종점(목록_이름_조회(지하철_역_목록_조회(신분당선ID)))).isEqualTo("미금역");


        Map 신규_구간_정보 = 구간_정보_맵(강남역ID, 미금역ID, value);
        ExtractableResponse<Response> 생성_결과 = 지하철_구간_생성(신분당선ID, 신규_구간_정보);

        assertAll(
                ()->assertThat(생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                ()->assertThat(상행_종점(목록_이름_조회(지하철_역_목록_조회(신분당선ID)))).isEqualTo("강남역")
        );
    }



    /**
     * Given 구간이 하나 등록되어 있을때
     * When 새로 등록할 구간의 상행역, 하행역이 모두 구간 역 목록에 존재할 경우
     * Then 400 BAD_REQUEST가 발생한다.
     */
    @DisplayName("동일 구간 추가 예외 발생")
    @Test
    void createExceptionDuplication() {
        Map 구간_정보 = 구간_정보_맵(강남역ID, 광교역ID, 10);
        지하철_구간_생성(신분당선ID, 구간_정보_맵(강남역ID, 광교역ID, 10));
        assertThat(목록_이름_조회(지하철_역_목록_조회(신분당선ID))).contains("강남역", "광교역");

        ExtractableResponse<Response> 생성_결과 = 지하철_구간_생성(신분당선ID, 구간_정보);

        assertThat(생성_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(생성_결과.as(ErrorResponse.class).getMessage()).isEqualTo(ErrMsg.SECTION_ALREADY_EXISTS);
    }


    /**
     * Given 구간이 하나 등록되어 있을때
     * When 상행역, 하행역이 모두 지하철역 목록에 존재하지 않는 구간을 등록할 경우
     * Then 400 BAD_REQUEST가 발생한다.
     */
    @DisplayName("상행역, 하행역 모두 존재 하지 않음")
    @Test
    void createExceptionNoMatch() {
        Map 구간_정보 = 구간_정보_맵(강남역ID, 광교역ID, 10);
        지하철_구간_생성(신분당선ID, 구간_정보_맵(강남역ID, 광교역ID, 10));
        assertFalse(목록_이름_조회(지하철_역_목록_조회(신분당선ID)).contains("정자역"));
        assertFalse(목록_이름_조회(지하철_역_목록_조회(신분당선ID)).contains("미금역"));

        ExtractableResponse<Response> 생성_결과 = 지하철_구간_생성(신분당선ID, 구간_정보_맵(정자역ID, 미금역ID, 10));

        assertThat(생성_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(생성_결과.as(ErrorResponse.class).getMessage()).isEqualTo(ErrMsg.NO_MATCHING_STATIONS);
    }

    /**
     * Given 구간이 두개 등록되어 있을때
     * When 종점을 포함하는 섹션을 삭제요청 하면
     * Then 섹션의 삭제가 성공하고, 목록 조회시 섹션의 갯수가 1개 줄고, 종점이 변경되었음을 확인할 수 있다.
     */
    @DisplayName("종점 삭제 결과 검증")
    @Test
    void deleteFirstOrLastStation_test() {
        지하철_구간_생성(신분당선ID, 구간_정보_맵(강남역ID, 미금역ID, 10));
        지하철_구간_생성(신분당선ID, 구간_정보_맵(미금역ID, 광교역ID, 10));
        assertThat(목록_아이디_조회(지하철_구간_목록_조회(신분당선ID))).hasSize(2);
        assertThat(상행_종점(목록_이름_조회(지하철_역_목록_조회(신분당선ID)))).isEqualTo("강남역");

        ExtractableResponse<Response> 삭제_결과 = 지하철_역_삭제(신분당선ID, 강남역ID);

        assertAll(
                ()->assertThat(삭제_결과.statusCode()).isEqualTo(HttpStatus.OK.value()),
                ()->assertThat(목록_아이디_조회(지하철_구간_목록_조회(신분당선ID))).hasSize(1),
                ()->assertThat(상행_종점(목록_이름_조회(지하철_역_목록_조회(신분당선ID)))).isEqualTo("미금역")
        );
    }


    /**
     * Given 구간이 한개 등록되어 있을때
     * When 역을 삭제하는 요청을 하면
     * Then 400 BAD_REQUEST가 발생한다.
     */
    @DisplayName("한개 구간 존재시 역 삭제 에러 발생")
    @Test
    void deleteStationWhenOnlyOneSection_test() {
        지하철_구간_생성(신분당선ID, 구간_정보_맵(강남역ID, 미금역ID, 10));

        ExtractableResponse<Response> 삭제_결과 = 지하철_역_삭제(신분당선ID, 강남역ID);

        assertAll(
                ()->assertThat(삭제_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                ()->assertThat(삭제_결과.as(ErrorResponse.class).getMessage()).isEqualTo(ErrMsg.CANNOT_DELETE_SECTION_WHEN_ONE)

        );
    }

    /**
     * Given 구간이 두개 등록되어 있을때
     * When 중간에 있는 역을 삭제요청하면
     * Then 해당 역이 삭제되고, 하행역으로 갖는 구간의 하행역과 거리가 변경된다.
     */
    @DisplayName("중간에 있는 역 삭제")
    @Test
    void deleteStationInMiddle_test() {
        Long 상행_구간_ID = 아이디_조회(지하철_구간_생성(신분당선ID, 구간_정보_맵(강남역ID, 미금역ID, 10)));
        지하철_구간_생성(신분당선ID, 구간_정보_맵(미금역ID, 광교역ID, 10));

        ExtractableResponse<Response> 삭제_결과 = 지하철_역_삭제(신분당선ID, 미금역ID);
        ExtractableResponse<Response> 역_조회_결과 = 지하철_역_목록_조회(신분당선ID);
        SectionResponse 구간_조회_결과 = 지하철_구간_조회(신분당선ID, 상행_구간_ID).as(SectionResponse.class);

        assertAll(
                ()-> assertThat(삭제_결과.statusCode()).isEqualTo(HttpStatus.OK.value()),
                ()-> assertFalse(결과에_존재한다(역_조회_결과, "미금역")),
                ()-> assertThat(구간_조회_결과.getDistance().getValue()).isEqualTo(20),
                ()-> assertThat(구간_조회_결과.getDownStationId()).isEqualTo(광교역ID)
        );
    }


    public static ExtractableResponse<Response> 지하철_구간_생성(Long 노선ID, Map 구간_정보) {

        return RestAssured.given().log().all()
                .pathParam("id", 노선ID)
                .body(구간_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_목록_조회(Long 노선ID) {

        return RestAssured.given().log().all()
                .pathParam("id", 노선ID)
                .when().get("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_조회(Long 노선ID, Long 구간ID) {

        return RestAssured.given().log().all()
                .pathParam("lineId", 노선ID)
                .pathParam("sectionId", 구간ID)
                .when().get("/lines/{lineId}/sections/{sectionId}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_역_삭제(Long 노선ID, Long 역ID) {

        return RestAssured.given().log().all()
                .pathParam("lineId", 노선ID)
                .param("stationId", 역ID)
                .when().delete("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_목록_조회(Long 노선ID) {

        return RestAssured.given().log().all()
                .pathParam("id", 노선ID)
                .when().get("/lines/{id}/stations")
                .then().log().all()
                .extract();
    }

    public static Map 구간_정보_맵(Long 상행역ID, Long 하행역ID, int 거리){
        Map<String, String> 구간_정보 = new HashMap<>();
        구간_정보.put("upStationId", 상행역ID+"");
        구간_정보.put("downStationId", 하행역ID+"");
        구간_정보.put("distance", 거리+"");
        return 구간_정보;
    }
    private String 하행_종점(List<String> 목록_리스트){
        if (목록_리스트.size()>0){
            return 목록_리스트.get(목록_리스트.size()-1);
        }
        return null;
    }

    private String 상행_종점(List<String> 목록_리스트){
        if (목록_리스트.size()>0){
            return 목록_리스트.get(0);
        }
        return null;
    }
}