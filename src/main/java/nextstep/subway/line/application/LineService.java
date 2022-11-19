package nextstep.subway.line.application;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.line.exception.LineExceptionCode;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository,
            StationService stationService) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLineWithSection(upStation, downStation));

        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(toList());
    }

    public LineResponse findLineResponseById(Long id) {
        return LineResponse.of(findById(id));
    }

    public Line findById(Long id) {
        Optional<Line> optional = lineRepository.findById(id);
        if(!optional.isPresent()) {
            throw new NotFoundException(LineExceptionCode.NOT_FOUND_BY_ID.getMessage());
        }

        return optional.get();
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void updateSection(Long id, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        List<Section> matchedSections = sectionRepository.findAllByRequestedSection(upStation, downStation);
        Line line = findById(id);

        line.updateSections(sectionRequest.toSection(upStation, downStation), matchedSections);
    }

    public List<SectionResponse> findAllByLine(Long lineId) {
        Line line = findById(lineId);

        return sectionRepository.findAllByLine(line).stream()
                .map(SectionResponse::of)
                .collect(toList());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Optional<Section> sectionOfUpStation = sectionRepository.findByUpStationId(stationId);
        Optional<Section> sectionOfDownStation = sectionRepository.findByDownStationId(stationId);
        Line line = findById(lineId);

        line.deleteSectionContainsStation(sectionOfUpStation, sectionOfDownStation);
    }
}
