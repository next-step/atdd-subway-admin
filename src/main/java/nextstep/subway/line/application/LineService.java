package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.DuplicateLineNameException;
import nextstep.subway.line.exception.LineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        throwExceptionExistsDuplicateName(request);
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    private void throwExceptionExistsDuplicateName(LineRequest request) {
        lineRepository.findByName(request.getName())
                .ifPresent(line -> {
                    throw new DuplicateLineNameException(line);
                });
    }

    public List<LineResponse> getLines() {
        return LineResponse.listOf(lineRepository.findAll());
    }

    public LineResponse getLine(Long id) {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow(throwLineNotFoundException(id));
    }

    public LineResponse modifyLine(Long id, LineRequest lineRequest) {
        return lineRepository.findById(id)
                .map(line -> line.update(lineRequest.toLine()))
                .map(LineResponse::of)
                .orElseThrow(throwLineNotFoundException(id));
    }

    private Supplier<LineNotFoundException> throwLineNotFoundException(Long id) {
        return () -> new LineNotFoundException(String.format("아이디 %d는 없는 노선입니다.", id));
    }

    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(throwLineNotFoundException(id));
        lineRepository.delete(line);
    }
}
