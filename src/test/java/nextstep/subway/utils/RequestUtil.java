package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSenderOptions;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestUtil {
    private static final BiFunction<RequestSpecification, String, Response> CREATE = RequestSenderOptions::post;
    private static final BiFunction<RequestSpecification, String, Response> SEARCH_ALL = RequestSenderOptions::get;
    private static final BiFunction<RequestSpecification,String, Response> DELETE = RequestSenderOptions::delete;

    private static final String STATION_URL = "/stations";
    private static final String LINE_URL = "/lines";

    private static final String INVALID_KEY = "name";

    public ExtractableResponse<Response> createLine(final Map<String, String> body) {
        return this.request(()->body, CREATE , LINE_URL);
    }

    public ExtractableResponse<Response> createStation(final String stationName) {
        return this.request(() -> makeBody(INVALID_KEY, stationName), CREATE , STATION_URL);
    }
    public ExtractableResponse<Response> getStations() {
        return this.request(HashMap::new, SEARCH_ALL, STATION_URL);
    }

    public ExtractableResponse<Response> deleteStation(final Long index) {
        return this.request(HashMap::new, DELETE, String.format(STATION_URL+"/%d",index));
    }

    private Map<String, String> makeBody(final String key, final String value) {
        HashMap<String, String> input = new HashMap<>();
        input.put(key, value);
        return input;
    }

    private ExtractableResponse<Response> request(Supplier<Map<String, String>> supplier, BiFunction<RequestSpecification, String, Response> function, String path) {
        return function.apply(
                RestAssured.given().log().all()
                        .body(supplier.get())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when(),
                path
        ).then().log().all().extract();
    }
}
