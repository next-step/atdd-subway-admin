package nextstep.subway.domain;

import nextstep.subway.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 구간 도메인 테스트")
@SpringBootTest
class SectionsTest {
    @Autowired
    DatabaseCleaner databaseCleaner;
    @Autowired
    LineRepository lineRepository;
    @Autowired
    StationRepository stationRepository;

    Line 신분당선;
    Station 강남역, 광교역, 판교역;
    Sections 신분당선_구간;

    @BeforeEach
    void setup() {
        신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600"));
        강남역 = stationRepository.save(new Station("강남역"));
        광교역 = stationRepository.save(new Station("광교역"));
        판교역 = stationRepository.save(new Station("판교역"));
        신분당선.addSection(new Section(강남역.getId(), 광교역.getId(), 1000));
        신분당선.addSection(new Section(강남역.getId(), 판교역.getId(), 500));
        신분당선_구간 = 신분당선.getSections();
    }

    @AfterEach
    void clean() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("상행선 기준으로 가운데역이 등록되는지 검증")
    void addBetweenUpStation() {
        Station 정자역 = stationRepository.save(new Station("정자역"));
        신분당선_구간.add(new Section(판교역.getId(), 정자역.getId(), 100));
        assertThat(신분당선_구간.toLineStationIds()).contains(정자역.getId());
    }

    @Test
    @DisplayName("하행선 기준으로 가운데역이 등록되는지 검증")
    void addBetweenDownStation() {
        Station 양재역 = stationRepository.save(new Station("양재역"));
        신분당선_구간.add(new Section(양재역.getId(), 판교역.getId(), 100));
        assertThat(신분당선_구간.toLineStationIds()).contains(양재역.getId());
    }

    @Test
    @DisplayName("신규 상행 종점역 추가")
    void addNewEndUpStation() {
        Station 신논현역 = stationRepository.save(new Station("신논현역"));
        신분당선_구간.add(new Section(신논현역.getId(), 강남역.getId(), 100));
        assertThat(신분당선_구간.toLineStationIds().get(0)).isEqualTo(신논현역.getId());
    }

    @Test
    @DisplayName("신규 하행 종점역 추가")
    void addNewEndDownStation() {
        Station 호매실역 = stationRepository.save(new Station("호매실역"));
        신분당선_구간.add(new Section(광교역.getId(), 호매실역.getId(), 100));
        assertThat(신분당선_구간.toLineStationIds().get(신분당선_구간.toLineStationIds().size() - 1)).isEqualTo(호매실역.getId());
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 501})
    @DisplayName("기존 구간과 같거나 긴 구간을 등록할 경우 오류 반환")
    void errorInvalidDistance(int distance) {
        Station 정자역 = stationRepository.save(new Station("정자역"));
        assertThatThrownBy(() -> 신분당선_구간.add(new Section(판교역.getId(), 정자역.getId(), distance)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("이미 등록된 구간을 요청할 경우 오류 반환")
    void errorExistSection() {
        assertThatThrownBy(() -> 신분당선_구간.add(new Section(강남역.getId(), 판교역.getId(), 100)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("알수 없는 구간을 요청할 경우 오류 반환")
    void errorUnknownSection() {
        Station 선릉역 = stationRepository.save(new Station("선릉역"));
        Station 삼성역 = stationRepository.save(new Station("삼성역"));
        assertThatThrownBy(() -> 신분당선_구간.add(new Section(선릉역.getId(), 삼성역.getId(), 100)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
