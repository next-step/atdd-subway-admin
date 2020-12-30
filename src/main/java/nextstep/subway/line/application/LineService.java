package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

	private final LineRepository lineRepository;
	private final StationService stationService;

	public LineService(LineRepository lineRepository,
		  StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	public List<LineResponse> findAllLines() {
		return lineRepository.findAll().stream()
			  .map(LineResponse::of)
			  .collect(Collectors.toList());
	}

	public LineResponse findLine(Long id) {
		Line line = getLine(id);
		return LineResponse.of(line);
	}

	public LineResponse saveLine(LineRequest request) {
		Line line = toLineWithSection(request);
		Line persistLine = lineRepository.save(line);
		return LineResponse.of(persistLine);
	}

	public LineResponse updateLine(Long id, LineRequest lineRequest) {
		Line line = getLine(id);
		line.update(lineRequest.toLine());

		return LineResponse.of(line);
	}

	public void deleteLine(Long id) {
		getLine(id);
		lineRepository.deleteById(id);
	}

	private Line toLineWithSection(LineRequest request) {
		Line line = request.toLine();

		validateSectionInfo(request);

		Station upStation = stationService.findById(request.getUpStationId());
		Station downStation = stationService.findById(request.getDownStationId());
		line.addSection(new Section(upStation, downStation, request.getDistance()));
		return line;
	}

	private Line getLine(Long id) {
		Optional<Line> maybeLine = findById(id);
		return maybeLine.orElseThrow(()
			  -> new EntityNotFoundException("[id=" + id + "] 노선정보가 존재하지 않습니다."));
	}

	private void validateSectionInfo(LineRequest request) {
		if (request.isSectionInfoEmpty()) {
			throw new IllegalArgumentException("구간정보가 존재하지 않습니다.");
		}
	}

	private Optional<Line> findById(Long id) {
		return lineRepository.findById(id);
	}
}
