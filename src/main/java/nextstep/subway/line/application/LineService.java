package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class LineService {
	private LineRepository lineRepository;
	private StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		checkDuplicated(request);
		Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(RuntimeException::new);
		Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(RuntimeException::new);

		Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
		return LineResponse.of(persistLine);
	}

	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();

		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse findById(Long id) {
		Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다."));
		return LineResponse.of(line);
	}

	public void updateLine(Long id, LineRequest request) {
		Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다."));

		line.update(request.toLine());
	}

	public void deleteLine(Long id) {
		lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다."));

		lineRepository.deleteById(id);
	}

	private void checkDuplicated(LineRequest request) {
		if (lineRepository.findByName(request.getName()).isPresent()) {
			throw new IllegalArgumentException("노선이 이미 존재합니다.");
		}
	}

	public LineResponse addNewSection(Long lineId, SectionRequest sectionRequest) {

		Line line = lineRepository.findById(lineId).orElseThrow(() -> new NotFoundLineException("Line이 존재하지 않습니다."));

		Section newSection = toSection(line, sectionRequest);
		line.addNewSection(newSection);
		return LineResponse.of(line);
	}

	public LineResponse removeSectionByStationId(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId).orElseThrow(NotFoundLineException::new);

		line.removeSection(stationId);
		return LineResponse.of(line);
	}

	private Section toSection(Line line, SectionRequest sectionRequest) {

		Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
			.orElseThrow(() -> new NotFoundStationException("Station이 존재하지 않습니다."));
		Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
			.orElseThrow(() -> new NotFoundStationException("Station이 존재하지 않습니다."));

		return new Section(line, upStation, downStation, sectionRequest.getDistance());
	}

}
