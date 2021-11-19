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
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        throwExceptionExistsDuplicateName(request);
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    private void throwExceptionExistsDuplicateName(LineRequest request) {
        Optional<Line> savedLine = lineRepository.findByName(request.getName());
        if (savedLine.isPresent()) {
            throw new DuplicateLineNameException("중복된 노선명이 있습니다.");
        }
    }

    public List<LineResponse> getLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow(() -> new LineNotFoundException(String.format("아이디 %d는 없는 노선입니다.", id)));
    }
}
