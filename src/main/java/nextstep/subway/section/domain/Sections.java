package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new LinkedList<>();
    //TODO (3단계) 구간 추가시 순서대로 넣기
    public void add(Section section) {
        this.sections.add(section);
    }

    private Station getFirstStation() {
        return sections.get(0).getUpStation();
    }

    //TODO (3단계) section으로 부터 상행역 하행역 순으로 리스트 저장하기
    public List<Station> getSortedStations() {
        List<Station> stationList = new ArrayList<>();
        stationList.add(getFirstStation());
        for (Section section : sections) {
            stationList.add(section.getDownStation());
        }
        return stationList;
    }
}
