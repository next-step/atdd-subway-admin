package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Sections {
    private final List<Section> sections;

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections valueOf(Section ... sections) {
        return new Sections(new ArrayList<>(Arrays.asList(sections)));
    }

    public static Sections valueOf(List<Section> sections) {
        return new Sections(sections);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Sections)) {
            return false;
        }
        Sections sections = (Sections) o;
        return Objects.equals(this.sections, sections.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sections);
    }
}
