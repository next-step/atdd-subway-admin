package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineCreateResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.application.exception.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.application.LineService.NOT_FOUND_LINE;
import static nextstep.subway.line.application.exception.LineNotFoundException.error;
import static nextstep.subway.station.application.StationService.NOT_FOUND_STATION;

@Service
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineCreateResponse addSection(Long id, SectionRequest request) {
        Line line = findLine(id);
        Station upStation = findByStation(request.getUpStationId());
        Station downStation = findByStation(request.getDownStationId());

        line.addSection(request.toSection(upStation, downStation));
        return LineCreateResponse.of(line);
    }

    public List<SectionResponse> findSection(Long id) {
        Line line = findLine(id);
        List<Section> sections = line.getSections();
        return sections.stream()
                .map(section -> SectionResponse.of(section.getUpStation(), section.getDownStation(), section.getDistance()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStation(Long lineId, Long stationId) {
        Line line = findLine(lineId);
        Station station = findByStation(stationId);
        line.removeSection(station);
    }

    private Station findByStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> StationNotFoundException.error(NOT_FOUND_STATION));
    }

    private Line findLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> error(NOT_FOUND_LINE));
    }
}
