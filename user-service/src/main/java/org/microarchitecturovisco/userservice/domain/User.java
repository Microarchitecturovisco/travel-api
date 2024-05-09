package org.microarchitecturovisco.userservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    @Email(regexp = ".+[@].+[\\.].+")
    @Size(max = 100)
    private String email;

    @NotNull
    private String password;

    @NotNull
    @Size(max = 50)
    private String name;

    @NotNull
    @Size(max = 50)
    private String surname;

    public User(String email, String password, String name, String surname){
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }
}
