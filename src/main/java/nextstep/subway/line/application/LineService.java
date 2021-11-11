package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
		return LineResponse.of(lineRepository.save(request.toLine()));
	}

	public List<LineResponse> getLines() {
		return lineRepository.findAll().stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse getLine(Long id) {
		return LineResponse.of(findLine(id));
	}

	public void updateLine(Long id, LineRequest request) {
		final Line line = findLine(id);
		line.update(request.toLine());
	}

	public void deleteLine(Long id) {
		if (lineRepository.existsById(id)) {
			lineRepository.deleteById(id);
		}
	}

	private Line findLine(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(EntityNotFoundException::new);
	}
}
