package nextstep.subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.stream.Stream;
import nextstep.subway.domain.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Domain:Line")
class LineTest {

    private Line 신분당선;
    private Station 논현역;
    private Station 정자역;
    private int 노선의_총_구간_길이;

    @BeforeEach
    void setUp() {
        논현역 = new Station("논현역");
        정자역 = new Station("정자역");
        노선의_총_구간_길이 = 100;

        신분당선 = new Line("신분당선", "red", 논현역, 정자역, 노선의_총_구간_길이);
    }

    @Test
    @DisplayName("상행/하행 종점역 구간을 포함한 신규 노선을 생성한다.")
    public void addSection() {
        // Given
        Station finalUpStation = new Station("논현역");
        Station finalDownStation = new Station("정자역");
        int 구간_길이 = 10;

        // When
        Line line = new Line("신분당선", "red", finalUpStation, finalDownStation, 구간_길이);

        // Then
        assertAll(
            () -> assertThat(line.getFinalUpStation()).isEqualTo(finalUpStation),
            () -> assertThat(line.getFinalDownStation()).isEqualTo(finalDownStation),
            () -> assertThat(line.getAllSections()).hasSize(1),
            () -> assertThat(line.getAllStations()).containsExactly(finalUpStation, finalDownStation)
        );
    }

