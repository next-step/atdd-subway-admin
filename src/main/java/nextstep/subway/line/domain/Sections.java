package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<Section>();

    
    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }
    
    public static Sections from(Section...sections) {
        return new Sections(Stream
                .of(sections)
                .collect(Collectors.toList()));
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }
    
    List<Section> getSections() {
        return sections;
    }
    
    int count() {
        return sections.size();
    }
    
    Section getSectionAt(int index) {
        return getSections().get(index); 
    }
    
    
    Station getStationAt(int index) {
        return getStations().get(index); 
    }

    void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        checkValidStations(section);
        sortedAdd(section);
    }
    
    private void sortedAdd(Section section) {
        for (int i = 0; i < sections.size(); i++) {
            Section oldSection = sections.get(i);
            // 새 구간이 기존 구간보다 먼저 (상행역이 같을때)
            if (addIfSameUpStations(oldSection, section, i)) {
                return;
            }
            // 기존 구간 다음에 새 구간 (하행역이 같을때)
            if (addIfSameDownStations(oldSection, section, i)) {
                return;
            }
            // 맨 앞에 새 구간 (새 구간 하행역 == 기존 구간 상행역)
            if (addIfFirstSection(oldSection, section, i)) {
                return;
            }
            // 맨 뒤에 새 구간 (기존 구간 하행역 == 새 구간 상행역)
            if (addIfLastSection(oldSection, section, i)) {
                return;
            }
        }
    }
    
    private boolean addIfSameUpStations(Section oldSection, Section newSection, int index) {
        if (newSection.isSameUpStation(oldSection.getUpStation())) {
            oldSection.moveUpStationTo(newSection.getDownStation(), newSection.getDistance());
            sections.add(index, newSection);
            return true;
        }
        return false;
    }
    
    private boolean addIfSameDownStations(Section oldSection, Section newSection, int index) {
        if (newSection.isSameDownStation(oldSection.getDownStation())) {
            oldSection.moveDownStationTo(newSection.getUpStation(), newSection.getDistance());
            sections.add(index+1, newSection);
            return true;
        }
        return false;
    }
    
    private boolean addIfFirstSection(Section oldSection, Section newSection, int index) {
        if (newSection.isSameDownStation(oldSection.getUpStation()) && index == 0) {
            sections.add(index, newSection);
            return true;
        }
        return false;
    }
    
    private boolean addIfLastSection(Section oldSection, Section newSection, int index) {
        if (newSection.isSameUpStation(oldSection.getDownStation())&& index == sections.size()-1) {
            sections.add(newSection);
            return true;
        }
        return false;
    }

    private void checkValidStations(Section section) {
        if(isExistStations(section.getUpStation(), section.getDownStation())) {
            throw new IllegalArgumentException(String.format("이미 등록된 노선입니다.(%s-%s)", section.getUpStation().getName(), section.getDownStation().getName()));
        }
        if(isNotExistStations(section.getUpStation(), section.getDownStation())) {
            throw new IllegalArgumentException(String.format("연결할 수 없는 노선입니다.(%s-%s)", section.getUpStation().getName(), section.getDownStation().getName()));
        }
    }
    
    private boolean isExistStations(Station...stations) {
        return Stream.of(stations).allMatch(station -> getStations().contains(station));
    }
    
    private boolean isNotExistStations(Station...stations) {
        return Stream.of(stations).allMatch(station -> !getStations().contains(station));
    }

}
