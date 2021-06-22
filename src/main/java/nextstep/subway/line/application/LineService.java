package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
	private LineRepository lineRepository;

	public LineService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		checkDuplicated(request);
		Line persistLine = lineRepository.save(request.toLine());
		return LineResponse.of(persistLine);
	}

	private void checkDuplicated(LineRequest request) {
		if (lineRepository.findByName(request.getName()).isPresent()) {
			throw new IllegalArgumentException("노선이 이미 존재합니다.");
		}
	}
}
