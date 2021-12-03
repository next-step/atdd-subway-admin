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
        List<Station> stations = new ArrayList<Station>();
        Section firstSection = getFirstSection();
        stations.add(firstSection.getUpStation());
        
        Station nextStation = firstSection.getDownStation();
        stations.add(firstSection.getDownStation());
        for (int i = 0; i < sections.size()-1; i++) {
            nextStation =  getNextStation(nextStation);
            stations.add(nextStation);
        }
        
        return stations;
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

    void add(Section otherSection) {
        checkValidStations(otherSection);
        for (Section section : sections) {
            if (addIfSameUpStations(section, otherSection)) {
                return;
            }
            if (addIfSameDownStations(section, otherSection)) {
                return;
            }
        }
        sections.add(otherSection);
    }
    
    void remove(Station station) {
        Section upSection = sections.stream()
                            .filter(section -> section.getDownStation().equals(station))
                            .findFirst()
                            .get();
        
        Section downSection = sections.stream()
                            .filter(section -> section.getUpStation().equals(station))
                            .findFirst()
                            .get();
        
        upSection.combine(downSection);
        sections.removeIf(section -> section.equals(downSection));
    }
    
    private boolean addIfSameUpStations(Section oldSection, Section newSection) {
        if (newSection.isSameUpStation(oldSection.getUpStation())) {
            oldSection.moveUpStationTo(newSection.getDownStation(), newSection.getDistance());
            sections.add(newSection);
            return true;
        }
        return false;
    }
    
    private boolean addIfSameDownStations(Section oldSection, Section newSection) {
        if (newSection.isSameDownStation(oldSection.getDownStation())) {
            oldSection.moveDownStationTo(newSection.getUpStation(), newSection.getDistance());
            sections.add(newSection);
            return true;
        }
        return false;
    }
    
    private void checkValidStations(Section section) {
        if (sections.size() == 0) {
            return;
        }
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
    
    private Section getFirstSection() {
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        
        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("첫 구간이 없습니다."));
    }
    
    private Station getNextStation(Station downStation) {
        return sections.stream()
                .filter(section -> downStation.equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("역이 연결되지 않았습니다."))
                .getDownStation();
    }
    

}
