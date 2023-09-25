package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_EXPERIENCE")
@AttributeOverride(name = "id", column = @Column(name = "EXPERIENCE_ID"))
public class Experience extends BaseEntity {

    @Column(name = "JOB_TITLE")
    private String title;

    @Column(name = "DATE_STARTED")
    private LocalDate dateStarted;

    @Column(name = "DATE_ENDED")
    private LocalDate dateEnded;

    @Column(name = "LOCATION")
    private String location;
}
