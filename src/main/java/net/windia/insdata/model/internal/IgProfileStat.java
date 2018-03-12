package net.windia.insdata.model.internal;

import javax.persistence.*;

@MappedSuperclass
public abstract class IgProfileStat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private IgProfile igProfile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IgProfile getIgProfile() {
        return igProfile;
    }

    public void setIgProfile(IgProfile igProfile) {
        this.igProfile = igProfile;
    }
}
