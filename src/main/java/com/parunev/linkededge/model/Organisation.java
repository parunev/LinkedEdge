package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `Organisation` class represents an organization in the LinkedEdge application. It extends the `BaseEntity` class
 * and is typically used when creating a user profile. This class represents organizations for which the user has worked.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "EDGE_ORGANISATION")
@AttributeOverride(name = "id", column = @Column(name = "ORGANISATION_ID"))
public class Organisation extends BaseEntity {

    /**
     * The name of the organization.
     */
    @Column(name = "ORGANISATION_NAME")
    private String name;

    /**
     * The LinkedIn sales navigator link associated with the organization.
     */
    @Column(name = "SALES_NAV_LINK")
    private String salesNavLink;

    /**
     * The user profile to which this organization is associated. These organizations represent those for whom the user has worked.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
