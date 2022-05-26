package nextstep.subway.section;

import static nextstep.subway.LineTestHelper.노선_생성_요청;
import static nextstep.subway.SectionTestHelper.구간_추가_요청;
import static nextstep.subway.StationTestHelper.구간_제거_실패_확인;
import static nextstep.subway.StationTestHelper.구간_제거_요청;
import static nextstep.subway.StationTestHelper.구간_제거_확인;
import static nextstep.subway.StationTestHelper.역_생성_요청;
import static nextstep.subway.TestHelper.getId;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class SectionAcceptanceTest extends BaseAcceptanceTest {

    private static final String 신분당선 = "신분당선";
    private static final String 분당선 = "분당선";
    private static final String RED_COLOR = "bg-red-600";
    private static final String GREEN_COLOR = "bg-green-600";
    private static final String 강남역 = "강남역";
    private static final String 역삼역 = "역삼역";
    private static final String 삼성역 = "삼성역";
    private static final String 서초역 = "서초역";

    private Long 신분당선_id;
    private Long 분당선_id;
    private Long 강남역_id;
    private Long 역삼역_id;
    private Long 삼성역_id;
    private Long 서초역_id;

    @BeforeEach
    public void setup() {
        ExtractableResponse<Response> 강남역_생성_응답 = 역_생성_요청(강남역);
        강남역_id = getId(강남역_생성_응답);
        ExtractableResponse<Response> 역삼역_생성_응답 = 역_생성_요청(역삼역);
        역삼역_id = getId(역삼역_생성_응답);
        ExtractableResponse<Response> 삼성역_생성_응답 = 역_생성_요청(삼성역);
        삼성역_id = getId(삼성역_생성_응답);
        ExtractableResponse<Response> 서초역_생성_응답 = 역_생성_요청(서초역);
        서초역_id = getId(서초역_생성_응답);

        //강남-역삼
        ExtractableResponse<Response> 노선_생성_응답 = 노선_생성_요청(신분당선, RED_COLOR, 20, 강남역_id, 역삼역_id);
        신분당선_id = getId(노선_생성_응답);

        //삼성-강남-서초
        ExtractableResponse<Response> 분당선_생성_응답 = 노선_생성_요청(분당선, GREEN_COLOR, 20, 삼성역_id, 서초역_id);
        분당선_id = getId(분당선_생성_응답);
        구간_추가_요청(분당선_id, new SectionRequest(삼성역_id, 강남역_id, 10));
    }

    /**
     * When 노선에 중간에 새로운 구간을 추가한다.
     */
    @Test
    public void addSection_middle() {
        //given
        SectionRequest sectionRequest = new SectionRequest(강남역_id, 삼성역_id, 10);

        // when
        ExtractableResponse<Response> 구간_추가_응답 = 구간_추가_요청(신분당선_id, sectionRequest);

        //then
        assertEquals(HttpStatus.OK.value(), 구간_추가_응답.statusCode());
    }

    /**
     * When 상행 종점을 추가한다.
     */
    @Test
    public void addSection_first() {
        //given
        SectionRequest sectionRequest = new SectionRequest(삼성역_id, 강남역_id, 10);

        // when
        ExtractableResponse<Response> 구간_추가_응답 = 구간_추가_요청(신분당선_id, sectionRequest);

        //then
        assertEquals(HttpStatus.OK.value(), 구간_추가_응답.statusCode());
    }

    /**
     * When 하행 종점을 추가한다.
     */
    @Test
    public void addSection_last() {
        //given
        SectionRequest sectionRequest = new SectionRequest(역삼역_id, 삼성역_id, 10);

        // when
        ExtractableResponse<Response> 구간_추가_응답 = 구간_추가_요청(신분당선_id, sectionRequest);

        //then
        assertEquals(HttpStatus.OK.value(), 구간_추가_응답.statusCode());
    }

    /**
     * When 중간 구간에 구간 추가하는 경우 구간길이가 크거나 같으면
     * Then BadRequest
     */
    @Test
    void addSection_fail_over_distance() {
        //given
        SectionRequest sectionRequest = new SectionRequest(강남역_id, 삼성역_id, 20);

        // when
        ExtractableResponse<Response> 구간_추가_응답 = 구간_추가_요청(신분당선_id, sectionRequest);

        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), 구간_추가_응답.statusCode());
    }

    /**
     * When 상행 하행이 모두 같은 구간을 추가하면
     * Then BadRequest
     */
    @Test
    void addSection_fail_same_up_down() {
        //given
        SectionRequest sectionRequest = new SectionRequest(강남역_id, 역삼역_id, 10);

        // when
        ExtractableResponse<Response> 구간_추가_응답 = 구간_추가_요청(신분당선_id, sectionRequest);

        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), 구간_추가_응답.statusCode());
    }

    /**
     * When 상행 하행이 모두 포함하지 않은 구간을 추가하면
     * Then BadRequest
     */
    @Test
    void addSection_fail_not_contains_up_down() {
        //given
        SectionRequest sectionRequest = new SectionRequest(서초역_id, 삼성역_id, 10);

        // when
        ExtractableResponse<Response> 구간_추가_응답 = 구간_추가_요청(신분당선_id, sectionRequest);

        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), 구간_추가_응답.statusCode());
    }

    /**
     * When 중간역을 삭제한다.
     */
    @Test
    void removeSection_middleStation() {
        //when
        ExtractableResponse<Response> response = 구간_제거_요청(분당선_id, 강남역_id);

        //then
        구간_제거_확인(response, 강남역);
    }

    /**
     * When 종점을 삭제하면
     */
    @Test
    void removeStation_endStation() {
        //when
        ExtractableResponse<Response> response = 구간_제거_요청(분당선_id, 삼성역_id);

        //then
        구간_제거_확인(response, 삼성역);
    }


    /**
     * When 노선에 없는 역을 제거하면
     * Then BadRequest
     */
    @Test
    void removeStation_fail_not_contains_station() {
        //when
        ExtractableResponse<Response> response = 구간_제거_요청(분당선_id, 역삼역_id);

        //then
        구간_제거_실패_확인(response);
    }

    /**
     * When 구간이 하나인 노선에서 역을 제거하면
     * Then BadRequest
     */
    @Test
    void removeStation_fail_only_one_section() {
        //when
        ExtractableResponse<Response> response = 구간_제거_요청(신분당선_id, 역삼역_id);

        //then
        구간_제거_실패_확인(response);
    }
}
