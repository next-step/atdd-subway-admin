package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.transaction.Transactional;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(
        final LineRepository lineRepository,
        final StationRepository stationRepository
    ) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest request) {
        final Line line = lineRepository.findById(lineId)
            .orElseThrow(NoSuchElementException::new);
        final List<Station> stations = getStations(
            request.getUpStationId(),
            request.getDownStationId()
        );
        final Station upStation = getStationById(stations, request.getUpStationId());
        final Station downStation = getStationById(stations, request.getDownStationId());
        final Section section = new Section(upStation, downStation, request.getDistance());
        line.addSection(section);
    }

    private List<Station> getStations(final Long upStationId, final Long downStationId) {
        final List<Station> stations = stationRepository.findAllById(
            Arrays.asList(upStationId, downStationId)
        );
        if (stations.size() != 2) {
            throw new NoSuchElementException();
        }
        return stations;
    }

    private Station getStationById(final List<Station> stations, final Long id) {
        return stations.stream()
            .filter(s -> Objects.equals(s.getId(), id))
            .findFirst()
            .orElseThrow(NoSuchElementException::new);
    }
}
