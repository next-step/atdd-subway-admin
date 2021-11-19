package nextstep.subway.line.application;

import static java.util.stream.Collectors.*;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLine() {
        return lineRepository.findAll().stream()
                             .map(LineResponse::of)
                             .collect(toList());
    }

    public LineResponse findLineById(final Long id) {

        Line line = lineRepository.findById(id)
                                  .orElseThrow(EntityNotFoundException::new);

        return LineResponse.of(line);
    }

    public LineResponse updateLine(final Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(EntityNotFoundException::new);

        line.update(lineRequest.toLine());

        return LineResponse.of(line);
    }

    public void delete(Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(EntityNotFoundException::new);
        lineRepository.delete(line);
    }
}
