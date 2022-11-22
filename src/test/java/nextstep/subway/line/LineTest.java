package nextstep.subway.line;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class LineTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void createLine() {
        upStation = stationRepository.save(new Station("강남역"));
        downStation = stationRepository.save(new Station("판교역"));

        line = lineRepository.save(new Line("신분당선", "red", upStation, downStation, 20));
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void setTerminus() {
        // then
        assertThat(line.getUpStation()).isEqualTo(upStation);
        assertThat(line.getDownStation()).isEqualTo(downStation);
    }

    @DisplayName("동일 구간을 추가하면 에러가 발생한다.")
    @Test
    void addSection_동일구간_추가() {
        // given
        Section section = new Section(upStation, downStation, new Distance(20));

        // when, then
        assertThatThrownBy(() -> line.addSection(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선 생성 후 역 사이에 새로운 구간을 추가한다.")
    @Test
    void addSection_구간추가() {
        //given
        Station newStation = stationRepository.save(new Station("광교역"));

        // when
        Section newSection = new Section(newStation, downStation, new Distance(5));
        line.addSection(newSection);

        //then
        assertThat(line.getSections().values()).hasSize(2);
        assertThat(line.getDistance().value()).isEqualTo(20);
    }

    @ParameterizedTest(name = "기존 역 사이 길이보다 크거나 같으면 추가할 수 없음")
    @ValueSource(ints = {20, 21})
    void addSection_구간길이가_크거나_같으면_추가할_수_없다(int distance) {
        //given
        Station newStation = stationRepository.save(new Station("광교역"));

        // when
        Section newSection = new Section(newStation, downStation, new Distance(distance));

        //then
        assertThatThrownBy(() -> line.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSection_상행역_하행역_둘다_포함_안된_경우_추가할_수_없다() {
        //given
        Station newUpStation = stationRepository.save(new Station("서울역"));
        Station newDownStation = stationRepository.save(new Station("시청역"));

        // when
        Section newSection = new Section(newUpStation, newDownStation, new Distance(5));

        //then
        assertThatThrownBy(() -> line.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있으면 추가할 수 없음")
    @Test
    void addSection_상행역_하행역_둘다_포함된_경우_추가할_수_없다() {
        //given
        Section newSection = new Section(upStation, downStation, new Distance(5));

        // when, then
        assertThatThrownBy(() -> line.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선 생성 후 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection_새로운_역을_상행_종점으로() {
        //given
        Station newUpStation = stationRepository.save(new Station("신사역"));

        // when
        Section newSection = new Section(newUpStation, upStation, new Distance(5));
        line.addSection(newSection);

        //then
        assertThat(line.getSections().values()).hasSize(2);
        assertThat(line.getUpStation()).isEqualTo(newUpStation);
        assertThat(line.getDistance().value()).isEqualTo(25);
    }

    @DisplayName("노선 생성 후 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection_새로운_역을_하행_종점으로() {
        //given
        Station newDownStation = stationRepository.save(new Station("신사역"));

        // when
        Section newSection = new Section(downStation, newDownStation, new Distance(5));
        line.addSection(newSection);

        //then
        assertThat(line.getSections().values()).hasSize(2);
        assertThat(line.getDownStation()).isEqualTo(newDownStation);
        assertThat(line.getDistance().value()).isEqualTo(25);
    }
}
