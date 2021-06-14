package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Station downStation;

    @Embedded
    private Distance distance;

    public Section() {
    }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.valueOf(distance);
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public boolean isInFrontOf(Section section) {
        return this.downStation.compareName(section.upStation());
    }

    protected boolean isInMidFrontOf(Section section) {
        return this.upStation.compareName(section.upStation());
    }

    protected boolean isInMidRearOf(Section section) {
        return this.downStation.compareName(section.downStation());
    }

    protected boolean isBehindOf(Section section) {
        return this.upStation.compareName(section.downStation());
    }

    protected void handleAttributesOfFrontSection(List<Section> sections, Section sectionIn) {
        sections.stream()
                .filter(it -> it.downStationName().equals(sectionIn.upStationName()))
                .findAny()
                .ifPresent(it -> it.handleAttributesToConnectInFrontOf(this));
    }

    protected void handleAttributesOfBackSection(List<Section> sections, Section section) {
        sections.stream()
                .filter(it -> it.upStationName().equals(section.downStationName()))
                .findAny()
                .ifPresent(it -> it.handleAttributesToConnectBehindOf(this));
    }

    protected void handleAttributesToConnectBehindOf(Section section) {
        this.shortenDistance(section.distance());
        this.upStation = section.downStation();
    }

    public void handleAttributesToConnectInFrontOf(Section section) {
        this.shortenDistance(section.distance());
        this.downStation = section.upStation();
    }

    public void handleAttributesToDeleteInFrontOf(Section section) {
        this.addDistance(section.distance());
        this.downStation = section.upStation();
    }

    public void handleAttributesToDeleteOnTail(Section section) {
        this.addDistance(section.distance());
        this.downStation = section.downStation();
    }

    private void shortenDistance(int distance) {
        this.distance.shorten(distance);
    }

    private void addDistance(int distance) {
        this.distance.add(distance);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public List<Station> upDownStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Long upStationId() {
        return upStation.getId();
    }

    public Long downStationId() {
        return downStation.getId();
    }

    public Long lineId() {
        return line.getId();
    }

    public String upStationName() {
        return upStation.getName();
    }

    public String downStationName() {
        return downStation.getName();
    }

    public boolean bothStationsAreAlreadyIn(List<Station> stations) {
        return stations.contains(this.upStation)
                && stations.contains(this.downStation);
    }

    public boolean bothStationsAreNotIn(List<Station> stations) {
        return !stations.contains(this.upStation)
                && !stations.contains(this.downStation);
    }

    public int distance() {
        return distance.get();
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void setDistance(int distance) {
        this.distance.set(distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }

    public void setDownStation(Station station) {
        downStation = station;
    }

    public boolean upStationIsEqualsWith(Station station) {
        return this.upStation.equals(station);
    }

    public boolean downStationIsEqualsWith(Station station) {
        return this.downStation.equals(station);
    }

}
