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
        Line persistLine = lineRepository.save(lineRequestToLine(lineRequest));
        Station upStation = stationService.getStationById(lineRequest.getUpStationId());
        Station downStation = stationService.getStationById(lineRequest.getDownStationId());
        persistLine.initSection(new Section(persistLine, upStation, downStation, lineRequest.getDistance()));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream().map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public Line getLineDomainById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.LINE_NO_FIND_BY_ID.getMessage()));
    }

    public LineResponse getLineById(Long id) {
        return LineResponse.of(getLineDomainById(id));
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

    private Line lineRequestToLine(LineRequest lineRequest) {
        return new Line(lineRequest.getName(), lineRequest.getColor());
    }

    public SectionsResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = getLineDomainById(lineId);
        int distance = sectionRequest.getDistance();
        checkDistance(line, distance);

        Station requestUpStation = stationService.getStationById(sectionRequest.getUpStationId());
        Station requestDownStation = stationService.getStationById(sectionRequest.getDownStationId());
        Section newSection = new Section(line, requestUpStation, requestDownStation, distance);

        return new SectionsResponse(line.addAndGetSections(newSection, requestUpStation, requestDownStation)
                .stream().map(SectionResponse::of).collect(Collectors.toList()));
    }

    private void checkDistance(Line line, int distance) {
        if(line.compareToDistance(distance) <= 0) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_DISTANCE_VALUE.getMessage());
        }
        if(distance <= 0) {
            throw new IllegalArgumentException(ErrorMessage.EXCEED_SECTION_DISTANCE.getMessage());
        }
    }

}
