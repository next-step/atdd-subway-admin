package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 인수테스트")
@Sql("/sql/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 노선을 생성을 생성하면
     * Then 노선이 생성된다
     * Then 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("새로운 노선을 생성한다.")
    @Test
    void createLine() {
        //when
        ExtractableResponse<Response> response = 노선을_생성한다("7호선", "#EEEEEE");

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> response_read = 전체_노선목록을_조회한다();

        //then
        List<String> lineNames = response_read.jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf("7호선");
    }

    /**
     * Given 노선을 생성하고
     * When 기존에 존재하는 노선 이름으로 노선을 생성하면
     * Then 노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 노선 이름으로 새로운 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        //given
        노선을_생성한다("7호선", "#EEEEEE");

        //when
        ExtractableResponse<Response> response = 노선을_생성한다("7호선", "#FFFFFF");

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 색상이 없이 노선을 생성하면
     * Then 노선 생성이 안된다
     */
    @DisplayName("Color를 지정하지 않고 노선을 생성한다.")
    @Test
    void createLineWithOutColor() {
        //when
        ExtractableResponse<Response> response = 노선을_생성한다("7호선", null);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 노선을 생성하고
     * When 노선 목록을 조회하면
     * Then 2개의 노선을 응답 받는다
     */
    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        //given
        노선을_생성한다("7호선", "#EEEEEE");
        노선을_생성한다("8호선", "#FFFFFF");

        //when
        ExtractableResponse<Response> response = 전체_노선목록을_조회한다();

        //then
        List<String> lines = response.jsonPath().getList("name", String.class);
        assertThat(lines.size()).isEqualTo(2);
        assertThat(lines).contains("7호선", "8호선");
    }

    /**
     * Given 노선을 생성하고
     * When 그 노선을 삭제하면
     * Then 그 노선 목록 조회 시 생성한 노선을 찾을 수 없다
     */
    @DisplayName("새로운 노선을 생성하고 제거한다.")
    @Test
    void deleteLine() {
        //given
        ExtractableResponse<Response> response_create = 노선을_생성한다("7호선", "#EEEEEE");
        LineResponse lineResponse = response_create.as(LineResponse.class);

        //when
        ExtractableResponse<Response> response_delete = 해당_노선을_제거한다(lineResponse.getId());
        assertThat(response_delete.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * When 존재하지 않는 노선을 삭제하면
     * Then 노선 삭제가 되지 않는다 (BAD_REQUEST처리)
     */
    @DisplayName("존재하지 않는 노선을 제거한다.")
    @Test
    void deleteEmptyLine() {
        //when
        ExtractableResponse<Response> response_delete = 해당_노선을_제거한다(3L);

        //then
        assertThat(response_delete.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 노선을_생성한다(String name, String color) {
        LineRequest lineRequest = new LineRequest(name, color);
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 전체_노선목록을_조회한다() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 해당_노선을_제거한다(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();
    }
}
