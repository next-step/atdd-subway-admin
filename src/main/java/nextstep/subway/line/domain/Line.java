package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.exception.IncorrectSectionException;
import nextstep.subway.line.dto.Stations;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Section section) {
        section.setLine(this);
        sections.add(section);
    }
    public void addAdditionalSection(Section section){
        section.setLine(this);
        Stations upStations = sections.getUpStations();
        Stations downStations = sections.getDownStations();
        if (upStations.contains(section.getUpStation()) && downStations.contains(section.getDownStation())) {
            throw new IncorrectSectionException("이미 역들이 등록된 구간입니다");
        }
        if (upStations.contains(section.getUpStation())) {
            sections.addInMiddle(section);
            return;
        }
        if (upStations.contains(section.getDownStation())) {
            sections.addOnTop(section);
            return;
        }
        if (downStations.contains(section.getUpStation())) {
            sections.addBelow(section);
            return;
        }
        throw new IncorrectSectionException("노선에 없는 역의 구간은 추가할 수 없습니다.");
    }

    public void deleteSectioByStation(Station station) {
        sections.Deletable();
        sections.sort();
        Stations upStations = sections.getUpStations();
        Stations downStations = sections.getDownStations();
        if (upStations.contains(station) && downStations.contains(station)) {
            sections.deleteMiddleSectionBy(station);
            return;
        }
        if (upStations.contains(station)) {
            sections.deleteFirstSectionBy(station);
            return;
        }
        if (downStations.contains(station)) {
            sections.deleteLastSectionBy(station);
            return;
        }
        throw new IncorrectSectionException("노선에 없는 역으로 삭제할 수 없습니다.");
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Stations getStations() {
        return sections.getStations();
    }


}
