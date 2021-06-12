package nextstep.subway.section.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.line.NotFoundLineException;
import nextstep.subway.exception.station.NotFoundStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line findedLine = lineRepository.findById(lineId).orElseThrow(NotFoundLineException::new);
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
            .orElseThrow(NotFoundStationException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
            .orElseThrow(NotFoundStationException::new);
        findedLine.addSection(sectionRequest.toSection(upStation, downStation));

        return LineResponse.of(findedLine);
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line findedLine = lineRepository.findById(lineId).orElseThrow(NotFoundLineException::new);
        Station station = stationRepository.findById(stationId).orElseThrow(NotFoundStationException::new);
        findedLine.removeSection(station);
    }
}
