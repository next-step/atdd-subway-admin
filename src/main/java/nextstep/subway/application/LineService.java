package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Lines;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        return LineResponse.of(lineRepository.save(lineRequest.toLine()));
    }

    public List<LineResponse> findAllLines() {
        Lines lines = new Lines(lineRepository.findAll());

        return lines.asList().stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Optional<Line> retrieveLine = lineRepository.findById(id);
        return LineResponse.of(retrieveLine.get());
    }

    @Transactional
    public LineResponse modifyLine(Long id, LineRequest lineRequest) {
        Optional<Line> retrieveLine = lineRepository.findById(id);
        Line line = retrieveLine.get();
        line.modify(lineRequest.getName(), lineRequest.getColor());
        return LineResponse.of(lineRepository.save(line));
    }
}
