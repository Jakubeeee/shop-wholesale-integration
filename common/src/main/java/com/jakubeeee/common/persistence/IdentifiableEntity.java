package com.jakubeeee.common.persistence;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Superclass for JPA entities that can be identified with unique {@link Long} id.
 */
@Data
@MappedSuperclass
public abstract class IdentifiableEntity implements JpaEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", updatable = false)
    protected Long id;

}
