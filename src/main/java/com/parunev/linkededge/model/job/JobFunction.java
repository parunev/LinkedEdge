package com.parunev.linkededge.model.job;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_JOB_FUNCTION")
@AttributeOverride(name = "id", column = @Column(name = "JOB_FUNCTION_ID"))
public class JobFunction extends BaseEntity {

    @Column(name = "JOB_FUNCTION_NAME")
    private String jobFunctionName;

    @ManyToOne
    @JoinColumn(name = "EDGE_JOB_ID")
    private Job job;
}
