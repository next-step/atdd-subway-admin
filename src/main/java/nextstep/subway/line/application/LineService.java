package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.UpdateLineResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String NOT_FOUND_ERROR_MESSAGE = "해당 노선의 아이디가 존재하지 않습니다.";
    private static final String DUPLICATED_NAME_ERROR_MESSAGE = "노선의 이름이 중복되었습니다.";

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

    private void validateExistsByName(String name) {
        if (lineRepository.existsByName(name)) {
            throw new IllegalArgumentException(DUPLICATED_NAME_ERROR_MESSAGE);
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(long id) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException(NOT_FOUND_ERROR_MESSAGE);
                });
        return LineResponse.of(line);
    }

    @Transactional
    public UpdateLineResponseDto updateLine(long id, LineRequest lineRequest) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException(NOT_FOUND_ERROR_MESSAGE);
                });
        line.update(lineRequest.toLine());
        lineRepository.flush();
        return UpdateLineResponseDto.of(line);
    }

    @Transactional
    public void deleteLine(long id) {
        if (lineRepository.existsById(id)) {
            lineRepository.deleteById(id);
            return;
        }
        throw new IllegalArgumentException(NOT_FOUND_ERROR_MESSAGE);
    }
}
