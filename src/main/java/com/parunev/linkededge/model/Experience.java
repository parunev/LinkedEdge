package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_EXPERIENCE")
@AttributeOverride(name = "id", column = @Column(name = "EXPERIENCE_ID"))
public class Experience extends BaseEntity {

    @Column(name = "DESCRIPTION", length = 2000)
    private String description;

    @Column(name = "JOB_TITLE")
    private String title;

    @Column(name = "DATE_STARTED")
    private String dateStarted;

    @Column(name = "DATE_ENDED")
    private String dateEnded;

    @Column(name = "LOCATION")
    private String location;

    @ManyToOne
    @JoinColumn(name = "EDGE_ORGANISATION_ID")
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
