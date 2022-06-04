package nextstep.subway.application;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.CreateSectionDTO;
import nextstep.subway.dto.request.SectionRequest;
import nextstep.subway.exception.DupSectionException;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.SectionInvalidException;
import nextstep.subway.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineSectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;
    private final LineStationRepository lineStationRepository;

    public LineSectionService(LineRepository lineRepository, StationRepository stationRepository,
        SectionRepository sectionRepository, LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
        this.lineStationRepository = lineStationRepository;
    }

    @Transactional()
    public void deleteStation(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(LineNotFoundException::new);
        Station station = stationRepository.findById(stationId)
            .orElseThrow(StationNotFoundException::new);
        if (line.getUpStation().equals(station)) {
            changeUpStation(line, station);

        } else if (line.getDownStation().equals(station)) {
            changeDownStation(line, station);

        } else {
            mergeSections(line, station);
        }

    }

    private void mergeSections(Line line, Station station) {
        Section backSection = sectionRepository.findAllByUpStationAndLine(station, line)
            .orElseThrow(LineNotFoundException::new);
        Section nextSection = sectionRepository.findAllByDownStationAndLine(station, line)
            .orElseThrow(LineNotFoundException::new);
        line.replaceSectionByMerge(backSection, nextSection);

        sectionRepository.delete(backSection);
        sectionRepository.delete(nextSection);
        lineStationRepository.deleteAllByStationAndLine(station, line);
    }


    private void changeDownStation(Line line, Station station) {
        Section section = sectionRepository.findAllByDownStationAndLine(station, line)
            .orElseThrow(SectionInvalidException::new);
        line.changeDownStationForDelete(section);
        sectionRepository.delete(section);
        lineStationRepository.deleteAllByStationAndLine(station, line);
    }

    private void changeUpStation(Line line, Station station) {
        Section section = sectionRepository.findAllByUpStationAndLine(station, line)
            .orElseThrow(SectionInvalidException::new);
        line.changeUpStationForDelete(section);
        sectionRepository.delete(section);
        lineStationRepository.deleteAllByStationAndLine(station, line);
    }

    @Transactional
    public void createSection(long lineId, SectionRequest sectionRequest) {
        CreateSectionDTO createSectionDTO = settingCreateSectionDTO(lineId, sectionRequest);

        validateDupSection(createSectionDTO);

        lineStationRepository.save(
            new LineStation(createSectionDTO.getLine(), createSectionDTO.getTargetStation()));

        Section appendSection = Section.of(createSectionDTO.getUpStation(),
            createSectionDTO.getDownStation(), createSectionDTO.getLine(),
            new Distance(sectionRequest.getDistance()));

        createSectionDTO.getLine()
            .addSection(appendSection, createSectionDTO.getTargetSection());
    }

    private CreateSectionDTO settingCreateSectionDTO(long lineId, SectionRequest sectionRequest) {
        Line line = getLineOrThrow(lineId);
        Station upStation = getStationOrThrow(sectionRequest.getUpStationId());
        Station downStation = getStationOrThrow(sectionRequest.getDownStationId());

        Section targetSection = null;
        Station targetStation = null;

        if (lineStationRepository.findAllByStationAndLine(upStation, line).isPresent()) {
            targetSection = sectionRepository.findAllByUpStationAndLine(upStation, line)
                .orElse(null);
            targetStation = downStation;
        } else if (lineStationRepository.findAllByStationAndLine(downStation, line).isPresent()) {
            targetSection = sectionRepository.findAllByDownStationAndLine(downStation, line)
                .orElse(null);
            targetStation = upStation;
        }
        return new CreateSectionDTO(upStation, downStation, targetSection, targetStation, line);
    }

    private void validateDupSection(CreateSectionDTO createSectionDTO) {
        if (sectionRepository.findAllByUpStationAndDownStationAndLine(
                createSectionDTO.getUpStation(), createSectionDTO.getDownStation(),
                createSectionDTO.getLine())
            .isPresent()) {
            throw new DupSectionException();
        }
    }

    private Station getStationOrThrow(long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(StationNotFoundException::new);
    }

    private Line getLineOrThrow(long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(LineNotFoundException::new);
    }

}
