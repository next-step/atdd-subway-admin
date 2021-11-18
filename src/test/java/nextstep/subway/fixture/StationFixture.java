package nextstep.subway.fixture;

import nextstep.subway.api.HttpMethod;
import nextstep.subway.station.dto.StationResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static nextstep.subway.fixture.LineFixture.*;

public class StationFixture {
    public static final HashMap<String, String> 암사역 = new HashMap<String, String>() {{
        put("name", "암사역");
    }};

    public static final HashMap<String, String> 천호역 = new HashMap<String, String>() {{
        put("name", "천호역");
    }};

    public static final HashMap<String, String> 강동구청역 = new HashMap<String, String>() {{
        put("name", "강동구청역");
    }};

    public static final HashMap<String, String> 몽촌토성역 = new HashMap<String, String>() {{
        put("name", "몽촌토성역");
    }};

    public static final HashMap<String, String> 잠실역 = new HashMap<String, String>() {{
        put("name", "잠실역");
    }};

    public static final HashMap<String, String> 석촌역 = new HashMap<String, String>() {{
        put("name", "석촌역");
    }};

    public static final HashMap<String, String> 송파역 = new HashMap<String, String>() {{
        put("name", "송파역");
    }};

    public static final HashMap<String, String> 가락시장역 = new HashMap<String, String>() {{
        put("name", "가락시장역");
    }};

    public static final HashMap<String, String> 문정역 = new HashMap<String, String>() {{
        put("name", "문정역");
    }};

    public static final HashMap<String, String> 장지역 = new HashMap<String, String>() {{
        put("name", "장지역");
    }};

    public static final HashMap<String, String> 복정역 = new HashMap<String, String>() {{
        put("name", "복정역");
    }};

    public static final HashMap<String, String> 산성역 = new HashMap<String, String>() {{
        put("name", "산성역");
    }};

    public static final HashMap<String, String> 남한산성입구역 = new HashMap<String, String>() {{
        put("name", "남한산성입구역");
    }};

    public static final HashMap<String, String> 단대오거리역 = new HashMap<String, String>() {{
        put("name", "단대오거리역");
    }};

    public static final HashMap<String, String> 신흥역 = new HashMap<String, String>() {{
        put("name", "신흥역");
    }};

    public static final HashMap<String, String> 수진역 = new HashMap<String, String>() {{
        put("name", "수진역");
    }};

    public static final HashMap<String, String> 모란역 = new HashMap<String, String>() {{
        put("name", "모란역");
    }};
    public static final HashMap<String, String> 역삼역 = new HashMap<String, String>() {{
        put("name", "역삼역");
    }};
    public static final HashMap<String, String> 강남역 = new HashMap<String, String>() {{
        put("name", "강남역");
    }};
    public static Map<String, Map<String, String>> 팔호선_역_모음 = new HashMap<>();
    public static Map<String, Map<String, String>> 이호선_역_모음 = new HashMap<>();

    public static Map<String, StationResponse> eightLineResponses;

    public static Map<String, StationResponse> twoLineResponses;

    public static void createStationInAdvance() {
        eightLineResponses = 역데이터_일괄_저장(팔호선, 팔호선_역_모음, eightLineStations, eightLineDistances);
        twoLineResponses = 역데이터_일괄_저장(이호선, 이호선_역_모음, twoLineStations, twoLineDistances);
    }

    private static Map<String, StationResponse> 역데이터_일괄_저장(Map<String, String> line, Map<String, Map<String, String>> stationsWithLine,
                                                           List<Map<String, String>> lineStations, List<Integer> distances) {
        Map<String, StationResponse> lineResponses = lineStations.stream()
            .map(HttpMethod::지하철_역_등록)
            .map(e -> e.jsonPath().getObject(".", StationResponse.class))
            .collect(Collectors.toMap(StationResponse::getName, Function.identity()));

        역데이터_미리_생성하기(line, stationsWithLine, lineResponses, lineStations, distances);

        return lineResponses;
    }

    private static void 역데이터_미리_생성하기(Map<String, String> line, Map<String, Map<String, String>> stationsWithLine
        , Map<String, StationResponse> lineResponses, List<Map<String, String>> lineStations, List<Integer> distances) {

        for (int i = 0; i < lineStations.size() - 1; i++) {
            StationResponse upStationResponse = lineResponses.get(lineStations.get(i).get("name"));
            StationResponse downStationResponse = lineResponses.get(lineStations.get(i + 1).get("name"));
            String upStationId = String.valueOf(upStationResponse.getId());
            String downStationId = String.valueOf(downStationResponse.getId());
            String distance = String.valueOf(distances.get(i));

            stationsWithLine.put(upStationResponse.getName(), new HashMap<String, String>() {{
                    put("name", line.get("name"));
                    put("color", line.get("color"));
                    put("upStationId", upStationId);
                    put("downStationId", downStationId);
                    put("distance", distance);
                }}
            );
        }
    }
}
