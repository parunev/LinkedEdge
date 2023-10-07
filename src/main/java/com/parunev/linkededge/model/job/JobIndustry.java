package com.parunev.linkededge.model.job;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_JOB_INDUSTRY")
@AttributeOverride(name = "id", column = @Column(name = "JOB_FUNCTION_ID"))
public class JobIndustry extends BaseEntity {

    @Column(name = "JOB_INDUSTRY_NAME")
    private String jobIndustryName;

    @ManyToOne
    @JoinColumn(name = "EDGE_JOB_ID")
    private Job job;
}
