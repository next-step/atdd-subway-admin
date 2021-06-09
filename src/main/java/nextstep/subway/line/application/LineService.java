package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.ConflictException;
import nextstep.subway.exception.NotExistLineException;
import nextstep.subway.line.domain.Line;
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

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        validDuplicate(request);
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }


    private void validDuplicate(LineRequest request) {
        lineRepository.findByName(request.getName()).ifPresent(
            line -> {
                throw new ConflictException();
            }
        );
    }

    public List<LineResponse> getList() {
        return lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        return lineRepository.findById(id).orElseThrow(
            NotExistLineException::new
        ).toResponse();
    }
}
