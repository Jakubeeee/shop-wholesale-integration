package com.jakubeeee.security.model;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role implements Serializable {

    @Getter
    @AllArgsConstructor
    public enum Type {
        BASIC_USER(1), PRO_USER(2), ADMIN(3);
        final long id;
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    protected Long id;

    String name;

    @ManyToMany(mappedBy = "roles")
    Set<User> users;

}
