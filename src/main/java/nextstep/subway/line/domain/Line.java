package nextstep.subway.line.domain;

import nextstep.subway.common.domain.*;
import nextstep.subway.linebridge.domain.LineBridge;
import nextstep.subway.linebridge.domain.LineBridges;

import javax.persistence.*;
import java.util.*;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Embedded
    private LineBridges lineBridges = new LineBridges();

    public Line (Builder builder){
        this.name = builder.name;
        this.color = builder.color;
        addLineBridge(builder.lineBridge);
    }

    protected Line() {
    }

    public Line update(String name, String color){
        if(!name.isEmpty() && !this.name.equals(name)){
            this.name = name;
        }
        if(!color.isEmpty() && !this.name.equals(color)){
            this.color = color;
        }
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void addLineBridge(LineBridge lineBridge) {
        Objects.requireNonNull(lineBridge, "구간 정보가 필요합니다.");
        lineBridges.add(lineBridge);

        if (lineBridge.getLine() != this) {
            lineBridge.updateLine(this);
        }
    }

    public List<LineBridge> getLineBridges() {
        return lineBridges.getLineBridges();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {

        private Long id;
        private String name;
        private String color;
        private LineBridge lineBridge;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public LineBridge getLineBridge() {
            return lineBridge;
        }

        public Builder setLineBridge(LineBridge lineBridge) {
            this.lineBridge = lineBridge;
            return this;
        }

        public Line build() {
            return new Line(this);
        }
    }
}
