package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.StationGroup;

@Service
@Transactional
public class LineService {
	private static final int ADJUST_NEXT_INDEX = 1;

	private StationService stationService;
	private SectionService sectionService;
	private LineRepository lineRepository;
	private SectionRepository sectionRepository;

	public LineService(StationService stationService, SectionService sectionService, LineRepository lineRepository,
		SectionRepository sectionRepository) {
		this.stationService = stationService;
		this.sectionService = sectionService;
		this.lineRepository = lineRepository;
		this.sectionRepository = sectionRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		StationGroup stationGroupToAdd = addUpStationAndDownStation(request);
		Line persistLine = lineRepository.save(request.toLine(stationGroupToAdd));
		sectionService.saveSection(request, persistLine);
		return changeSortedStations(persistLine);
	}

	private LineResponse changeSortedStations(Line persistLine) {
		sortStationGroup(persistLine);
		return LineResponse.of(persistLine);
	}

	private StationGroup addUpStationAndDownStation(LineRequest request) {
		List<Long> stationIdsToAdd = Arrays.asList(request.getUpStationId(), request.getDownStationId())
			.stream()
			.map(Long::parseLong)
			.collect(Collectors.toList());
		return stationService.addUpStationAndDownStation(stationIdsToAdd);
	}

	private void sortStationGroup(Line line) {
		StationGroup stationGroupOfLine = line.stationGroup();
		stationGroupOfLine.clear();
		sectionRepository.findAllByLine(line)
			.forEach(section -> addStationFromSectionIntoStationGroup(stationGroupOfLine, section));
	}

	private void addStationFromSectionIntoStationGroup(StationGroup stationGroupOfLine, Section section) {
		addStationsIntoEmptyStationGroup(stationGroupOfLine, section);
		addDownStationNextToUpStation(stationGroupOfLine, section);
		addUpStationPreviousToDownStation(stationGroupOfLine, section);
	}

	private void addUpStationPreviousToDownStation(StationGroup stationGroupOfLine, Section section) {
		if (stationGroupOfLine.contains(section.downStation())) {
			int downStationIndex = stationGroupOfLine.indexOf(section.downStation());
			stationGroupOfLine.add(downStationIndex, section.upStation());
		}
	}

	private void addDownStationNextToUpStation(StationGroup stationGroupOfLine, Section section) {
		if (stationGroupOfLine.contains(section.upStation())) {
			int upStationIndex = stationGroupOfLine.indexOf(section.upStation());
			stationGroupOfLine.add(upStationIndex + ADJUST_NEXT_INDEX, section.downStation());
		}
	}

	private void addStationsIntoEmptyStationGroup(StationGroup stationGroupOfLine, Section section) {
		if (stationGroupOfLine.isEmpty()) {
			stationGroupOfLine.add(section.upStation());
			stationGroupOfLine.add(section.downStation());
		}
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		return lineRepository.findAll().stream()
			.map(this::changeSortedStations)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findLineById(Long id) {
		Line persistLine = findLineByIdFromRepository(id);
		return changeSortedStations(persistLine);
	}

	private Line findLineByIdFromRepository(Long id) {
		return Optional.ofNullable(lineRepository.findById(id)).get()
			.orElseThrow(new NotFoundException("지하철 노선을 찾을 수 없습니다. id :" + id));
	}

	public void deleteLineById(Long id) {
		sectionService.deleteAllByLine(findLineByIdFromRepository(id));
		lineRepository.deleteById(id);
	}

	public void updateLine(Long id, LineRequest lineRequest) {
		Line sourceLine = findLineByIdFromRepository(id);
		sourceLine.update(lineRequest.toLine());
	}
}
