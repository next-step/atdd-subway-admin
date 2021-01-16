package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

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

    public void changeLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
            int newDistance = 0;
            Section sectionBetweenUpstation = findIfSameUpstation(section);
            Section sectionBetweenDownstation = findIfSameDownstation(section);
           if (sectionBetweenUpstation != null) {
                int count = sections.indexOf(sectionBetweenUpstation);

                newDistance = sectionBetweenUpstation.getDistance() - section.getDistance();
                sections.remove(count);
                sections.add(count, new Section(sectionBetweenUpstation.getUpStation(), section.getDownStation(), section.getDistance()));
                sections.add(count+1, new Section(section.getDownStation(), sectionBetweenUpstation.getDownStation(), newDistance));
            }

            if (sectionBetweenDownstation != null) {
                int count = sections.indexOf(sectionBetweenDownstation);

                newDistance = sectionBetweenDownstation.getDistance() - section.getDistance();
                sections.remove(count);
                sections.add(count, new Section(sectionBetweenDownstation.getUpStation(), section.getUpStation(), newDistance));
                sections.add(count+1, new Section(section.getUpStation(), sectionBetweenDownstation.getDownStation(), section.getDistance()));

            }
            addSectionRestCaes(section, sectionBetweenUpstation, sectionBetweenDownstation);
            section.setLine(this);
    }

    private void addSectionRestCaes(Section section, Section sectionBetweenUpstation, Section sectionBetweenDownstation) {
        if (sectionBetweenUpstation == null && sectionBetweenDownstation == null) {
            addNewSectionBased(section);
        }

        if (sections.isEmpty()) {
            sections.add(section);
        }
    }

    private boolean addNewSectionBased(Section section) {
        for (Section sectionValue: sections) {
            if (sectionValue.getUpStation() == section.getUpStation() && sectionValue.getDownStation() == section.getDownStation()) {
                throw new IllegalArgumentException("새로운 구간은 기존 구간과 같을 수 없습니다!");
            }

            if (sectionValue.getUpStation() == section.getDownStation()) {
                sections.add(sections.indexOf(sectionValue), new Section(section.getUpStation(), section.getDownStation(), section.getDistance()));
                return true;
            }

            if (sectionValue.getDownStation() == section.getUpStation()) {
                sections.add(sections.indexOf(sectionValue)+1, new Section(section.getUpStation(), section.getDownStation(), section.getDistance()));
                return true;
            }
        }
        return false;
    }

    private Section findIfSameUpstation(Section section) {
        for (Section sectionUpstation: sections) {
            if (sectionUpstation.getUpStation() == section.getUpStation() && sectionUpstation.getDownStation() != section.getDownStation() && sectionUpstation.getDistance() > section.getDistance()) {
                return sectionUpstation;
            }
        }
        return null;
    }

    private Section findIfSameDownstation(Section section) {
        for (Section sectionDownstation: sections) {
            if (sectionDownstation.getDownStation() == section.getDownStation() && sectionDownstation.getUpStation() != section.getUpStation() && sectionDownstation.getDistance() > section.getDistance()) {
                return sectionDownstation;
            }
        }
        return null;
    }

    public void deleteSection(Long stationId) {
        if (sections.size() == 1) {
            occureException(stationId);
        }

        if (sections.size() > 1) {
            deleteStation(stationId);
        }
    }

    private void deleteStation(Long stationId) {
        for (Section targetSection:sections) {
            executeRestructorSection(stationId, targetSection);
        }
    }

    private void executeRestructorSection(Long stationId, Section targetSection) {
        int count;
        int addDistance;
        Long downStation;
        if (targetSection.getDownStation() == stationId) {
            count = sections.indexOf(targetSection);
            addDistance = sections.get(count).getDistance() + sections.get(count+1).getDistance();
            downStation = sections.get(count+1).getDownStation();
            sections.remove(count+1);
            targetSection.changeSection(downStation, addDistance);
        }
    }

    private void occureException(Long stationId) {
        if (sections.get(0).getUpStation() == stationId || sections.get(0).getDownStation() == stationId) {
            throw new IllegalArgumentException("구간이 하나인 지하철역은 삭제할 수 없습니다!");
        }
    }

    public Long getId() { return id; }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections;
    }
}