    @Test
    @DisplayName("신규 상행종점역 구간을 추가한다.")
    public void addFinalUpSection() {
        // Given
        final Station 신사역 = new Station("신사역");
        final Station 논현역 = new Station("논현역");
        final int 구간_길이 = 5;

        final Section 신규_상행종점역_구간 = Section.of(신분당선, 신사역, 논현역, 구간_길이);

        // When
        신분당선.addSection(신규_상행종점역_구간);

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(신사역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(정자역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(신규_상행종점역_구간),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(Section.of(신분당선, 논현역, 정자역, 100)),
            () -> assertThat(신분당선.getAllSections()).hasSize(2),
            () -> assertThat(신분당선.getAllStations())
                .as("노선에 포함된 정렬된 지하철 역 목록 조회")
                .containsExactly(신사역, 논현역, 정자역)
        );
    }

    @Test
    @DisplayName("신규 하행종점역 구간을 추가한다.")
    public void addFinalDownSection() {
        // Given
        final Station 정자역 = new Station("정자역");
        final Station 광교역 = new Station("광교역");
        final int 구간_길이 = 5;

        final Section 신규_하행종점역_구간 = Section.of(신분당선, 정자역, 광교역, 구간_길이);

        // When
        신분당선.addSection(신규_하행종점역_구간);

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(논현역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(광교역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(Section.of(신분당선, 논현역, 정자역, 100)),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(신규_하행종점역_구간),
            () -> assertThat(신분당선.getAllSections()).hasSize(2),
            () -> assertThat(신분당선.getAllStations())
                .as("노선에 포함된 정렬된 지하철 역 목록 조회")
                .containsExactly(논현역, 정자역, 광교역)
        );
    }

    @Test
    @DisplayName("노선 중간에 구간을 추가한다. : 상행역이 동일한 경우")
    public void middleInsertionSection_WhenSameUpStation() {
        // Given
        final Station 논현역 = new Station("논현역");
        final Station 신논현역 = new Station("신논현역");
        final int 구간_길이 = 5;

        final Section 상행역이_동일한_구간 = Section.of(신분당선, 논현역, 신논현역, 구간_길이);

        // When
        신분당선.addSection(상행역이_동일한_구간);

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(논현역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(정자역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(Section.of(신분당선, 논현역, 신논현역, 5)),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(Section.of(신분당선, 신논현역, 정자역, 95)),
            () -> assertThat(신분당선.getAllSections()).hasSize(2),
            () -> assertThat(신분당선.getAllStations())
                .as("노선에 포함된 정렬된 지하철 역 목록 조회")
                .containsExactly(논현역, 신논현역, 정자역)
        );
    }

    @Test
    @DisplayName("노선 중간에 구간을 추가한다. : 하행역이 동일한 경우")
    public void middleInsertionSection_WhenSameDownStation() {
        // Given
        final Station 신논현역 = new Station("신논현역");
        final Station 정자역 = new Station("정자역");
        final int 구간_길이 = 5;

        final Section 하행역이_동일한_구간 = Section.of(신분당선, 신논현역, 정자역, 구간_길이);

        // When
        신분당선.addSection(하행역이_동일한_구간);

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(논현역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(정자역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(Section.of(신분당선, 논현역, 신논현역, 95)),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(Section.of(신분당선, 신논현역, 정자역, 5)),
            () -> assertThat(신분당선.getAllSections()).hasSize(2),
            () -> assertThat(신분당선.getAllStations())
                .as("노선에 포함된 정렬된 지하철 역 목록 조회")
                .containsExactly(논현역, 신논현역, 정자역)
        );
    }

    @Test
    @DisplayName("여러 구간 중간에 노드 삽입")
    public void addReal() {
        // Given
        Station 신사역 = new Station("신사역");
        Station 신논현역 = new Station("신논현역");
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 양재시민의숲역 = new Station("양재시민의숲역");
        Station 청계산입구역 = new Station("청계산입구역");
        Station 판교역 = new Station("판교역");
        Station 미금역 = new Station("미금역");

        // When
        신분당선.addSection(Section.of(신분당선, 정자역, 미금역, 5));
        신분당선.addSection(Section.of(신분당선, 논현역, 신논현역, 5));
        신분당선.addSection(Section.of(신분당선, 신논현역, 강남역, 5));
        신분당선.addSection(Section.of(신분당선, 강남역, 양재역, 10));
        신분당선.addSection(Section.of(신분당선, 양재시민의숲역, 정자역, 65));
        신분당선.addSection(Section.of(신분당선, 양재시민의숲역, 청계산입구역, 35));
        신분당선.addSection(Section.of(신분당선, 판교역, 정자역, 5));
        신분당선.addSection(Section.of(신분당선, 신사역, 논현역, 5));

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(신사역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(미금역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(Section.of(신분당선, 신사역, 논현역, 5)),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(Section.of(신분당선, 정자역, 미금역, 5)),
            () -> assertThat(신분당선.getAllSections()).hasSize(9),
            () -> assertThat(신분당선.getTotalDistance())
                .as("신사-5-논현-5-신논현-5-강남-10-양재-15-양재시민의숲-35-청계산입구-25-판교-5-정자-5-미금")
                .isEqualTo(노선의_총_구간_길이 + 5 + 5),
            () -> assertThat(신분당선.getAllStations())
                .as("노선에 포함된 정렬된 지하철 역 목록 조회")
                .containsExactly(신사역, 논현역, 신논현역, 강남역, 양재역, 양재시민의숲역, 청계산입구역, 판교역, 정자역, 미금역)
        );
    }

    @Test
    @DisplayName("동일 구간 추가 시 예외")
    public void throwException_WhenAddSameSection() {
        // Given
        Station 논현역 = new Station("논현역");
        Station 정자역 = new Station("정자역");
        final int 구간_길이 = 5;

        Section 동일구간 = Section.of(신분당선, 논현역, 정자역, 구간_길이);

        // When & Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> 신분당선.addSection(동일구간));
    }

    @Test
    @DisplayName("접점이 없는 역이 포함된 구간 추가 시 예외")
    public void throwException_WhenNotContainsAnyConnectStations() {
        // Given
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        final int 구간_길이 = 5;

        Section 접점이_없는_구간 = Section.of(신분당선, 강남역, 양재역, 구간_길이);

        // When & Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> 신분당선.addSection(접점이_없는_구간));
    }

    /**
     * Given : 기존 `논현-90-강남-10-정자` 구간에
     * When : 유효하지 않은 구간 거리를 가지는 신규 `양재-{distance}-강남`구간을 추가하면
     * Then : 예외가 발생한다.
     */
    @ParameterizedTest
    @MethodSource
    @DisplayName("추가하는 구간의 길이가 연결 구간의 길이보다 크거나 같은 경우 예외")
    public void throwException_WhenAddingSectionsDistanceIsGraterOrEqualsThanConnectedSectionsDistance(
        Section 유효하지_않은_길이를_가지는_구간,
        String givenDescription
    ) {
        // Given
        final Station 강남역 = new Station("강남역");
        final Section 상행역이_동일한_구간 = Section.of(신분당선, 강남역, 정자역, 10);
        신분당선.addSection(상행역이_동일한_구간);

        // When
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> 신분당선.addSection(유효하지_않은_길이를_가지는_구간))
            .as(givenDescription);
    }

    private static Stream throwException_WhenAddingSectionsDistanceIsGraterOrEqualsThanConnectedSectionsDistance() {
        final Line 신분당선 = new Line("신분당선", "red");
        final Station 강남역 = new Station("강남역");
        final Station 양재역 = new Station("양재역");
        return Stream.of(
            Arguments.of(Section.of(신분당선, 강남역, 양재역, 10), "인접 구간의 길이와 추가하는 구간의 길이가 같은 경우"),
            Arguments.of(Section.of(신분당선, 강남역, 양재역, 11), "인접 구간의 길이보다 추가하는 구간의 길이가 큰 경우"),
            Arguments.of(Section.of(신분당선, 강남역, 양재역, 100), "전체 구간의 길이와 추가하는 구간의 길이가 같은 경우"),
            Arguments.of(Section.of(신분당선, 강남역, 양재역, 101), "전체 구간의 길이보다 추가하는 구간의 길이가 큰 경우")
        );
    }
}
