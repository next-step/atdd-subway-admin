package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestStation {
    private static final Function<RequestSpecification, Response> CREATE = requestSpecification -> requestSpecification.post("/stations");
    private static final Function<RequestSpecification, Response> SEARCH_ALL = requestSpecification -> requestSpecification.get("/stations");
    private static final String INVALID_KEY = "name";


    public ExtractableResponse<Response> createStation(final String stationName) {
        return this.request(() -> makeBody(INVALID_KEY, stationName), CREATE);
    }

    public ExtractableResponse<Response> getStations() {
        return this.request(HashMap::new, SEARCH_ALL);
    }

    public ExtractableResponse<Response> deleteStation(final Long index) {
        final Function<RequestSpecification, Response> DELETE = requestSpecification -> requestSpecification.delete("/stations/{id}", index);
        return this.request(HashMap::new, DELETE);
    }

    private Map<String, String> makeBody(final String key, final String value) {
        HashMap<String, String> input = new HashMap<>();
        input.put(key, value);
        return input;
    }

    private ExtractableResponse<Response> request(Supplier<Map<String, String>> supplier, Function<RequestSpecification, Response> function) {
        return function.apply(
                RestAssured.given().log().all()
                        .body(supplier.get())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
        ).then().log().all().extract();
    }
}
