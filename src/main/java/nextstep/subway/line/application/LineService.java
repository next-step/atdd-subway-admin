package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	public LineResponse saveLine(LineRequest request) {
		Line persistLine = lineRepository.save(request.toLine());

		return LineResponse.of(persistLine);
	}

	public LineResponse updateLine(Long id, LineRequest request) {
		Line line = findById(id);
		line.update(request.toLine());

		return LineResponse.of(line);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();

		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findLineById(Long id) {
		Line line = findById(id);

		return LineResponse.of(line);
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	private Line findById(Long id) {
		return lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
	}
}
