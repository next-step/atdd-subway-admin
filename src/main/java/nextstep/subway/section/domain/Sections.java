package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
    private final List<Section> sections = new ArrayList<>();

    public void add(final Section section) {
        sections.add(section);
    }

    public List<StationResponse> stationsResponses() {
        final List<StationResponse> result = new LinkedList<>();

        for (final Section section : sections) {
            addIfAbsent(result, section.getUpStation());
            addIfAbsent(result, section.getDownStation());
        }

        return result;
    }

    private void addIfAbsent(final List<StationResponse> result, final Station station) {
        final StationResponse stationResponse = StationResponse.of(station);

        if (!result.contains(stationResponse)) {
            result.add(stationResponse);
        }
    }
}
