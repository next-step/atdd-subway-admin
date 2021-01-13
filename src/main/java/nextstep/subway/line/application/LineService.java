package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public List<LineResponse> findAll() {
        return lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public LineResponse findOne(final Long id) {
        return LineResponse.of(lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(String.format("%d로 요청한 지하철 노선 정보가 없습니다.", id))));
    }
}
