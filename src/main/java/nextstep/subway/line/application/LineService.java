package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
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
	public List<LineResponse> findAllLine() {
		return lineRepository.findAll().stream()
				.map(LineResponse::of)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findLineById(Long id) {
		return LineResponse.of(findLine(id));
	}

	@Transactional
	public void updateLineById(Long id, LineRequest lineRequest) {
		Line line = findLine(id);
		line.update(lineRequest.toLine());
	}

	@Transactional
	public void deleteLineById(Long id) {
		Line line = findLine(id);
		lineRepository.delete(line);
	}

	private Line findLine(Long id) {
		return lineRepository.findById(id)
				.orElseThrow(() -> new LineNotFoundException("cannot find line"));
	}
}
