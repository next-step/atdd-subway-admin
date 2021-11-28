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

    void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        checkValidStations(section);
        
        for (int i = 0; i < sections.size(); i++) {
            Section nowSection = sections.get(i);
            // 중간에 넣어야함 재조립
            if (nowSection.getUpStation().equals(section.getUpStation())) {
                section.checkShorter(nowSection.getDistance());
                nowSection.moveUpStationTo(section.getDownStation(), section.getDistance());
                sections.add(i, section);
                return;
            }
            // 중간에 넣어야함 재조립
            if (nowSection.getDownStation().equals(section.getDownStation())) {
                section.checkShorter(nowSection.getDistance());
                nowSection.moveDownStationTo(section.getUpStation(), section.getDistance());
                sections.add(i+1, section);
                return;
            }
            // 앞에 넣어야함
            if (nowSection.getUpStation().equals(section.getDownStation()) && i == 0) {
                sections.add(i, section);
                return;
            }
            // 뒤에 넣어야함
            if (nowSection.getDownStation().equals(section.getUpStation()) && i == sections.size()-1) {
                sections.add(section);
                return;
            }
        }
    }
    
    int count() {
        return sections.size();
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
    
    void print() {
        sections.stream().forEach(s -> System.out.println(s.toString()));
    }

}
