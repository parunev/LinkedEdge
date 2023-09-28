package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "EDGE_ORGANISATION")
@AttributeOverride(name = "id", column = @Column(name = "ORGANISATION_ID"))
public class Organisation extends BaseEntity {

    @Column(name = "ORGANISATION_NAME")
    private String name;

    @Column(name = "SALES_NAV_LINK")
    private String salesNavLink;

    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
