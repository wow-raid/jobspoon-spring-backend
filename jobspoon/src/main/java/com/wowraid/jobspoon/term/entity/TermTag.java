package com.wowraid.jobspoon.term.entity;

// TermId(FK)
// TagId(FK)

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TermTagId.class)
public class TermTag {

    @Id
    @ManyToOne
    @JoinColumn(name = "term_id", nullable = true)
    private Term term;

    @Id
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

}
