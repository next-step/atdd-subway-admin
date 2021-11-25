package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Embeddable
public class Sections {
    private static final String NOT_NULL_ERROR_MESSAGE = "종점역은 빈값이 될 수 없습니다.";
    private static final String ALREADY_BOTH_REGISTERED_ERROR_MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.";
    private static final String NOT_EXIST_EITHER_STATION_ERROR_MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어 있지 않습니다.";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "lineId", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(Section section) {
        validateNotNull(section);
        this.sections.add(section);
    }

    private void validateNotNull(Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException(NOT_NULL_ERROR_MESSAGE);
        }
    }

    public void addSection(final Section section) {

        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();

        final boolean isUpStationExisted = isStationExist(upStation);
        final boolean isDownStationExisted = isStationExist(downStation);

        validateStationExist(isDownStationExisted, isUpStationExisted);

        if (isDownStationExisted) {
            updateDownStation(section);
        }

        if (isUpStationExisted) {
            updateUpStation(section);
        }

        this.sections.add(section);
    }

    private void updateUpStation(Section section) {
        sections.stream()
                .filter(it -> it.getUpStation() == section.getUpStation())
                .findFirst()
                .ifPresent(it -> it.updateUpStation(section.getDownStation(), section.getDistance()));
    }

    private void updateDownStation(Section section) {
        sections.stream()
                .filter(it -> it.getDownStation() == section.getDownStation())
                .findFirst()
                .ifPresent(it -> it.updateDownStation(section.getUpStation(), section.getDistance()));
    }

    private boolean isStationExist(Station station) {
        return getStations().stream()
                .anyMatch(it -> it.equals(station));
    }

    private void validateStationExist(boolean isDownStationExisted, boolean isUpStationExisted) {
        if (isDownStationExisted && isUpStationExisted) {
            throw new IllegalArgumentException(ALREADY_BOTH_REGISTERED_ERROR_MESSAGE);
        }

        if (!isDownStationExisted && !isUpStationExisted) {
            throw new IllegalArgumentException(NOT_EXIST_EITHER_STATION_ERROR_MESSAGE);
        }
    }

    public Sections(List<Section> sections) {
        this.sections.addAll(sections);
    }

    public static Sections of(Section section) {
        return new Sections(section);
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> getStationsOrderByUptoDown() {
        return LineStationUpToDownSortUtils.sort(this.sections);
    }

    private Set<Station> getStations() {
        final Set<Station> stations = new HashSet<>();
        this.sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });
        return stations;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    @Override
    public String toString() {
        return sections.toString();
    }

}
