package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.api.AssertMethod.*;
import static nextstep.subway.api.HttpMethod.*;
import static nextstep.subway.fixture.LineFixture.이호선;
import static nextstep.subway.fixture.LineFixture.팔호선;
import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    void setUpInLineAcceptanceTest() {
        createStationInAdvance();
    }

    @DisplayName("지하철 노선의 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_등록(팔호선, 암사역, 천호역, 1300);

        // then
        지하철_노선_생성_확인(response, 팔호선, 암사역, 천호역);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록(팔호선, 암사역, 천호역, 1300);

        // when
        ExtractableResponse<Response> response = 지하철_노선_등록(팔호선, 암사역, 천호역, 1300);

        // then
        지하철_노선_생성_실패_확인(response);
    }

    @DisplayName("지하철 노선의 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createdResponse1 = 지하철_노선_등록(팔호선, 암사역, 천호역, 1300);
        ExtractableResponse<Response> createdResponse2 = 지하철_노선_등록(팔호선, 천호역, 강동구청역, 900);

        // when
        ExtractableResponse response = 지하철_노선_목록_조회();

        // then
        지하철_노선_목록_응답_확인(response);
        지하철_노선_목록_포함_확인(response, createdResponse1.as(LineResponse.class));
        지하철_노선_목록_포함_확인(response, createdResponse2.as(LineResponse.class));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록(팔호선, 암사역, 천호역, 1300);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회(createdResponse);

        // then
        지하철_노선_등록_정상_응답_확인(response);
        지하철_노선_포함_확인(response, createdResponse.as(LineResponse.class));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록(팔호선, 암사역, 천호역, 1300);

        Long 암사역_아이디 = 역_생성_응답.get("암사역").getId();
        Long 천호역_아이디 = 역_생성_응답.get("천호역").getId();

        // when
        ExtractableResponse response = 지하철_노선_수정(createdResponse, LineRequest.of(이호선, 암사역_아이디, 천호역_아이디, 1000));

        // then
        지하철_노선_등록_정상_응답_확인(response);
        지하철_노선_수정_확인(지하철_노선_조회(createdResponse), 이호선);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록(팔호선, 암사역, 천호역, 1300);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거(createdResponse);

        // then
        지하철_노선_삭제_확인(response);
        지하철_노선_조회_없음_확인(createdResponse);
    }

    @DisplayName("지하철 노선의 구간을 기존의 구간사이에 생성한다.(상행선 일치)")
    @Test
    void createSection1_A() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);

        // when
        ExtractableResponse<Response> response2 = 팔호선_구간_등록(response1, 암사역, 천호역, 300);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        LineResponse lineResponse = response2.as(LineResponse.class);
        assertThat(lineResponse.getStations().get(0).getId()).isEqualTo(암사역_아이디);
        assertThat(lineResponse.getStations().get(1).getId()).isEqualTo(천호역_아이디);
        assertThat(lineResponse.getStations().get(2).getId()).isEqualTo(강동구청역_아이디);

        assertThat(lineResponse.getSectionFromUpStationId(암사역_아이디).getDistance()).isEqualTo(300);
        assertThat(lineResponse.getSectionFromUpStationId(천호역_아이디).getDistance()).isEqualTo(400);
    }

    @DisplayName("지하철 노선의 구간을 기존의 구간사이에 생성한다.(하행선 일치)")
    @Test
    void createSection1_B() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);

        // when
        ExtractableResponse<Response> response2 = 팔호선_구간_등록(response1, 천호역, 강동구청역, 300);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        LineResponse lineResponse = response2.as(LineResponse.class);
        assertThat(lineResponse.getStations().get(0).getId()).isEqualTo(암사역_아이디);
        assertThat(lineResponse.getStations().get(1).getId()).isEqualTo(천호역_아이디);
        assertThat(lineResponse.getStations().get(2).getId()).isEqualTo(강동구청역_아이디);

        assertThat(lineResponse.getSectionFromUpStationId(암사역_아이디).getDistance()).isEqualTo(400);
        assertThat(lineResponse.getSectionFromUpStationId(천호역_아이디).getDistance()).isEqualTo(300);
    }

    @DisplayName("지하철 노선의 구간을 상행 종점으로 생성한다.")
    @Test
    void createSection2() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);

        // when
        ExtractableResponse<Response> response2 = 팔호선_구간_등록(response1, 천호역, 암사역, 300);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        LineResponse lineResponse = response2.as(LineResponse.class);
        assertThat(lineResponse.getStations().get(0).getId()).isEqualTo(천호역_아이디);
        assertThat(lineResponse.getStations().get(1).getId()).isEqualTo(암사역_아이디);
        assertThat(lineResponse.getStations().get(2).getId()).isEqualTo(강동구청역_아이디);

        assertThat(lineResponse.getSectionFromUpStationId(천호역_아이디).getDistance()).isEqualTo(300);
        assertThat(lineResponse.getSectionFromUpStationId(암사역_아이디).getDistance()).isEqualTo(700);
    }

    @DisplayName("지하철 노선의 구간을 하행 종점으로 생성한다.")
    @Test
    void createSection3() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);

        // when
        ExtractableResponse<Response> response2 = 팔호선_구간_등록(response1, 강동구청역, 몽촌토성역, 1000);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        LineResponse lineResponse = response2.as(LineResponse.class);
        assertThat(lineResponse.getStations().get(0).getId()).isEqualTo(암사역_아이디);
        assertThat(lineResponse.getStations().get(1).getId()).isEqualTo(강동구청역_아이디);
        assertThat(lineResponse.getStations().get(2).getId()).isEqualTo(몽촌토성역_아이디);

        assertThat(lineResponse.getSectionFromUpStationId(암사역_아이디).getDistance()).isEqualTo(700);
        assertThat(lineResponse.getSectionFromUpStationId(강동구청역_아이디).getDistance()).isEqualTo(1000);
    }

    @DisplayName("지하철 노선의 구간 중 상행 종점을 제거한다.")
    @Test
    void deleteSection1() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);
        팔호선_구간_등록(response1, 강동구청역, 몽촌토성역, 1000);

        // when
        ExtractableResponse response2 = 상행역_기반_구간_제거(response1, 암사역_아이디);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> response3 = 지하철_노선_조회(response1);
        LineResponse lineResponse3 = response3.as(LineResponse.class);

        assertThat(lineResponse3.getStations().get(0).getId()).isEqualTo(강동구청역_아이디);
        assertThat(lineResponse3.getStations().get(1).getId()).isEqualTo(몽촌토성역_아이디);
        assertThat(lineResponse3.getSectionFromUpStationId(강동구청역_아이디).getDistance()).isEqualTo(1000);
    }

    @DisplayName("지하철 노선의 구간 중 하행 종점을 제거한다.")
    @Test
    void deleteSection2() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);
        팔호선_구간_등록(response1, 강동구청역, 몽촌토성역, 1000);

        // when
        ExtractableResponse response2 = 상행역_기반_구간_제거(response1, 몽촌토성역_아이디);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> response3 = 지하철_노선_조회(response1);
        LineResponse lineResponse3 = response3.as(LineResponse.class);

        assertThat(lineResponse3.getStations().get(0).getId()).isEqualTo(암사역_아이디);
        assertThat(lineResponse3.getStations().get(1).getId()).isEqualTo(강동구청역_아이디);
        assertThat(lineResponse3.getSectionFromUpStationId(암사역_아이디).getDistance()).isEqualTo(700);
    }

    @DisplayName("지하철 노선의 구간 중 중간역을 제거한다.")
    @Test
    void deleteSection3() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);
        팔호선_구간_등록(response1, 강동구청역, 몽촌토성역, 1000);

        // when
        ExtractableResponse response2 = 상행역_기반_구간_제거(response1, 강동구청역_아이디);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> response3 = 지하철_노선_조회(response1);
        LineResponse lineResponse3 = response3.as(LineResponse.class);

        assertThat(lineResponse3.getStations().get(0).getId()).isEqualTo(암사역_아이디);
        assertThat(lineResponse3.getStations().get(1).getId()).isEqualTo(몽촌토성역_아이디);
        assertThat(lineResponse3.getSectionFromUpStationId(암사역_아이디).getDistance()).isEqualTo(1700);
    }


    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 BAD_REQUEST를 응답으로 받는다.")
    @Test
    void exceptionTest1() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);

        // when
        ExtractableResponse<Response> response2 = 팔호선_구간_등록(response1, 암사역, 천호역, 700);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 BAD_REQUEST를 응답으로 받는다.")
    @Test
    void exceptionTest2() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);
        팔호선_구간_등록(response1, 강동구청역, 몽촌토성역, 300);

        // when
        ExtractableResponse<Response> response2 = 팔호선_구간_등록(response1, 암사역, 몽촌토성역, 1000);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 BAD_REQUEST를 응답으로 받는다.")
    @Test
    void exceptionTest3() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);

        // when
        ExtractableResponse<Response> response2 = 팔호선_구간_등록(response1, 천호역, 몽촌토성역, 1000);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간이 하나(암사역-강동구청역)인 상태에서 상행선을 제거하면, BAD_REQUEST를 응답으로 받는다.")
    @Test
    void exceptionTest4() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);

        // when
        ExtractableResponse response2 = 상행역_기반_구간_제거(response1, 암사역_아이디);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간이 하나(암사역-강동구청역)인 상태에서 하행선을 제거하면, BAD_REQUEST를 응답으로 받는다.")
    @Test
    void exceptionTest5() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);

        // when
        ExtractableResponse response2 = 상행역_기반_구간_제거(response1, 강동구청역_아이디);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간에 없는 역을 제거를 시도하면, BAD_REQUEST_를 응답으로 받는다.")
    @Test
    void exceptionTest6() {
        // given
        ExtractableResponse<Response> response1 = 팔호선_노선_등록(암사역, 강동구청역, 700);

        // when
        ExtractableResponse response2 = 상행역_기반_구간_제거(response1, 천호역_아이디);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
