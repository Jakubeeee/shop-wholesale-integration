package com.jakubeeee.common.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Superclass for JPA entities that can be identified with unique {@link Long} id.
 */
@MappedSuperclass
@EqualsAndHashCode
@ToString
public abstract class IdentifiableEntity implements BasicEntity {

    @Getter
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", updatable = false)
    protected Long id;

}
