package nextstep.subway.application;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.dto.request.LineRequest;
import nextstep.subway.dto.response.LineReponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineReponse createLine(LineRequest lineRequest) {
        Line line = lineRepository.save(lineRequest.toLine());
        return LineReponse.of(line);
    }

    public List<LineReponse> getLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineReponse.of(line))
                .collect(Collectors.toList());
    }

    public LineReponse getLine(Long id) {
        Line line = lineRepository.getById(id);
        return LineReponse.of(line);
    }
}
