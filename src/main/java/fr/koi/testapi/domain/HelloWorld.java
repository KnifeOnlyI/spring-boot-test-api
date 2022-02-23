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
@Entity(name = "hello_world")
public class HelloWorld {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "hello_world_id_gen")
    @SequenceGenerator(name = "gen_hello_world_id", sequenceName = "hello_world_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    private String message;
}
