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
import java.util.Date;

/**
 * Entity to manage tokens in database
 */
@Getter
@Setter
@Accessors(chain = true)
@Entity(name = "token")
@Table(name = "token")
public class TokenEntity {
    /**
     * The ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "token_id_gen")
    @SequenceGenerator(name = "token_id_gen", sequenceName = "token_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    /**
     * The value
     */
    @Column(name = "token")
    private String value;

    /**
     * The creation date
     */
    @Column(name = "created_at")
    @SuppressWarnings("java:S2143")
    private Date createdAt = new Date();

    /**
     * The deleted flag
     */
    private Boolean deleted = false;
}
