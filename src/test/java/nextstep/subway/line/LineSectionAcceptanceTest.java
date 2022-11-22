package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.SectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 구간 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {
    private static final String SECTIONS = "sections";
    private static final String SECTION_MAIN_PATH = LineConstant.LINE_MAIN_PATH + "/%d" + "/sections";
    private static final String STATION_ID = "stationId";
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    private Line line;
    private Station gangNamStation;
    private Station seungSuStation;
    private Station hongDaeStation;
    private Station guiStation;

    @BeforeEach
    void init() {
        gangNamStation = stationRepository.save(new Station("강남역"));
        seungSuStation = stationRepository.save(new Station("성수역"));
        hongDaeStation = stationRepository.save(new Station("홍대역"));
        guiStation = stationRepository.save(new Station("구의역"));
        line = new Line("테스트노선", "red");
        line.addStations(gangNamStation, seungSuStation, 10L);
        lineRepository.save(line);
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), seungSuStation.getId(), guiStation.getId(), 8L);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findResponse = findSectionByLine(convertLineId(saveResponse.jsonPath()));
        List<SectionResponse> sections = convertSection(findResponse.jsonPath());
        assertThat(isSameSectionCount(sections.size(), 2)).isTrue();
        assertThat(convertStationName(sections)).containsExactly("강남역", "성수역", "구의역");

    }

    @DisplayName("노선 구간에 새로운 상행 종점을 추가 할 수 있다.")
    @Test
    void addUpStation() {
        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), hongDaeStation.getId(), gangNamStation.getId(), 8L);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findResponse = findSectionByLine(convertLineId(saveResponse.jsonPath()));
        List<SectionResponse> sections = convertSection(findResponse.jsonPath());
        assertThat(isSameSectionCount(sections.size(), 2)).isTrue();
        assertThat(sections.get(0).getUpStationId()).isEqualTo(hongDaeStation.getId());

    }

    @DisplayName("노선 구간에 새로운 하행 종점을 추가 할 수 있다.")
    @Test
    void addDownStation() {
        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), seungSuStation.getId(), hongDaeStation.getId(), 8L);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findResponse = findSectionByLine(convertLineId(saveResponse.jsonPath()));
        List<SectionResponse> sections = convertSection(findResponse.jsonPath());
        assertThat(isSameSectionCount(sections.size(), 2)).isTrue();
        assertThat(sections.get(sections.size() - 1).getDownStationId()).isEqualTo(hongDaeStation.getId());

    }

    @DisplayName("중복된 노선 구간은 저장할 수 없다.")
    @Test
    void addSectionFailedDuplicate() {
        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), gangNamStation.getId(), seungSuStation.getId(), 8L);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("이미 존재하는 구간 하나 사이에 새로운 구간을 등록 할 수 있다.")
    @Test
    void addSectionMiddleExistOneSection() {
        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), guiStation.getId(), seungSuStation.getId(), 3L);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findResponse = findSectionByLine(convertLineId(saveResponse.jsonPath()));
        List<SectionResponse> sections = convertSection(findResponse.jsonPath());
        assertThat(isSameSectionCount(sections.size(), 2)).isTrue();
        assertThat(isSameDistance(sections.get(0), 7L)).isTrue();
        assertThat(isSameDistance(sections.get(1), 3L)).isTrue();
    }

    @DisplayName("이미 여러 구간이 존재하는 구간 사이에 새로운 구간을 등록 할 수 있다.(현재 구간 강남역-화곡역-성수역-온수역 )")
    @Test
    void addSectionMiddleExistManySection() {
        //given
        createManySection();

        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), guiStation.getId(), seungSuStation.getId(), 3L);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findResponse = findSectionByLine(convertLineId(saveResponse.jsonPath()));
        List<SectionResponse> sections = convertSection(findResponse.jsonPath());
        assertThat(sections.stream().map(SectionResponse::getDistance)).containsExactlyElementsOf(Arrays.asList(1L, 6L, 3L, 1L));

    }

    @DisplayName("이미 존재하는 구간 사이에 새로운 구간을 등록 하려고 하나 기존 구간의 길이보다 크거나(11L) , 같아서 저장 할 수 없다.(10L)")
    @ParameterizedTest
    @ValueSource(longs = {11L, 10L})
    void addSectionMiddleFailedDistance(long distance) {
        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), gangNamStation.getId(), guiStation.getId(), distance);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("새로운 구간을 저장하려고 하나 기존 구간에 부합하는 upStation ,downStation이 존재 하지 않는다.")
    @Test
    void addSectionMiddleNotExistUpAndDownStation() {
        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), hongDaeStation.getId(), guiStation.getId(), 11L);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("삭제하고자 하는 역이 구간에 존재 하지 않아서 삭제 할 수 없다.(기본 강남 - 성수역 구간만 존재)")
    @Test
    void deleteSectionFailedStationNotInclude() {
        //when
        ExtractableResponse<Response> deleteResonse = deleteSection(line.getId(), hongDaeStation);

        //then
        assertThat(deleteResonse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간이 하나인 경우에는 삭제 할 수 없다.(기본 강남 - 성수역 구간만 존재)")
    @Test
    void deleteSectionFailedHasOneSection() {
        //when
        ExtractableResponse<Response> deleteResonse = deleteSection(line.getId(), gangNamStation);

        //then
        assertThat(deleteResonse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행 종점을 삭제 할 수 있다. (강남 - 성수역 - 홍대구간 존재)")
    @Test
    void deleteUpStationTerminus() {
        //given
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), seungSuStation.getId(), hongDaeStation.getId(), 1L);

        //when
        ExtractableResponse<Response> deleteResponse = deleteSection(line.getId(), gangNamStation);

        //then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> findSection = findSectionByLine(convertLineId(saveResponse.jsonPath()));
        List<SectionResponse> sections = convertSection(findSection.jsonPath());
        assertThat(sections).hasSize(1);
        assertDistance(sections, Collections.singletonList(1L));
        assertStationName(sections, Arrays.asList("성수역", "홍대역"));
    }

    @DisplayName("하행 종점을 삭제 할 수 있다. (강남 - 성수역 - 홍대구간 존재)")
    @Test
    void deleteDownStationTerminus() {
        //given
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), seungSuStation.getId(), hongDaeStation.getId(), 1L);

        //when
        ExtractableResponse<Response> deleteResponse = deleteSection(line.getId(), hongDaeStation);

        //then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> findSection = findSectionByLine(convertLineId(saveResponse.jsonPath()));
        List<SectionResponse> sections = convertSection(findSection.jsonPath());
        assertThat(sections).hasSize(1);
        assertDistance(sections, Collections.singletonList(10L));
        assertStationName(sections, Arrays.asList("강남역", "성수역"));
    }

    @DisplayName("중간 구간을 삭제 할 수 있다. (강남 - 성수역 - 홍대구간 존재)")
    @Test
    void deleteMiddleSection() {
        //given
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), seungSuStation.getId(), hongDaeStation.getId(), 1L);

        //when
        ExtractableResponse<Response> deleteResponse = deleteSection(line.getId(), seungSuStation);

        //then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> findSection = findSectionByLine(convertLineId(saveResponse.jsonPath()));
        List<SectionResponse> sections = convertSection(findSection.jsonPath());
        assertDistance(sections, Collections.singletonList(11L));
        assertStationName(sections, Arrays.asList("강남역", "홍대역"));
    }

    @DisplayName("중간 구간을 삭제 할 수 있다. (강남 - 성수역 - 홍대구간- 구의 존재)")
    @Test
    void deleteMiddleSectionManyCase() {
        //given
        createSection(line.getId(), seungSuStation.getId(), hongDaeStation.getId(), 1L);
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), hongDaeStation.getId(), guiStation.getId(), 1L);

        //when
        ExtractableResponse<Response> deleteResponse = deleteSection(line.getId(), hongDaeStation);

        //then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> findSection = findSectionByLine(convertLineId(saveResponse.jsonPath()));
        List<SectionResponse> sections = convertSection(findSection.jsonPath());
        assertDistance(sections, Arrays.asList(10L, 2L));
        assertStationName(sections, Arrays.asList("강남역", "성수역", "구의역"));
    }

    private ExtractableResponse<Response> deleteSection(Long id, Station hongDaeStation) {
        return RestAssured.given().log().all()
                .param(STATION_ID, hongDaeStation.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(String.format(SECTION_MAIN_PATH, id))
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createSection(Long lineId, Long upStationId, Long downStationId, Long distance) {
        Map<String, Object> params = new HashMap<>();
        params.put(LineConstant.UP_STATION, upStationId);
        params.put(LineConstant.DOWN_STATION, downStationId);
        params.put(LineConstant.DISTANCE, distance);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(String.format(SECTION_MAIN_PATH, lineId))
                .then().log().all()
                .extract();
    }

    private List<SectionResponse> convertSection(JsonPath jsonPath) {
        return jsonPath.getList(SECTIONS, SectionResponse.class);
    }

    private Long convertLineId(JsonPath jsonPath) {
        return jsonPath.getLong(LineConstant.LINE_ID);
    }

    private ExtractableResponse<Response> findSectionByLine(Long lineId) {
        return RestAssured.given().log().all()
                .when().get(String.format(SECTION_MAIN_PATH, lineId))
                .then().log().all()
                .extract();
    }

    private boolean isSameDistance(SectionResponse sectionResponse, Long expect) {
        return sectionResponse.getDistance().equals(expect);
    }

    private boolean isSameSectionCount(int sectionCount, int expect) {
        return sectionCount == expect;
    }

    private void createManySection() {
        Station station = stationRepository.save(new Station("화곡역"));
        createSection(line.getId(), gangNamStation.getId(), station.getId(), 1L);
        Station otherStation = stationRepository.save(new Station("온수역"));
        createSection(line.getId(), seungSuStation.getId(), otherStation.getId(), 1L);
    }

    private List<String> convertStationName(List<SectionResponse> responses) {
        LinkedHashSet<String> names = new LinkedHashSet<>();
        responses.forEach(v -> {
            names.add(v.getUpStationName());
            names.add(v.getDownStationName());
        });
        return new ArrayList<>(names);
    }

    private void assertDistance(List<SectionResponse> sections, List<Long> distance) {
        assertThat(sections.stream().map(SectionResponse::getDistance)).containsExactlyElementsOf(distance);
    }

    private void assertStationName(List<SectionResponse> sections, List<String> expectNames) {
        List<String> stationNames = convertStationName(sections);
        assertThat(stationNames).containsExactlyElementsOf(expectNames);
    }
}
