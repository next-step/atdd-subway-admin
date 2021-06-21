package nextstep.subway.line.application;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineNameDuplicatedException;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) throws LineNameDuplicatedException {
        checkNameDuplication(request.getName());
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    private void checkNameDuplication(String name) throws LineNameDuplicatedException {
        if (lineRepository.findByName(name).isPresent()) {
            throw new LineNameDuplicatedException(name);
        }
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(toList());
    }
}
