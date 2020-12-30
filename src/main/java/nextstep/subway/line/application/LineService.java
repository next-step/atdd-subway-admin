package nextstep.subway.line.application;

import nextstep.subway.common.exception.CustomException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(long id) {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow(() -> new CustomException("`Line`의 엔티티가 존재하지 않습니다."));
    }

    public LineResponse updateLine(long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new CustomException("`Line`의 엔티티가 존재하지 않습니다."));
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void deleteById(long id) {
        lineRepository.deleteById(id);
    }
}
