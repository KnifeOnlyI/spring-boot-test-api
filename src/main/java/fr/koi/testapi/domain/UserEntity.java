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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * The entity to manage users in database
 */
@Getter
@Setter
@Accessors(chain = true)
@Entity(name = "user")
@Table(name = "koi_user")
public class UserEntity {
    /**
     * The ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "koi_user_id_gen")
    @SequenceGenerator(name = "koi_user_id_gen", sequenceName = "koi_user_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * The email
     */
    private String email;

    /**
     * The login
     */
    private String login;

    /**
     * The password
     */
    private String password;

    /**
     * The creation date
     */
    private Date createdAt;

    /**
     * The activated flag
     */
    private Boolean activated;

    /**
     * The tokens
     */
    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<TokenEntity> tokens;

    /**
     * The groups
     */
    @ManyToMany
    @JoinTable(
        name = "user_group",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private List<GroupEntity> groups;
}
