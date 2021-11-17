package nextstep.subway.line.application;

import nextstep.subway.exception.CannotFindEntityException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    public static final String ERROR_MESSAGE_CANNOT_FIND_ENTITY = "요청한 지하철 노선 정보를 찾을 수 없습니다.";
    private final LineRepository lineRepository;

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

    @Transactional(readOnly = true)
    public List<LineResponse> findById(Long id) throws CannotFindEntityException {
        Line line = findLineById(id);

        return Collections.singletonList(LineResponse.of(line));
    }

    public void updateById(Long id, LineRequest lineRequest) throws CannotFindEntityException {
        Line line = findLineById(id);
        line.update(lineRequest.toLine());
    }

    private Line findLineById(Long id) throws CannotFindEntityException {
        Optional<Line> optionalLine = lineRepository.findById(id);

        if (!optionalLine.isPresent()) {
            throw new CannotFindEntityException(ERROR_MESSAGE_CANNOT_FIND_ENTITY);
        }

        return optionalLine.get();
    }
}
