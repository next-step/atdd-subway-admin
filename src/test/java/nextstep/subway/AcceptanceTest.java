package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.exception.ApiErrorMessage;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }

        databaseCleanup.execute();
    }

    protected void 데이터_생성됨(final ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    protected void 데이터_생성_실패됨(final ExtractableResponse<Response> response, final String errorMessage) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.as(ApiErrorMessage.class).getMessage())
                        .isEqualTo(errorMessage)
        );
    }

    protected ExtractableResponse<Response> 목록_조회_요청(final String uri) {
        return RestAssured.given()
                .log().all()
                .when()
                .get(uri)
                .then()
                .log().all()
                .extract();
    }

    protected void 목록_응답됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    protected <T, S> void 목록에_포함_검증(final T responses, final S... createdResponses) {
        assertAll(
                () -> assertThat(responses).isNotNull(),
                () -> {
                    List<S> createdLines = Arrays.stream(createdResponses).collect(Collectors.toList());
                    String responseListClassName = responses.getClass().getSimpleName();
                    String methodName = "get" + responseListClassName.substring(0, 1).toUpperCase() + responseListClassName.substring(1);
                    Method method = responses.getClass().getMethod(methodName);
                    List<S> responseListValues = (List<S>) method.invoke(responses);
                    assertThat(responseListValues).containsAll(createdLines);
                }
        );
    }

    protected <T> ExtractableResponse<Response> 데이터_생성_요청(final T request, final String uri) {
        return RestAssured.given()
                .log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(uri)
                .then()
                .log().all()
                .extract();
    }

    protected ExtractableResponse<Response> 데이터_제거_요청(final String uri) {
        return RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then()
                .log().all()
                .extract();
    }

    protected void 데이터_삭제완료됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
