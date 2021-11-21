package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.UpdateLineResponseDto;
import nextstep.subway.line.exception.DuplicateLineNameException;
import nextstep.subway.line.exception.NotFoundLineByIdException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LineResponse saveLine(LineRequest request) {
        validateExistsByName(request.getName());
        final Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional
    public UpdateLineResponseDto updateLine(long id, LineRequest lineRequest) {
        validateExistsByName(lineRequest.getName());
        final Line line = getLineByIdOrElseThrow(id);
        line.update(lineRequest.toLine());
        lineRepository.flush();
        return UpdateLineResponseDto.of(line);
    }

    private void validateExistsByName(String name) {
        if (lineRepository.existsByName(name)) {
            throw new DuplicateLineNameException();
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(long id) {
        final Line line = getLineByIdOrElseThrow(id);
        return LineResponse.of(line);
    }

    private Line getLineByIdOrElseThrow(long id) {
        return lineRepository.findById(id)
                .orElseThrow(NotFoundLineByIdException::new);
    }

    @Transactional
    public void deleteLine(long id) {
        try {
            lineRepository.deleteById(id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundLineByIdException();
        }
    }
}
