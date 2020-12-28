package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<Station> stations = stationRepository.findByIdIn(Arrays.asList(request.getUpStationId(), request.getDownStationId()));
        Station upStation = findOneStationById(stations, request.getUpStationId());
        Station downStation = findOneStationById(stations, request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findById(long id) {
        return lineRepository.findById(id)
            .map(LineResponse::of)
            .orElseThrow(NoSuchElementException::new);
    }

    public void modifyLine(long id, LineRequest lineRequest) {
        Line modifiedLine = lineRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
        modifiedLine.update(lineRequest.toLine());
        lineRepository.save(modifiedLine);
    }

    public void deleteById(long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(NoSuchElementException::new);

        Section newSection = toSection(line, sectionRequest);
        line.addLineSection(newSection);
        return LineResponse.of(lineRepository.save(line));
    }

    private Section toSection(Line line, SectionRequest sectionRequest) {
        Station preStation = findNullableStationById(sectionRequest.getUpStationId());
        Station station = findNullableStationById(sectionRequest.getDownStationId());

        return new Section(line, station, sectionRequest.getDistance(), preStation);
    }

    private Station findNullableStationById (Long id) {
        if (id == null) {
            return null;
        }
        return stationRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
    }

    private Station findOneStationById (List<Station> stations, Long id) {
        return stations.stream()
            .filter(station -> station.getId().equals(id))
            .findFirst()
            .orElseThrow(NoSuchElementException::new);
    }
}
