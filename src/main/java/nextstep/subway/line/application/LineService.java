package nextstep.subway.line.application;

import nextstep.subway.line.application.exceptions.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.exceptions.AlreadyExistLineException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        isAlreadyExistLine(request.getName(), request.getColor());
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(it -> LineResponse.of(it))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));

        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    void isAlreadyExistLine(final String name, final String color) {
        if (lineRepository.existsByNameAndColor(name, color)) {
            throw new AlreadyExistLineException("이미 존재하는 지하철을 또 추가할 수 없습니다.");
        }
    }
}
