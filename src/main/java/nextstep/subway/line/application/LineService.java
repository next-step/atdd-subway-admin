package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineInfoResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineInfoResponse> findAllForLineInfo() {
        return lineRepository.findAll()
                                .stream()
                                .map(LineInfoResponse::of)
                                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineInfoResponse findLineInfo(Long lineId) {
        return lineRepository.findById(lineId)
                                .map(LineInfoResponse::of)
                                .orElse(new LineInfoResponse());
    }

    @Transactional
    public void updateLineInfo(Long lineId, Line newLine) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        line.update(newLine);
    }

    @Transactional
    public void deleteLineInfo(Long lineId) {
        lineRepository.deleteById(lineId);
    }
}
