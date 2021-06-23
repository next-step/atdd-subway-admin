package nextstep.subway.line.application;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class LineService {

	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private StationRepository stationRepository;

	public LineResponse saveLine(LineRequest request) {
		Station upStation = findByIdStation(request.getUpStationId());
		Station downStation = findByIdStation(request.getDownStationId());
		Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
		return LineResponse.of(persistLine);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		return LineResponse.ofList(lineRepository.findAll());
	}

	@Transactional(readOnly = true)
	public LineResponse findLineById(Long id) {
		return LineResponse.of(findByIdLine(id));
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public LineResponse updateLine(Long id, LineRequest request) {
		Line line = findByIdLine(id);
		Station upStation = findByIdStation(request.getUpStationId());
		Station downStation = findByIdStation(request.getDownStationId());
		line.update(request.toLine(upStation, downStation));
		return LineResponse.of(line);
	}

	private Line findByIdLine(Long id) {
		return lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}

	public LineResponse addSection(Long id, SectionRequest sectionRequest) {
		Station upStation = findByIdStation(sectionRequest.getUpStationId());
		Station downStation = findByIdStation(sectionRequest.getDownStationId());
		Line line = findByIdLine(id);
		line.addSection(sectionRequest.toSection(line, upStation, downStation));
		return LineResponse.of(line);
	}

	public void removeSectionByStationId(Long lineId, Long stationId) {
		Line line = findByIdLine(lineId);
		Station deleteStation = findByIdStation(stationId);
		line.removeStation(deleteStation);
	}

	private Station findByIdStation(Long stationId) {
		return stationRepository.findById(stationId).orElseThrow(EntityNotFoundException::new);
	}
}
