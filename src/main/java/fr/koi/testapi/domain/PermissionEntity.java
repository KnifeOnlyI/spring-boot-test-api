package fr.koi.testapi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity to manage permissions in database
 */
@Getter
@Setter
@Accessors(chain = true)
@Entity(name = "permission")
@Table(name = "permission")
public class PermissionEntity {
    /**
     * The ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "permission_id_gen")
    @SequenceGenerator(name = "permission_id_gen", sequenceName = "permission_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    /**
     * The name
     */
    private String name;
}
