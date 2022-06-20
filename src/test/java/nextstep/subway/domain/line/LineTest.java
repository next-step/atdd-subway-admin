package nextstep.subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.domain.station.Station;
import org.junit.jupiter.api.BeforeEach;
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

    @Test
    @DisplayName("신규 상행종점역 구간을 추가한다.")
    public void addFinalUpSection() {
        // Given
        Station 신사역 = new Station("신사역");
        Station 논현역 = new Station("논현역");
        int distance = 5;

        Section 신규_상행종점역_구간 = new Section(신분당선, 신사역, 논현역, distance);

        // When
        신분당선.addSection(신규_상행종점역_구간);

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).isEqualTo(신사역),
            () -> assertThat(신분당선.getFinalDownStation()).isEqualTo(정자역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(신규_상행종점역_구간),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(new Section(신분당선, 논현역, 정자역, 5)),
            () -> assertThat(신분당선.getAllSections()).hasSize(2),
            () -> assertThat(신분당선.getAllStations())
                .as("구간 추가 시, 노선에 포함된 역 목록의 정렬 여부 확인")
                .containsExactly(신사역, 논현역, 정자역)
        );
    }

    @Test
    @DisplayName("신규 하행종점역 구간을 추가한다.")
    public void addFinalDownSection() {
        // Given
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");
        int distance = 5;

        Section 신규_하행종점역_구간 = new Section(신분당선, 정자역, 광교역, distance);

        // When
        신분당선.addSection(신규_하행종점역_구간);

        // Then
        assertAll(
            () -> assertThat(신분당선.getFinalUpStation()).isEqualTo(논현역),
            () -> assertThat(신분당선.getFinalDownStation()).isEqualTo(광교역),
            () -> assertThat(신분당선.getFinalUpSection()).isEqualTo(신규_하행종점역_구간),
            () -> assertThat(신분당선.getFinalDownSection()).isEqualTo(new Section(신분당선, 정자역, 광교역, 5)),
            () -> assertThat(신분당선.getAllSections()).hasSize(2),
            () -> assertThat(신분당선.getAllStations())
                .as("구간 추가 시, 노선에 포함된 역 목록의 정렬 여부 확인")
                .containsExactly(논현역, 정자역, 광교역)
        );
    }

    /**
     * `신사역-정자역` 구간에
     * <p>
     * `신사역-신논현역` 구간을 추가하면
     * <p>
     * 두개의 구간이 생성된다.
     * <p>
     * - `신사역-신논현역`,
     * <p>
     * - `신논현역-정자역`
     */
    @Test
    @DisplayName("접점이 없는 역이 포함된 구간 추가 시 예외")
    public void addSectionInMiddle() {
        // Given

        // When

        // Then
    }

    @Test
    @DisplayName("노선의 상행종점역을 조회한다.")
    public void findUpStation() {
        // Given

        // When

        // Then
    }

    @Test
    @DisplayName("노선의 하행종점역을 조회한다.")
    public void findDownStation() {
        // Given

        // When

        // Then
    }

    @Test
    @DisplayName("노선에 포함된 지하철 역 목록을 조회한다.")
    public void getStations() {
        // Given

        // When

        // Then
    }

    @Test
    @DisplayName("노선의 신규 상행종점역 구간을 추가한다.")
    public void addNewUpStationSection() {
        // Given

        // When

        // Then
    }

    @Test
    @DisplayName("노선의 신규 하행종점역 구간을 추가한다.")
    public void addNewDownStationSection() {
        // Given

        // When

        // Then
    }

    @Test
    @DisplayName("노선의 구간 목록을 조회한다.")
    public void getSections() {
        // Given

        // When

        // Then
    }

    @Test
    @DisplayName("노선의 전체 길이를 조회한다.")
    public void getDistance() {
        // Given

        // When

        // Then
    }
}
