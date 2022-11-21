package nextstep.subway.section;

import static nextstep.subway.line.LineFixture.지하철_노선_생성후_아이디_반환;
import static nextstep.subway.section.SectionFixture.구간_등록;
import static nextstep.subway.station.StationFixture.지하철역_생성후_아이디_반환;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class SectionAcceptanceTest extends AcceptanceTest {

    Long 이호선;
    Long 강남역;
    Long 역삼역;
    Long 블루보틀역;
    Long 스타벅스역;

    @BeforeEach
    void setUpLine() {
        강남역 = 지하철역_생성후_아이디_반환("강남역");
        역삼역 = 지하철역_생성후_아이디_반환("역삼역");
        블루보틀역 = 지하철역_생성후_아이디_반환("블루보틀역");
        스타벅스역 = 지하철역_생성후_아이디_반환("스타벅스역");
        이호선 = 지하철_노선_생성후_아이디_반환("이호선", "bg-green-600", 7, 강남역, 역삼역);
    }

    /*
    GIVEN 강남역을 상행선 역삼역을 하행선으로 있는 이호선
    WHEN 강남역 역삼역 사이 블루보틀역 등록
    THEN 이호선 강남역 역삼역 사이 블루보틀역 추가
     */
    @Test
    @DisplayName("역 사이에 새로운 역 등록")
    void addStationBetween() {
        //when
        구간_등록(이호선, 강남역, 블루보틀역, 4);

        //then
        assertThat(SectionFixture.구간_목록_조회(이호선).jsonPath().getList(".", SectionResponse.class))
            .hasSize(2)
            .extracting(SectionResponse::getDownStation)
            .extracting(StationResponse::getName)
            .contains("역삼역", "블루보틀역");
    }

    /*
   GIVEN 강남역을 상행선 역삼역을 하행선으로 있는 이호선
   WHEN 강남역 역삼역 사이 블루보틀역 등록
   THEN 이호선 강남역 역삼역 사이 블루보틀역 추가
    */
    @Test
    @DisplayName("역 사이에 새로운 역 등록")
    void addStationBetween2() {
        //when
        구간_등록(이호선, 블루보틀역, 역삼역, 4);

        //then
        assertThat(SectionFixture.구간_목록_조회(이호선).jsonPath().getList(".", SectionResponse.class))
            .hasSize(2)
            .extracting(SectionResponse::getDownStation)
            .extracting(StationResponse::getName)
            .contains("역삼역", "블루보틀역");
    }

    /*
    GIVEN 강남역을 상행선 역삼역을 하행선으로 있는 이호선
    WHEN 강남역 앞에 블루보틀역 등록
    THEN 이호선 상행종점은 블루보틀역
     */
    @Test
    @DisplayName("새로운 상행역 종점 등록")
    void addStationFirst() {
        //when
        구간_등록(이호선, 블루보틀역, 강남역, 4);

        //then
        assertThat(SectionFixture.구간_목록_조회(이호선).jsonPath().getList(".", SectionResponse.class))
            .hasSize(2)
            .extracting(SectionResponse::getUpStation)
            .extracting(StationResponse::getName)
            .contains("강남역", "블루보틀역");
    }

    /*
    GIVEN 강남역을 상행선 역삼역을 하행선으로 있는 이호선
    WHEN 역삼역 뒤에 블루보틀역 등록
    THEN 이호선 하행종점은 블루보틀역
     */
    @Test
    @DisplayName("새로운 하행역 종점 등록")
    void addStationLast() {
        //when
        구간_등록(이호선, 역삼역, 블루보틀역, 3);

        //then
        assertThat(SectionFixture.구간_목록_조회(이호선).jsonPath().getList(".", SectionResponse.class))
            .hasSize(2)
            .extracting(SectionResponse::getDownStation)
            .extracting(StationResponse::getName)
            .contains("역삼역", "블루보틀역");
    }

    @Test
    @DisplayName("역 사이에 새로운 역 등록시 길이가 같거나 크면 에러 발생")
    void longerDistanceException() {
        //when
        ExtractableResponse<Response> response = 구간_등록(이호선, 강남역, 블루보틀역, 7);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("동일한 상행선과 하행선을 가진 구간 추가시 에러 발생")
    void sameSectionException() {
        //when
        ExtractableResponse<Response> response = 구간_등록(이호선, 강남역, 역삼역, 3);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("상행선과 하행선이 없는 구간 추가시 에러 발생")
    void noRelateSectionException() {
        //when
        ExtractableResponse<Response> response = 구간_등록(이호선, 블루보틀역, 스타벅스역, 7);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
