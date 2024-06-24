package air.astana.authservice.model.entity;

import air.astana.authservice.model.RoleCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "roles_id_seq")
    @SequenceGenerator(name = "roles_id_seq", sequenceName = "roles_id_seq", allocationSize = 1)
    private int id;

    @Enumerated(EnumType.STRING)
    private RoleCode code;

    @OneToMany(mappedBy = "role")
    private List<User> users;
}
