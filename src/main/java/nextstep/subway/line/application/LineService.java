package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
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
		Station upStation = stationRepository.findById(request.getUpStationId())
			.orElseThrow(() -> new DataIntegrityViolationException("없는 역입니다."));
		Station downStation = stationRepository.findById(request.getDownStationId())
			.orElseThrow(() -> new DataIntegrityViolationException("없는 역입니다."));

		Line persistLine = lineRepository.save(
			new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));

		return LineResponse.of(persistLine);
	}

	public List<LineResponse> getLines() {
		List<Line> lines = lineRepository.findAll();

		return lines.stream().map(LineResponse::of).collect(Collectors.toList());
	}

	public void updateLine(Long id, LineRequest request) {
		Line persistLine = lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당하는 노선이 없습니다."));
		persistLine.update(request.toLine());
	}

	public LineResponse getLine(Long id) {
		return LineResponse.of(
			lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당하는 노선이 없습니다.")));
	}

	public void deleteLine(Long id) {
		lineRepository.deleteById(id);
	}

	public LineResponse addSection(Long lineId, SectionRequest request) {
		Line line = lineRepository.findById(lineId).orElseThrow(() -> new DataIntegrityViolationException("없는 노선입니다."));
		Station upStation = stationRepository.findById(request.getUpStationId())
			.orElseThrow(() -> new DataIntegrityViolationException("없는 역입니다."));
		Station downStation = stationRepository.findById(request.getDownStationId())
			.orElseThrow(() -> new DataIntegrityViolationException("없는 역입니다."));

		line.addSection(upStation, downStation, request.getDistance());

		return LineResponse.of(line);
	}

	public void deleteSection(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId).orElseThrow(() -> new DataIntegrityViolationException("없는 노선입니다."));
		Station station = stationRepository.findById(stationId)
			.orElseThrow(() -> new DataIntegrityViolationException("없는 역입니다."));
		line.deleteSectionBy(station);
	}
}
