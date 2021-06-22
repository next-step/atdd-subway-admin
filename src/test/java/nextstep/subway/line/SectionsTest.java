package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.exception.CanNotAddSectionException;
import nextstep.subway.exception.LimitDistanceException;
import nextstep.subway.exception.RegisteredSectionException;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;

public class SectionsTest {

    private Station 강남역 = new Station("강남역");
    private Station 양재역 = new Station("양재역");
    private Station 양재시민의숲 = new Station("양재시민의 숲");
    private Station 청계산입구 = new Station("청계산 입구");

    @DisplayName("노선 구간 등록 : 중간(상행역이 일치하는 경우)")
    @Test
    void addSectionInMiddle(){
        Sections sections = new Sections();
        sections.add(new Section(강남역, 청계산입구, 10));
        sections.add(new Section(강남역, 양재역, 4));
        sections.add(new Section(양재역, 양재시민의숲, 4));
        assertThat(sections.stations()).containsExactly(강남역, 양재역, 양재시민의숲, 청계산입구);
    }

    @DisplayName("노선 구간 등록 : 중간(하행역이 일치하는 경우)")
    @Test
    void addSectionInMiddle2(){
        Sections sections = new Sections();
        sections.add(new Section(강남역, 청계산입구, 10));
        sections.add(new Section(양재역, 청계산입구, 4));
        sections.add(new Section(양재시민의숲, 청계산입구, 2));
        assertThat(sections.stations()).containsExactly(강남역, 양재역, 양재시민의숲, 청계산입구);
    }

    @DisplayName("노선 구간 등록 : 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionInFirst(){
        Sections sections = new Sections();
        sections.add(new Section(양재역, 청계산입구, 10));
        sections.add(new Section(강남역, 양재역, 4));
        assertThat(sections.stations()).containsExactly(강남역, 양재역, 청계산입구);
    }

    @DisplayName("노선 구간 등록 : 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionInLast(){
        Sections sections = new Sections();
        sections.add(new Section(강남역, 양재시민의숲, 10));
        sections.add(new Section(양재시민의숲, 청계산입구, 4));
        assertThat(sections.stations()).containsExactly(강남역, 양재시민의숲, 청계산입구);
    }

    @DisplayName("노선 구간 등록 에러 : 이미 등록된 구간을 등록할 경우")
    @Test
    void addSectionThrowRegisteredSectionException(){
        Sections sections = new Sections();
        sections.add(new Section(강남역, 양재시민의숲, 10));
        assertThatThrownBy(() -> sections.add(new Section(강남역, 양재시민의숲, 4)))
            .isInstanceOf(RegisteredSectionException.class);
    }

    @DisplayName("노선 구간 등록 에러 : 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionThrowCanNotAddSectionException(){
        Sections sections = new Sections();
        sections.add(new Section(강남역, 청계산입구, 10));
        assertThatThrownBy(() -> sections.add(new Section(양재역, 양재시민의숲, 4)))
            .isInstanceOf(CanNotAddSectionException.class);
    }

    @DisplayName("노선 구간 등록 에러 : 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSectionThrowLimitDistanceException(){
        Sections sections = new Sections();
        sections.add(new Section(강남역, 청계산입구, 10));
        assertThatThrownBy(() -> sections.add(new Section(양재역, 청계산입구, 11)))
            .isInstanceOf(LimitDistanceException.class)
            .hasMessage("거리가 기준 거리 이하가 될 수 없습니다. (기준 거리 : " + Section.MIN_DISTANCE + ")");
        assertThatThrownBy(() -> sections.add(new Section(강남역, 양재역, 10)))
            .isInstanceOf(LimitDistanceException.class)
            .hasMessage("거리가 기준 거리 이하가 될 수 없습니다. (기준 거리 : " + Section.MIN_DISTANCE + ")");
    }
}
