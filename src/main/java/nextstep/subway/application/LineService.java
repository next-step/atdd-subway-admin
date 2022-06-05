package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static nextstep.subway.common.Messages.NOT_FOUND_LINE_ERROR;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Section section = Section.of(
                lineRequest.getDistance(),
                stationService.findStation(lineRequest.getUpStationId()),
                stationService.findStation(lineRequest.getDownStationId())
        );

        Line response = lineRepository.save(Line.of(lineRequest.getName(), lineRequest.getColor(), section));
        return LineResponse.of(response);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = findLineById(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void delete(Long id) {
        Line line = findLineById(id);
        line.delete();
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NOT_FOUND_LINE_ERROR));
    }
}
