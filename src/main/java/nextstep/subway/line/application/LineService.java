package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.common.exception.ResourceNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.ResourceAlreadyExistException;

@Service
@Transactional(readOnly = true)
public class LineService {
	public static final String EXCEPTION_MESSAGE_LINE_ALREADY_EXIST = "이미 존재하는 노선입니다.";
	public static final String EXCEPTION_MESSAGE_NOT_FOUND_LINE = "존재하지 않는 노선입니다.";

	private LineRepository lineRepository;

	public LineService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	@Transactional
	public LineResponse saveLine(LineRequest request) {
		validateAlreadyExist(request);
		Line persistLine = lineRepository.save(request.toLine());
		return LineResponse.of(persistLine);
	}

	public List<LineResponse> findLineList() {
		List<Line> line = lineRepository.findAll();
		return line.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse findLine(Long id) {
		Optional<Line> line = lineRepository.findById(id);
		return line.map(LineResponse::of)
			.orElseThrow(() -> new ResourceNotFoundException(EXCEPTION_MESSAGE_NOT_FOUND_LINE));
	}

	private void validateAlreadyExist(LineRequest request) {
		if (lineRepository.existsByName(request.getName())) {
			throw new ResourceAlreadyExistException(EXCEPTION_MESSAGE_LINE_ALREADY_EXIST);
		}
	}

	@Transactional
	public void updateLine(Long id, LineRequest request) {
		Line line = lineRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(EXCEPTION_MESSAGE_NOT_FOUND_LINE));
		line.update(new Line(request.getName(), request.getColor()));
	}
}
