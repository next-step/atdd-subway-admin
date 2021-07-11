package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.common.exception.DuplicateSectionException;
import nextstep.subway.common.exception.NoDataException;
import nextstep.subway.common.exception.NotMatchStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());
        Line persistLine = lineRepository.save(new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        return LineResponse.of(findLineById(id));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        findLineById(id).update(lineRequest.toLine());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());

        List<Station> stations = line.getStations();

        boolean upStationMatched = stations.stream().anyMatch(station -> station == upStation);
        boolean downStationMatched = stations.stream().anyMatch(station -> station == downStation);

        if(upStationMatched && downStationMatched) {
            throw new DuplicateSectionException();
        }

        if(!upStationMatched && !downStationMatched) {
            throw new NotMatchStationException();
        }
        // 추가 상행역이 역들 중에 있는 경우
        if(upStationMatched) {
            // 역들 사이에 들어가는 경우, 기존 구간 정보 업데이트
            line.getSections().findNextSectionByUpStation(upStation)
                .ifPresent(section -> section.updateUpStation(downStation, sectionRequest.getDistance()));
        }

        // 추가 하행역이 역들 중에 있는 경우
        if(downStationMatched) {
            //역들 사이에 들어가는 경우, 기존 구간 정보 업데이트
            line.getSections().findNextSectionByDownStation(downStation)
            .ifPresent(section -> section.updateDownStation(upStation, sectionRequest.getDistance()));
        }
        // 구간 추가
        line.addSection(new Section(line, upStation, downStation, sectionRequest.getDistance()));
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NoDataException::new);
    }
}
