package nextstep.subway.domain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.dto.StationResponse;

@Embeddable
public class Sections {

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

        Deque<Station> setctions = new ArrayDeque<>();

        sections.forEach(section -> {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();

            if (setctions.isEmpty()) {
                setctions.addAll(Arrays.asList(upStation, downStation));
            } else {
                makeSequenceStations(setctions, upStation, downStation);
            }
        });

        return new ArrayList(setctions);
    }

    private void makeSequenceStations(Deque stationDeque, Station upStation, Station downStation) {
        if (stationDeque.getFirst().equals(downStation)) {
            stationDeque.addFirst(upStation);
            return;
        }

        stationDeque.addLast(downStation);
    }

}
