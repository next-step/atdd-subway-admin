package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse createLine(LineRequest request) {
        Line newLine = lineRepository.save(request.getLine());
        Station upStation = stationRepository.findById(request.getUpStationId())
                                             .orElseThrow(() -> new NoSuchElementException("지하철 역이 존재하지 않습니다"));

        Station downStation = stationRepository.findById(request.getDownStationId())
                                               .orElseThrow(() -> new NoSuchElementException("지하철 역이 존재하지 않습니다"));

        upStation.setLine(newLine);
        downStation.setLine(newLine);

        Section upSection = sectionRepository.save(new Section(newLine, upStation));
        Section downSection = sectionRepository.save(new Section(upSection, newLine, downStation, request.getDistance()));

        upSection.setLine(newLine);
        downSection.setLine(newLine);

        return LineResponse.of(newLine, newLine.getStations());
    }

    public List<LineResponse> getLines() {
        List<LineResponse> lines = new LinkedList<>();
        for (Line line : lineRepository.findAll()) {
            lines.add(LineResponse.of(line, line.getStations()));
        }
        return lines;
    }

    public LineResponse getLineById(Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(() -> new NoSuchElementException("지하철 노선이 존재하지 않습니다"));

        List<Section> sections = sectionRepository.findAllByLineId(id);
        List<Station> stations = new LinkedList<>();
        Long sectionId = null;

        for (Section section : sections) {
            if (section.getParent() == null) {
                sectionId = section.getId();
                stations.add(section.getStation());
                sections.remove(section);
                break;
            }
        }

        while (sections.size() > 0) {
            for (Section section : sections) {
                if (section.getParent().getId().equals(sectionId)) {
                    stations.add(section.getStation());
                    sectionId = section.getId();
                    sections.remove(section);
                    break;
                }
            }
        }

        return LineResponse.of(line, stations);
    }

    @Transactional
    public void deleteLineById(Long id) {
        List<Station> stations = stationRepository.findAllByLineId(id);
        for (Station station : stations) {
            station.setLine(null);
        }
        List<Section> sections = sectionRepository.findAllByLineId(id);
        for (Section section : sections) {
            section.setLine(null);
        }
        lineRepository.deleteById(id);
    }

    @Transactional
    public void updateLineById(Long id, LineRequest request) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(() -> new NoSuchElementException("지하철 노선이 존재하지 않습니다"));
        line.setName(request.getName());
        line.setColor(request.getColor());
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest request) {
        Line line = lineRepository.findById(lineId)
                                  .orElseThrow(() -> new NoSuchElementException("지하철 노선이 존재하지 않습니다"));

        Station upStation = stationRepository.findById(request.getUpStationId()).get();
        Station downStation = stationRepository.findById(request.getDownStationId()).get();

        upStation.setLine(line);
        downStation.setLine(line);

        List<Station> stations = stationRepository.findAllByLineId(lineId);
        List<Section> sections = sectionRepository.findAllByLineId(lineId);

        if (stations.contains(downStation)) {
            for (Section section : sections) {
                if (section.getStation().getId().equals(downStation.getId())) {
                    Section newSection = sectionRepository.save(new Section(line, upStation));
                    section.setParent(newSection);
                    section.setDistance(request.getDistance());
                    break;
                }
            }
        }
    }
}
