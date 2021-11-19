package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

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

    public void addSection(Station upStation, Station downStation, int distance) {

        if (sections.isEmpty()) {
            addSectionToList(upStation, downStation, distance);
            return;
        }
        Section section = new Section(this, upStation, downStation, distance);
        if (sections.isContainsSection(section)) {
            throw new IllegalArgumentException("이미 등록된 Section 입니다.");
        }

        boolean isUpStationSame = sections.isAnyMatchUpStation(upStation);
        boolean isDownStationSame = sections.isAnyMatchDownStation(downStation);

//        if (isDownStationSame && isUpStationSame) {
//            throw new IllegalArgumentException("이미 등록된 Section 입니다.");
//        }
        if(!getStations().contains(upStation) && !getStations().contains(downStation) ){
            throw new IllegalArgumentException("상행, 하행 모두 등록되지 않은 역입니다.");
        }
        addNewEndUpStation(upStation, downStation, distance);
        addNewEndDownStation(upStation, downStation, distance);

        if (isUpStationSame) {
            upStationSameAndDownStationDiff(upStation, downStation, distance);
        }
        if (isDownStationSame) {
            downStationSameAndUpStationDiff(upStation, downStation, distance);
        }
    }

    private void addNewEndUpStation(Station upStation, Station downStation, int distance) {
        boolean isNewEndUpStation = sections.isNewEndUpStation(downStation);
        if (isNewEndUpStation) {
            addSectionToList(0, upStation, downStation, distance);
        }
    }

    private void addNewEndDownStation(Station upStation, Station downStation, int distance) {
        boolean isNewEndDownStation = sections.isNewEndDownStation(upStation);
        if (isNewEndDownStation) {
            addSectionToList(upStation, downStation, distance);
        }
    }

    private void addSectionToList(int index, Station upStation, Station downStation, int distance) {
        sections.add(index, new Section(this, upStation, downStation, distance));
        sections.indexOrders();
    }

    private void addSectionToList( Station upStation, Station downStation, int distance) {
        sections.add(new Section(this, upStation, downStation, distance));
        sections.indexOrders();
    }

    private void upStationSameAndDownStationDiff(Station upStation, Station downStation, int distance) {
        Section findSection = sections.findUpStationSame(upStation);

        if (findSection.getDistance() <= distance) {
            throw new IllegalArgumentException("등록하려는 구간 길이가 더 큽니다.");
        }
        addSectionToList(sections.getSections().indexOf(findSection) + 1, downStation, findSection.getDownStation(), distance);
        findSection.updateDownStation(downStation, findSection.getDistance() - distance);
    }

    private void downStationSameAndUpStationDiff(Station upStation, Station downStation, int distance) {
        Section findSection = sections.findDownStationSame(downStation);

        if (findSection.getDistance() <= distance) {
            throw new IllegalArgumentException("등록하려는 구간 길이가 더 큽니다.");
        }
        addSectionToList(sections.getSections().indexOf(findSection), findSection.getUpStation(), upStation, distance);
        findSection.updateUpStation(upStation, findSection.getDistance() - distance);
    }
    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public List<Station> getStations() {

        return sections.getStations();
    }

    public void deleteSection(Station station) {

        if(!getStations().contains(station)){
            throw new IllegalArgumentException("등록되지 않은 역입니다.");
        }
        if (sections.size() == 1) {
            throw new IllegalArgumentException("구간이 하나만 있을 경우 제거할 수 없습니다.");
        }
        if (isDeleteEndUpStation(station)) {
            return;
        }
        if (isDeleteEndDownStation(station)) {
            return;
        }
        deleteBetweenSection(station);
    }

    private boolean isDeleteEndUpStation(Station station) {
        boolean isEndUpStation = sections.isNewEndUpStation(station);
        if (isEndUpStation) {
            sections.remove(0);
            sections.indexOrders();
            return true;
        }
        return false;
    }

    private boolean isDeleteEndDownStation(Station station) {
        boolean isEndDownStation = sections.isNewEndDownStation(station);
        if (isEndDownStation) {
            sections.remove(sections.size() - 1);
            sections.indexOrders();
            return true;
        }
        return false;
    }

    private void deleteBetweenSection(Station station) {

        Section downStationSame = sections.findDownStationSame(station);
        Section upStationSame = sections.findUpStationSame(station);
        Section newSection = new Section(this,
                downStationSame.getUpStation(),
                upStationSame.getDownStation(),
                downStationSame.getDistance() + upStationSame.getDistance());
        sections.remove(downStationSame);
        sections.remove(upStationSame);
        sections.add(newSection);
        sections.indexOrders();

    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                '}';
    }
}
