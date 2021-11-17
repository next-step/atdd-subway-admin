package nextstep.subway.line.application;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.NotExistsLineException;

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

    public List<LineResponse> findAllLine(Pageable pageable) {
        return lineRepository.findAll(pageable).stream()
            .map(LineResponse::of)
            .collect(toList());
    }

    public LineResponse findLineById(final Long id) {
        return LineResponse.of(
            lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new)
        );
    }

    public LineResponse updateLine(final Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(NotExistsLineException::new);

        line.update(lineRequest.toLine());
        return LineResponse.of(lineRepository.save(line));
    }

    public void delete(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(NotExistsLineException::new);
        lineRepository.delete(line);
    }
}
