package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private static final String NO_EXIST_LINE_ERROR_MESSAGE = "해당 노선은 존재하지 않습니다.";

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Section section = mapToSection(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        Line save = lineRepository.save(Line.of(lineRequest.getName(), lineRequest.getColor(), section));
        return LineResponse.of(save);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return LineResponse.of(findById(id));
    }

    @Transactional
    public void updateLineInfo(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.updateLine(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Section mapToSection(Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);
        return Section.of(upStation, downStation, distance);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NO_EXIST_LINE_ERROR_MESSAGE));
    }

    @Transactional
    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Line line = findById(id);
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        line.addSection(Section.of(upStation, downStation, sectionRequest.getDistance()));
        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse removeSection(Long lineId, Long stationId) {
        Line line = findById(lineId);
        Station station = stationService.findById(stationId);
        line.remove(station);
        return LineResponse.of(line);
    }
}
