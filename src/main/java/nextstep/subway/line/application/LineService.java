package nextstep.subway.line.application;

import nextstep.subway.common.exception.DuplicateEntityException;
import nextstep.subway.common.exception.NotFoundEntityException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {
    private static final String NOT_FOUND_ENTITY_MESSAGE = "Entity를 찾을 수 없습니다.";
    private static final String DUPLICATE_ENTITY_MESSAGE = "이미 존재하는 Entity가 있습니다.";

    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        validateUniqueName(request);
        Line persistLine = lineRepository.save(request.toLine());

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return LineResponse.listOf(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_ENTITY_MESSAGE));

        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse update(Long id, LineRequest lineRequest) {
        validateUniqueName(lineRequest);
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_ENTITY_MESSAGE));
        line.update(lineRequest.toLine());

        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private void validateUniqueName(LineRequest request) {
        if (lineRepository.existsByName(request.getName())) {
            throw new DuplicateEntityException(DUPLICATE_ENTITY_MESSAGE);
        }
    }
}
