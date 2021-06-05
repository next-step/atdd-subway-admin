package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;

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
		Line persistLine = lineRepository.save(request.toLine());
		return LineResponse.of(persistLine);
	}

	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();

		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse getLine(long lineID) {
        Line line = this.lineRepository.findById(lineID)
            .orElseThrow(this.getEntityNotFoundExceptionSupplier());
        return LineResponse.of(line);
    }

	private Supplier<EntityNotFoundException> getEntityNotFoundExceptionSupplier() {
		return () -> new EntityNotFoundException("id에 해당하는 Line을 찾을 수 없습니다.");
	}

	public LineResponse updateLine(long lineID, LineRequest lineRequest) {
		Line line = this.lineRepository.findById(lineID).orElseThrow(this.getEntityNotFoundExceptionSupplier());
		line.update(lineRequest.toLine());
		return LineResponse.of(this.lineRepository.save(line));
	}

	public void deleteLine(long lineID) {
		this.lineRepository.findById(lineID).orElseThrow(this.getEntityNotFoundExceptionSupplier());
		this.lineRepository.deleteById(lineID);
	}
}
