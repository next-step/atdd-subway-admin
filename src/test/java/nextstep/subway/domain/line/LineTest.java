package nextstep.subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.domain.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Domain:Line")
class LineTest {

    private Line 신분당선;
    private Station 논현역;
    private Station 정자역;

    @BeforeEach
    void setUp() {
        논현역 = new Station("논현역");
        정자역 = new Station("정자역");
        int distance = 10;

        // When
        신분당선 = new Line("신분당선", "red", 논현역, 정자역, distance);
    }

    @Test
    @DisplayName("상행/하행 종점역 구간을 포함한 신규 노선을 생성한다.")
    public void addSection() {
        // Given
        Station finalUpStation = new Station("논현역");
        Station finalDownStation = new Station("정자역");
        int distance = 10;

        // When
        Line line = new Line("신분당선", "red", finalUpStation, finalDownStation, distance);

        // Then
        assertThat(line.getFinalUpStation()).isEqualTo(finalUpStation);
        assertThat(line.getFinalDownStation()).isEqualTo(finalDownStation);
        assertThat(line.getAllSections()).hasSize(1);
        assertThat(line.getAllStations()).containsExactly(finalUpStation, finalDownStation);
    }

    //        논현 - 정자
    // 신사 - 논현
    // 논현 - 정자
    //        정자 - 광교
    @Test
    @DisplayName("신규 상행종점역 구간을 추가한다.")
    public void addFinalUpSection() {
        // Given
        final Station 신사역 = new Station("신사역");
        final Station 논현역 = new Station("논현역");
        final int distance = 5;

        final Section 신규_상행종점역_구간 = new Section(신분당선, 신사역, 논현역, distance);

        // When
        신분당선.addSection(신규_상행종점역_구간);

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(신사역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(정자역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(신규_상행종점역_구간),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(new Section(신분당선, 논현역, 정자역, 5)),
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
        final int distance = 5;

        final Section 신규_하행종점역_구간 = new Section(신분당선, 정자역, 광교역, distance);

        // When
        신분당선.addSection(신규_하행종점역_구간);

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(논현역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(광교역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(신규_하행종점역_구간),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(new Section(신분당선, 정자역, 광교역, 5)),
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
        final int distance = 5;

        final Section 상행역이_동일한_구간 = new Section(신분당선, 논현역, 신논현역, distance);

        // When
        신분당선.addSection(상행역이_동일한_구간);

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(논현역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(정자역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(new Section(신분당선, 논현역, 신논현역, 5)),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(new Section(신분당선, 신논현역, 정자역, 5)),
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
        final int distance = 5;

        final Section 하행역이_동일한_구간 = new Section(신분당선, 신논현역, 정자역, distance);

        // When
        신분당선.addSection(하행역이_동일한_구간);

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(논현역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(정자역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(new Section(신분당선, 논현역, 신논현역, 5)),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(new Section(신분당선, 신논현역, 정자역, 5)),
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
        신분당선.addSection(new Section(신분당선, 정자역, 미금역, 5));
        신분당선.addSection(new Section(신분당선, 논현역, 신논현역, 5));
        신분당선.addSection(new Section(신분당선, 신논현역, 강남역, 5));
        신분당선.addSection(new Section(신분당선, 양재역, 정자역, 5));
        신분당선.addSection(new Section(신분당선, 양재시민의숲역, 정자역, 5));
        신분당선.addSection(new Section(신분당선, 양재시민의숲역, 청계산입구역, 5));
        신분당선.addSection(new Section(신분당선, 판교역, 정자역, 5));
        신분당선.addSection(new Section(신분당선, 신사역, 논현역, 5));

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).as("노선의 상행종점역 조회").isEqualTo(신사역),
            () -> assertThat(신분당선.getFinalDownStation()).as("노선의 하행종점역 조회").isEqualTo(미금역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(new Section(신분당선, 신사역, 논현역, 5)),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(new Section(신분당선, 정자역, 미금역, 5)),
            () -> assertThat(신분당선.getAllSections()).hasSize(9),
            () -> assertThat(신분당선.getAllStations())
                .as("노선에 포함된 정렬된 지하철 역 목록 조회")
                .containsExactly(신사역, 논현역, 신논현역, 강남역, 양재역, 양재시민의숲역, 청계산입구역, 판교역, 정자역, 미금역)
        );
    }

    @Disabled
    @Test
    @DisplayName("동일 구간 추가 시 예외")
    public void throwException_WhenAddSameSection() {
        // Given

        // When

        // Then
    }

    @Disabled
    @Test
    @DisplayName("접점이 없는 역이 포함된 구간 추가 시 예외")
    public void throwException_WhenNotContainsAnyConnectStations() {
        // Given

        // When

        // Then
    }

    @Disabled
    @Test
    @DisplayName("추가하는 구간의 길이가 연결 구간의 길이보다 크거나 같은 경우 예외")
    public void throwException_WhenAddingSectionsDistanceIsGraterOrEqualsThanConnectedSectionsDistance() {
        // Given

        // When

        // Then
    }

    @Disabled
    @Test
    @DisplayName("노선의 전체 길이를 조회한다.")
    public void getDistance() {
        // Given

        // When

        // Then
    }
}
