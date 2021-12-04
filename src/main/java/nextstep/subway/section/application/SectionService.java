package nextstep.subway.section.application;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveSection(long lineId, SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        Section added = makeSection(sectionRequest);

        line.updateSections(added);

        return LineResponse.from(line);
    }

    @Transactional
    public void removeSectionByStationId(long lineId, long stationId) {
        Line line = findLineById(lineId);
        Station station = findStationById(stationId);

        line.deleteStation(station);
    }

    private Line findLineById(long id) {
        return lineRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
    }

    private Section makeSection(SectionRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        return new Section(upStation, downStation, new Distance(request.getDistance()));
    }

    private Station findStationById(long id) {
        return stationRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
    }
}
