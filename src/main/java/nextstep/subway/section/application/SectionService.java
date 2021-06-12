package nextstep.subway.section.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.line.NotFoundLineException;
import nextstep.subway.exception.section.NotFoundSectionException;
import nextstep.subway.exception.station.NotFoundStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository,
            SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
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
        Section section = sectionRepository.findByUpStationId(stationId).orElseThrow(NotFoundSectionException::new);
        findedLine.removeSection(section);
        sectionRepository.delete(section);
    }
}
