package nextstep.subway;


import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
    @LocalServerPort
    private int port;
    @Autowired
    private DatabaseClean databaseClean;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseClean.execute();
    }
    protected Long getId(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }
    protected String getProperty(ExtractableResponse<Response> response, String property) {
        return response.jsonPath().getString(property);
    }
    protected List<String> getList(ExtractableResponse<Response> response, String property) {
        return response.jsonPath().getList(property, String.class);
    }
}
