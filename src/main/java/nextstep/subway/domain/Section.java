package nextstep.subway.domain;

import nextstep.subway.constants.ErrorCode;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Section extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    private Long distance;

    protected Section() {}

    public static Section of(Station upStation, Station downStation, long distance) {
        return new Section(upStation, downStation, distance);
    }

    private Section(Station upStation, Station downStation, long distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void belongLine(Line line) {
        this.line = line;
    }

    public Line getLine() {
        return this.line;
    }

    public String findUpStationName() {
        return upStation.getName();
    }

    public String findDownStationName() {
        return downStation.getName();
    }

    public boolean isSameUpStationId(Section section) {
        return upStation.equals(section.getUpStation());
    }

    public boolean isSameDownStationId(Section section) {
        return downStation.equals(section.getDownStation());
    }

    public boolean isSameDownStationId(Station station) {
        return this.downStation.equals(station);
    }

    public boolean isSameUpStationId(Station station) {
        return this.upStation.equals(station);
    }

    public void updateAndCreateTwiceSectionWhenUpStationSame(Section newSection) {
        checkDistanceValidation(this.distance, newSection.distance);
        this.modifyUpStation(newSection.downStation);

        this.modifyDistance(this.distance, newSection.distance);
    }

    public void updateAndCreateTwiceSectionWhenDownStationSame(Section newSection) {
        checkDistanceValidation(this.distance, newSection.distance);
        this.modifyDownStation(newSection.upStation);

        newSection.modifyDistance(this.distance, newSection.distance);
    }

    private void modifyDownStation(Station tempStation) {
        this.downStation = tempStation;
    }

    private void modifyUpStation(Station downStation) {
        this.upStation = downStation;
    }

    private void modifyDistance(long existingDistance, Long distance) {
        this.distance = existingDistance - distance;
    }

    private void checkDistanceValidation(Long existingDistance, Long newDistance) {
        if (existingDistance <= newDistance) {
            throw new IllegalArgumentException(ErrorCode.ADD_SECTION_DISTANCE_EXCEPTION.getErrorMessage());
        }
    }

    public Long getDistance() {
        return distance;
    }

    public long findUpStationId() {
        return upStation.getId();
    }

    public long findDownStationId() {
        return downStation.getId();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void addStations(List<Station> stations) {
        stations.add(upStation);
        stations.add(downStation);
    }

    public List<Station> toStations() {
        return Arrays.asList(upStation, downStation);
    }

    public void addNextStation(List<Station> stations) {
        stations.add(downStation);
    }

    public boolean isEqualUpStationNewSectionDownStation(Section section) {
        return upStation.equals(section.getDownStation());
    }

    public boolean isEqualDownStationNewSectionUpStation(Section section) {
        return downStation.equals(section.getUpStation());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(line, section.line) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, line, distance);
    }
}
