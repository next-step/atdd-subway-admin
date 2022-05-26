package nextstep.subway.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;

@Service
@Transactional(readOnly = true)
public class LineService {
	private final LineRepository lineRepository;

	public LineService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest) {
		Line line = lineRepository.save(lineRequest.toLine());
		return LineResponse.of(line);
	}

	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();

		return lines
				.stream()
					.map(line -> LineResponse.of(line))
					.collect(Collectors.toList());
	}

	public LineResponse findLine(Long id) {
		Optional<Line> line = lineRepository.findById(id);
		return LineResponse.of(line.get());
	}

	public LineResponse updateLine(Long id, LineRequest lineRequest) {
		Line line = lineRepository.save(lineRequest.toLine(id));
		return LineResponse.of(line);
	}
}
