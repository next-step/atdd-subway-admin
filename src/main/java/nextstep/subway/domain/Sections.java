package nextstep.subway.domain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;

@Embeddable
public class Sections {

    private static final int HAS_ONE_STATION_SIZE = 1;

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(final List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
    }

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            sections.forEach(inner -> inner.validSection(section));
            sections
                    .stream()
                    .filter(inner -> inner.isSameUpDownStation(section))
                    .findAny()
                    .ifPresent(inner -> inner.resetSection(section));
        }

        sections.add(section);
    }

    public List<StationResponse> getStations() {
        return getStationList().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    private List<Station> getStationList() {

        Deque<Station> stations = new ArrayDeque<>();

        sections.forEach(section -> {

            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();

            if (stations.isEmpty()) {
                stations.addAll(Arrays.asList(upStation, downStation));
            } else {
                makeSequenceStations(stations, upStation, downStation);
            }

        });

        return new ArrayList(stations);
    }

    private void makeSequenceStations(Deque stationDeque, Station upStation, Station downStation) {
        if (stationDeque.getFirst().equals(downStation)) {
            stationDeque.addFirst(upStation);
            return;
        }
        stationDeque.addLast(downStation);
    }

    public void deleteStation(Station deleteStation) {
        validRemoveStation(deleteStation);

        if (isBetweenStations(deleteStation)) {
            deleteBetweenStation(deleteStation);
        }

        if (sections.size() != HAS_ONE_STATION_SIZE) {
            removeUpStation(deleteStation);
            removeDownStation(deleteStation);
        }
    }

    public void validRemoveStation(Station deleteStation) {
        validSectionsSize();
        validStationInStations(deleteStation);
    }

    private void validStationInStations(Station deleteStation) {
        if (!getStationList().contains(deleteStation)) {
            throw new SubwayException(ErrorCode.VALID_DELETE_NOT_IN_STATIONS_ERROR);
        }
    }

    private void validSectionsSize() {
        if (sections.size() == HAS_ONE_STATION_SIZE) {
            throw new SubwayException(ErrorCode.VALID_DELETE_LAST_STATION_ERROR);
        }
    }

    private boolean isBetweenStations(Station removeStation) {
        return getUpStations().contains(removeStation) && getDownStations().contains(removeStation);
    }

    private List<Station> getUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private void deleteBetweenStation(Station removeStation) {
        Section removeSection = findRemoveSection(removeStation);
        sections.remove(removeSection);
        sections.stream()
                .findFirst()
                .ifPresent(inner -> inner.removeSection(removeSection));
    }

    private Section findRemoveSection(Station removeStation) {
        return sections.stream()
                .filter(inner -> inner.isSameUpStation(removeStation) || inner.isSameDownStation(removeStation))
                .findAny()
                .filter(inner -> inner.isSameUpStation(removeStation))
                .orElseThrow(NoSuchElementException::new);
    }

    private void removeUpStation(Station removeStation) {
        sections.stream()
                .filter(inner -> inner.isSameUpStation(removeStation))
                .findAny().ifPresent(inner -> sections.remove(inner));
    }

    private void removeDownStation(Station removeStation) {
        sections.stream()
                .filter(inner -> inner.isSameDownStation(removeStation))
                .findAny().ifPresent(inner -> sections.remove(inner));
    }

}
