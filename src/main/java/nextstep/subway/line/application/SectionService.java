package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public SectionService(
        final LineRepository lineRepository,
        final StationService stationService
    ) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest request) {
        final Line line = lineRepository.findById(lineId)
            .orElseThrow(NoSuchElementException::new);
        final Section section = createSection(request);
        line.addSection(section);
    }

    @Transactional
    public Section createSection(final SectionRequest request) {
        final List<Station> stations = stationService.getStationsByIdIn(
            request.getUpStationId(),
            request.getDownStationId()
        );
        final Station upStation = pickStationById(stations, request.getUpStationId());
        final Station downStation = pickStationById(stations, request.getDownStationId());
        return new Section(upStation, downStation, request.getDistance());
    }

    private Station pickStationById(final List<Station> stations, final Long id) {
        return stations.stream()
            .filter(s -> Objects.equals(s.getId(), id))
            .findFirst()
            .orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public void removeSectionByStationId(final Long lineId, final Long stationId) {
        final Line line = lineRepository.findById(lineId)
            .orElseThrow(NoSuchElementException::new);
        final Station station = stationService.getStationById(stationId);
        line.deleteSection(station);
    }
}
