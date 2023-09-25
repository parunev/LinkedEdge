package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_PROFILE")
@AttributeOverride(name = "id", column = @Column(name = "PROFILE_ID"))
public class Profile extends BaseEntity {

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "LOCATION")
    private String location;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "SALES_NAV_LINK")
    private String salesNavLink;

    @Column(name = "ACCOUNT_LINK")
    private String accountLink;

    @ManyToOne
    @JoinColumn(name = "EDUCATION_ID")
    private Education education;

    @ManyToOne
    @JoinColumn(name = "EXPERIENCE_ID")
    private Experience experience;

    @ManyToOne
    @JoinColumn(name = "ORGANISATION_ID")
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name = "SKILL_ID")
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
