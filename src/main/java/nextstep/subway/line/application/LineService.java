package nextstep.subway.line.application;

import nextstep.subway.exception.NotFoundDataException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(final LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse save(final LineRequest request) {
        return LineResponse.of(lineRepository.save(request.toLine()));
    }

    public LineResponses findAll() {
        return LineResponses.of(lineRepository.findAll());
    }

    public LineResponse findById(Long id) {
        return LineResponse.of(lineRepository.findById(id).orElse(new Line()));
    }

    public void update(Long id, LineRequest updatedLine) {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundDataException::new);
        line.update(Line.of(updatedLine));
    }

    public void delete(Long id) {
        lineRepository.deleteById(id);
    }
}
