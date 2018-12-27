package org.nzbhydra.mediainfo;

import com.google.common.base.Strings;
import lombok.Data;

import javax.persistence.*;
import java.util.Optional;

@Data
@Entity
@Table(name = "tvinfo")
public class TvInfo implements Comparable<TvInfo> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;

    @Column(unique = true)
    private String tvdbId;
    @Column(unique = true)
    private String tvrageId;
    @Column(unique = true)
    private String tvmazeId;
    private String title;
    private Integer year;
    private String posterUrl;

    public TvInfo(String tvdbId, String tvrageId, String tvmazeId, String title, Integer year, String posterUrl) {
        this.tvdbId = tvdbId;
        this.tvrageId = tvrageId;
        this.tvmazeId = tvmazeId;
        this.title = title;
        this.year = year;
        this.posterUrl = posterUrl;
    }

    public TvInfo(MediaInfo mediaInfo) {
        this.tvdbId = mediaInfo.getTvDbId().orElse(null);
        this.tvrageId = mediaInfo.getTvRageId().orElse(null);
        this.tvmazeId = mediaInfo.getTvMazeId().orElse(null);
        this.title = mediaInfo.getTitle().orElse(null);
        this.year = mediaInfo.getYear().orElse(null);
        this.posterUrl = mediaInfo.getPosterUrl().orElse(null);
    }

    public TvInfo() {
    }

    public Optional<String> getTvdbId() {
        return Optional.ofNullable(Strings.emptyToNull(tvdbId));
    }

    public Optional<String> getTvrageId() {
        return Optional.ofNullable(Strings.emptyToNull(tvrageId));
    }

    public Optional<String> getTvmazeId() {
        return Optional.ofNullable(Strings.emptyToNull(tvmazeId));
    }

    public Optional<String> getPosterUrl() {
        return Optional.ofNullable(Strings.emptyToNull(posterUrl));
    }

    @Override
    public int compareTo(TvInfo o) {
        if (o == null) {
            return 1;
        }
        return Integer.compare(getNumberOfContainedIds(), o.getNumberOfContainedIds());
    }

    protected int getNumberOfContainedIds() {
        int countContainedIds = 0;
        if (getTvmazeId().isPresent()) {
            countContainedIds++;
        }
        if (getTvrageId().isPresent()) {
            countContainedIds++;
        }
        if (getTvdbId().isPresent()) {
            countContainedIds++;
        }
        return countContainedIds;
    }
}
