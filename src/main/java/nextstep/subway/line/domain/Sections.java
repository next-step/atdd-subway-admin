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
        if (sections.size() == 0) {
            sections.add(section);
            return;
        }
        if(isExistStations(section.getUpStation(), section.getDownStation())) {
            throw new IllegalArgumentException(String.format("이미 등록된 노선입니다.(%s-%s)", section.getUpStation().getName(), section.getDownStation().getName()));
        }
        if(isNotExistStations(section.getUpStation(), section.getDownStation())) {
            throw new IllegalArgumentException(String.format("연결할 수 없는 노선입니다.(%s-%s)", section.getUpStation().getName(), section.getDownStation().getName()));
        }
        List<Section> newSections = new ArrayList<Section>();
        for (int i = 0; i < sections.size(); i++) {
            Section s = sections.get(i);
            newSections.add(s);
            // 중간에 넣어야함 재조립
            if (s.getUpStation().equals(section.getUpStation())) {
                section.checkShorter(s.getDistance());
                newSections.set(i, section);
                Section newSection = Section.of(s.getLine(), section.getDownStation(), s.getDownStation(), s.getDistance()-section.getDistance());
                newSections.add(newSection);
            }
            // 중간에 넣어야함 재조립
            if (s.getDownStation().equals(section.getDownStation())) {
                section.checkShorter(s.getDistance());
                Section newSection = Section.of(s.getLine(), s.getUpStation(), section.getUpStation(), s.getDistance()-section.getDistance());
                newSections.set(i, newSection);
                newSections.add(section);
            }
            // 앞에 넣어야함
            if (s.getUpStation().equals(section.getDownStation())) {
                if (i == 0) {
                    newSections.clear();
                    newSections.add(section);
                    newSections.addAll(sections);
                    new Sections(newSections);
                    return;
                }
                section.checkShorter(s.getDistance());
                newSections.set(i, section);
                Section newSection = Section.of(s.getLine(), section.getDownStation(), s.getDownStation(), s.getDistance()-section.getDistance());
                newSections.add(newSection);
            }
            // 뒤에 넣어야함
            if (s.getDownStation().equals(section.getUpStation())) {
                if (i == sections.size()-1) {
                    sections.add(section);
                    return;
                }
                section.checkShorter(s.getDistance());
                Section newSection = Section.of(s.getLine(), s.getUpStation(), section.getUpStation(), s.getDistance()-section.getDistance());
                newSections.set(i, newSection);
                newSections.add(section);
            }
            
            if (i == sections.size()-1) {
                new Sections(newSections);
                return;
            }
            
        }
    }
    
    int count() {
        return sections.size();
    }
    
    private boolean isExistStations(Station...stations) {
        return Stream.of(stations).allMatch(station -> getStations().contains(station));
    }
    
    private boolean isNotExistStations(Station...stations) {
        return Stream.of(stations).allMatch(station -> !getStations().contains(station));
    }

}
