package nextstep.subway.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.InvalidLineException;
import nextstep.subway.exception.InvalidStationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final StationService stationService;
    private static final String INVALID_LINE = "%d : 유효하지 않은 지하철 노선입니다.";

    public LineService(LineRepository lineRepository,
        StationRepository stationRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.findStationById(lineRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineRequest.getDownStationId());
        Line newLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(newLine);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        String lineName = lineRequest.getName();
        String lineColor = lineRequest.getColor();
        Line foundLine = lineRepository.findById(id).orElseThrow(
            () -> new InvalidLineException(String.format(INVALID_LINE, id))
        );
        foundLine.update(lineName, lineColor);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(line -> LineResponse.of(line))
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return LineResponse.of(lineRepository.findById(id)
            .orElseThrow(() -> new InvalidLineException(String.format(INVALID_LINE, id))));
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line findLine = lineRepository.findById(lineId).orElseThrow(
            () -> new InvalidLineException(String.format(INVALID_LINE, lineId))
        );
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
        findLine.addSection(Section.of(upStation, downStation, sectionRequest.getDistance()));
    }

}
