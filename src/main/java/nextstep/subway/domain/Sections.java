package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Set<Station> getStations() {
        return sections.stream()
                        .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                        .collect(Collectors.toSet());
    }

    public void addSection(Station upStation, Station downStation, Integer distance) {
        Section section = new Section(upStation, downStation, distance);
        sections.add(section);
    }

    public void addSection(Section section) {
        if(sections.isEmpty()) {
            sections.add(section);
            return;
        }
        addSectionValid(section);
        updateExistSection(section);
        sections.add(section);
    }

    private boolean addSectionValid(Section section) {
        if(hasUpStationAndDownStation(section)) {
            throw new IllegalArgumentException("");
        }

        if(hasNotUpStationAndDownStation(section)) {
            throw new IllegalArgumentException("");
        }

        return true;
    }

    private boolean hasUpStationAndDownStation(Section newSection) {
        return getStations().contains(newSection.getUpStation()) &&
                getStations().contains(newSection.getDownStation());
    }

    private boolean hasNotUpStationAndDownStation(Section newSection) {
        return !getStations().contains(newSection.getUpStation()) &&
                !getStations().contains(newSection.getDownStation());
    }
    
    //역과 역 사이에 새로운 구간이 생길 때, 기존 구간 변경
    private void updateExistSection(Section newSection) {
        //역과 역사이 (상행이 같을 때)
        sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateUpSection(newSection));

        //역과 역사이 (하행이 같을 때)
        sections.stream()
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateDownSection(newSection));

        //상행 종점, 하행 종점이 새로 추가되는 경우는 기존 수정 필요 X
    }

    public List<Section> getSections() {
        return sections;
    }
}
