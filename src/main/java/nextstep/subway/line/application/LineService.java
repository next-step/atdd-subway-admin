package nextstep.subway.line.application;

import nextstep.subway.exception.DuplicateDataException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) throws NoSuchFieldException {
        validateDuplicatedName(request);
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public void validateDuplicatedName(LineRequest lineRequest) throws NoSuchFieldException {
        if (lineRepository.existsByName(lineRequest.getName())) {
            throw new DuplicateDataException("이미 존재하는 노선 이름입니다.",
                    lineRequest.getClass().getDeclaredField("name").getName(),
                    lineRequest.getName(), lineRequest.getClass().getName());
        }
    }
}
