package fr.koi.testapi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

/**
 * Entity to manage groups in database
 */
@Getter
@Setter
@Accessors(chain = true)
@Entity(name = "group")
@Table(name = "koi_group")
public class GroupEntity {
    /**
     * The ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "group_id_gen")
    @SequenceGenerator(name = "group_id_gen", sequenceName = "group_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    /**
     * The name
     */
    private String name;

    /**
     * The permissions
     */
    @ManyToMany
    @JoinTable(
        name = "group_permission",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<PermissionEntity> permissions;
}
