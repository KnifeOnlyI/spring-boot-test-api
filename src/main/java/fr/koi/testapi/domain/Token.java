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

@Getter
@Setter
@Accessors(chain = true)
@Entity(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "token_id_gen")
    @SequenceGenerator(name = "gen_token_id", sequenceName = "token_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    private String value;
}
