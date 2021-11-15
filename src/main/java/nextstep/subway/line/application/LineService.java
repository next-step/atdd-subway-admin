package nextstep.subway.line.application;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
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
        try {
            Line persistLine = lineRepository.save(request.toLine());
            return LineResponse.of(persistLine);
        } catch (DataAccessException e) {
            throw new DuplicationKeyException();
        }
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
}
