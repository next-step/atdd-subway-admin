package nextstep.subway.section;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록_요청하기;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.LineAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    ObjectMapper objectMapper;

    StationResponse 강남역;
    StationResponse 정자역;
    LineResponse 신분당선;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        super.setUp();

        // given
        강남역 = 지하철역_등록_요청하기("강남역").as(StationResponse.class);
        정자역 = 지하철역_등록_요청하기("정자역").as(StationResponse.class);

        신분당선 = LineAcceptanceTest.지하철노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 정자역.getId(), 10)
                .as(LineResponse.class);
    }

    /**
     * Given 강남역과 정자역이 신분당선에 등록되어 있고, 양재역이 등록되어 있을 때
     * When 강남역-양재역 구간을 강남역과 정자역 사이에 등록할 때
     * Then 추가된 역이 있는지 확인할 수 있다
     */
    @Test
    void 구간_사이에_새로운역_등록() throws JsonProcessingException {
        // given
        Station 양재역 = 지하철역_등록_요청하기("양재역").as(Station.class);
        SectionRequest 구간_등록_요청건 = new SectionRequest(강남역.getId(), 양재역.getId(), 1);

        // when
        ExtractableResponse<Response> 지하철구간_등록_요청_결과 = 지하철구간_등록_요청하기(구간_등록_요청건, 신분당선);

        // then
        지하철_구간_생성_정상_응답됨(지하철구간_등록_요청_결과);
    }

    /**
     * Given 강남역과 정자역이 신분당선에 등록되어 있고, 신사역이 등록되어 있을 때
     * When 신사역을 상행종점역으로 등록할 때
     * Then 추가된 역이 있는지 확인할 수 있다
     */
    @Test
    void 새로운역_상행종점역_등록() throws JsonProcessingException {
        // given
        Station 신사역 = 지하철역_등록_요청하기("신사역").as(Station.class);
        SectionRequest 구간_등록_요청건 = new SectionRequest(강남역.getId(), 신사역.getId(), 1);

        // when
        ExtractableResponse<Response> 지하철구간_등록_요청_결과 = 지하철구간_등록_요청하기(구간_등록_요청건, 신분당선);

        // then
        지하철_구간_생성_정상_응답됨(지하철구간_등록_요청_결과);
    }

    /**
     * Given 강남역과 정자역이 신분당선에 등록되어 있고, 미금역이 등록되어 있을 때
     * When 미금역을 하행종점역으로 등록할 때
     * Then 추가된 역이 있는지 확인할 수 있다
     */
    @Test
    void 새로운역_하행종점역_등록() throws JsonProcessingException {
        // given
        Station 미금역 = 지하철역_등록_요청하기("미금역").as(Station.class);
        SectionRequest 구간_등록_요청건 = new SectionRequest(강남역.getId(), 미금역.getId(), 1);

        // when
        ExtractableResponse<Response> 지하철구간_등록_요청_결과 = 지하철구간_등록_요청하기(구간_등록_요청건, 신분당선);

        // then
        지하철_구간_생성_정상_응답됨(지하철구간_등록_요청_결과);
    }

    /**
     * Given 강남역과 정자역구간의 길이가 10으로 신분당선에 등록되어 있고, 미금역이 등록되어 있을 때
     * When 강남역-미금역 길이가 10으로 구간 등록할 때
     * Then 정상 등록되지 않음을 확인할 수 있다
     */
    @Test
    void 예외확인_구간_길이_기존_같을시_등록_불가() throws JsonProcessingException {
        // given
        Station 미금역 = 지하철역_등록_요청하기("미금역").as(Station.class);
        SectionRequest 구간_등록_요청건 = new SectionRequest(강남역.getId(), 미금역.getId(), 10);

        // when
        ExtractableResponse<Response> 지하철구간_등록_요청_결과 = 지하철구간_등록_요청하기(구간_등록_요청건, 신분당선);

        // then
        지하철구간_등록_오류_응답됨(지하철구간_등록_요청_결과);
    }

    /**
     * Given 강남역-양재역, 강남역-정자역 등록되어 있을 때
     * When 강남역-양재역 구간 등록 요청시
     * Then 정상 등록되지 않음을 확인할 수 있다
     */
    @Test
    void 기존구간_등록_불가() throws JsonProcessingException {
        // given
        Station 양재역 = 지하철역_등록_요청하기("양재역").as(Station.class);
        SectionRequest 구간_등록_요청건 = new SectionRequest(강남역.getId(), 양재역.getId(), 1);
        지하철구간_등록_요청하기(구간_등록_요청건, 신분당선);
        SectionRequest 구간_등록_요청_중복건 = new SectionRequest(강남역.getId(), 양재역.getId(), 10);

        // when
        ExtractableResponse<Response> 지하철구간_중복_등록_요청_결과 = 지하철구간_등록_요청하기(구간_등록_요청_중복건, 신분당선);

        // then
        지하철구간_등록_오류_응답됨(지하철구간_중복_등록_요청_결과);
    }

    /**
     * Given 강남역-정자역,정자역-미금역 등록되어 있을 때
     * When 강남역-미금역 구간 등록 요청시
     * Then 정상 등록되지 않음을 확인할 수 있다
     */
    @Test
    void 포함된_구간_등록_불가() throws JsonProcessingException {
        // given
        Station 미금역 = 지하철역_등록_요청하기("미금역").as(Station.class);
        SectionRequest 구간_등록_요청건 = new SectionRequest(정자역.getId(), 미금역.getId(), 1);
        지하철구간_등록_요청하기(구간_등록_요청건, 신분당선);
        SectionRequest 포함구간_등록_요청건 = new SectionRequest(강남역.getId(), 미금역.getId(), 1);

        // when
        ExtractableResponse<Response> 지하철구간_포함_등록_요청_결과 = 지하철구간_등록_요청하기(포함구간_등록_요청건, 신분당선);

        // then
        지하철구간_등록_오류_응답됨(지하철구간_포함_등록_요청_결과);
    }

    private void 지하철_구간_생성_정상_응답됨(ExtractableResponse<Response> 지하철구간_등록_요청_결과) {
        assertThat(지하철구간_등록_요청_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철구간_등록_오류_응답됨(ExtractableResponse<Response> 지하철구간_등록_요청_결과) {
        assertThat(지하철구간_등록_요청_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철구간_등록_요청하기(SectionRequest sectionRequest, LineResponse lineResponse)
            throws JsonProcessingException {

        String param = objectMapper.writeValueAsString(sectionRequest);

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", lineResponse.getId())
                .then().log().all()
                .extract();
    }
}
