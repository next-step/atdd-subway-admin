package nextstep.subway.line.application;

import nextstep.subway.common.exception.*;
import nextstep.subway.line.domain.*;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

	private LineRepository lineRepository;

	private StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public List<LineResponse> listLine() {
		List<Line> findLines = lineRepository.findAll();
		return findLines.stream().map(LineResponse::of).collect(Collectors.toList());
	}

	public LineResponse findLineById(Long id) {
		Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundLineException(id));
		return LineResponse.of(line);
	}

	@Transactional
	public void update(LineRequest lineRequest) {
		Line line = lineRepository.findById(lineRequest.getId()).orElseThrow(() -> new NotFoundLineException(lineRequest.getId()));
		line.update(lineRequest.toLine());
		lineRepository.save(line);
	}

	@Transactional
	public void delete(Long id) {
		Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundLineException(id));
		lineRepository.delete(line);
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest) {
		Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(() -> new NotFoundStationException(lineRequest.getUpStationId()));
		Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(() -> new NotFoundStationException(lineRequest.getDownStationId()));

		Line line = new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation, lineRequest.getDistance());
		lineRepository.save(line);
		return LineResponse.of(line);
	}

	@Transactional
	public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
		Line line = lineRepository.findById(lineId).orElseThrow(() -> new NotFoundStationException(lineId));
		Section newSection = toSection(line, sectionRequest);
		line.addSection(newSection, line);
		return LineResponse.of(lineRepository.save(line));
	}

	private Section toSection(Line line, SectionRequest sectionRequest) {
		Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(() -> new NotFoundStationException(sectionRequest.getUpStationId()));
		Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(() -> new NotFoundStationException(sectionRequest.getDownStationId()));
		return new Section(line, upStation, downStation, sectionRequest.getDistance(), upStation);
	}

	@Transactional
	public void removeSectionByStationId(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId).orElseThrow(() -> new NotFoundLineException(lineId));
		line.removeSectionByStationId(stationId);
		lineRepository.save(line);
	}
}
