package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());
        Station upStation = stationService.getStationById(lineRequest.getUpStationId());
        Station downStation = stationService.getStationById(lineRequest.getDownStationId());
        persistLine.initSection(new Section(persistLine, upStation, downStation, new Distance(lineRequest.getDistance())));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream().map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public Line getById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.LINE_NO_FIND_BY_ID.getMessage()));
    }

    public LineResponse getLineResponseById(Long id) {
        return LineResponse.of(getById(id));
    }

    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.LINE_NO_FIND_BY_ID.getMessage()));
        line.updateNameAndColor(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
        lineRepository.save(line);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public SectionsResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = getById(lineId);
        Distance distance = new Distance(sectionRequest.getDistance());

        Station requestUpStation = stationService.getStationById(sectionRequest.getUpStationId());
        Station requestDownStation = stationService.getStationById(sectionRequest.getDownStationId());

        return new SectionsResponse(line.addAndGetSections(requestUpStation, requestDownStation, distance)
                .stream().map(SectionResponse::of).collect(Collectors.toList()));
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = getById(lineId);
        line.checkStationExist(stationId);
        line.removeSection(stationId);
    }
}
