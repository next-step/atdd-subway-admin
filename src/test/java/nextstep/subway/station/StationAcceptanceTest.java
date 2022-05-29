package nextstep.subway.station;

import static nextstep.subway.utils.AttdStationUtils.지하철목록_조회하기;
import static nextstep.subway.utils.AttdStationUtils.지하철상세_조회하기;
import static nextstep.subway.utils.AttdStationUtils.지하철역_만들기;
import static nextstep.subway.utils.AttdStationUtils.지하철역_수정하기;
import static nextstep.subway.utils.AttdStationUtils.지하철역_지우기;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class StationAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void 지하철역_생성하기_테스트() {
        // when
        ExtractableResponse<Response> 지하철역_만들기_response = 지하철역_만들기("강남역");

        // then
        assertThat(지하철역_만들기_response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> 지하철역_조회하기_response = 지하철목록_조회하기();
        List<String> stationNames = 지하철역_조회하기_response.jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void 지하철역_중복으로_생성하기_테스트() {
        // given
        지하철역_만들기("강남역");

        // when
        ExtractableResponse<Response> 지하철_중복으로_만들기_response = 지하철역_만들기("강남역");

        // then
        assertThat(지하철_중복으로_만들기_response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역들을 조회한다.")
    @Test
    void 지하철역_리스트_조회하기_테스트() {
        //given
        지하철역_만들기("강남역");
        지하철역_만들기("양재역");

        //when
        ExtractableResponse<Response> response = 지하철목록_조회하기();

        //then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.jsonPath().getList("$"))
                    .extracting("id")
                    .containsExactly(1, 2),
            () -> assertThat(response.jsonPath().getList("$"))
                    .extracting("name")
                    .containsExactly("강남역", "양재역")
        );
    }


    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void 지하철역_지우기_테스트() {
        //given
        지하철역_만들기("강남역"); //id 1
        지하철역_만들기("역삼역"); //id 2

        //when
        지하철역_지우기(1);

        //then
        ExtractableResponse<Response> response = 지하철목록_조회하기();
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.jsonPath().getList("$"))
                .extracting("id")
                .containsExactly(2),
            () -> assertThat(response.jsonPath().getList("$"))
                .extracting("name")
                .containsExactly("역삼역")
        );
    }


    /**
     * Given 지하철역을 생성하고
     * When 똑같은 이름의 지하철을 생성하면
     * Then 생성이 되지 않고 에러(400)이 발생한다
     * */
    @Test
    @DisplayName("중복된 지하철이름을 저장시 에러가 발생한다")
    public void 지하철_중복_저장하기_테스트(){
        //given
        ExtractableResponse<Response> responseOk = 지하철역_만들기("강남역");

        //when
        ExtractableResponse<Response> response = 지하철역_만들기("강남역");

        //then
        assertAll(
            () -> assertThat(responseOk.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );
    }

    /**
     * when 없는 지하철을 삭제하면
     * then 에러가 발생한다.
     * */
    @Test
    @DisplayName("저장되지 않은 지하철역을 삭제시 에러 발생한다.")
    public void 없는_지하철역_삭제하기_테스트(){
        //when
        ExtractableResponse<Response> response = 지하철역_지우기(10);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
    * given 지하철을 생성하고
    * when 지하철을 수정하면
    * then 수정된 지하철이 확인가능하다.
    * */
    @Test
    @DisplayName("지하철 저장 후 수정이 가능해야한다")
    public void 지하철_수정하기_테스트() {
        //given
        ExtractableResponse<Response> 강남역 = 지하철역_만들기("강남역");
        long 강남역_ID = Integer.toUnsignedLong(강남역.jsonPath().get("id"));

        //when
        Map<String, String> 수정할_데이터 = new HashMap<>();
        수정할_데이터.put("name", "강남역수정");
        ExtractableResponse<Response> 강남역_수정결과 = 지하철역_수정하기(String.valueOf(강남역_ID), 수정할_데이터);

        //then
        assertThat(강남역_수정결과.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * given 지하철을 생성하고
     * when 지하철ID를 통해 조회하면
     * then 상세 정보를 받을수 있다.
     */
    @Test
    @DisplayName("지하철 번호를 통해 상세 정보 조회가 가능하다")
    public void 지하철_상세정보_조회하기_테스트() {
        //given
        ExtractableResponse<Response> 강남역 = 지하철역_만들기("강남역");
        long 강남역_ID = Integer.toUnsignedLong(강남역.jsonPath().get("id"));

        //when
        ExtractableResponse<Response> 지하철상세_조회하기_response = 지하철상세_조회하기(강남역_ID);

        //then
        assertAll(
            () -> assertThat(지하철상세_조회하기_response.jsonPath().get("name").toString()).isEqualTo(
                "강남역"),
            () -> assertThat(지하철상세_조회하기_response.jsonPath().get("id").toString()).isEqualTo("1")
        );
    }

}
