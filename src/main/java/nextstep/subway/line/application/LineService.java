package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    private Line findLineByLineId(Long id) {
        return lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        return LineResponse.of(findLineByLineId(id));
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineByLineId(id);
        line.update(lineRequest.toLine());

        Line persistLine = lineRepository.saveAndFlush(line);
        return LineResponse.of(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

}
