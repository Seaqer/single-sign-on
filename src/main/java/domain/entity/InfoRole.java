package domain.entity;


import core.domain.authorization.Role;

import javax.persistence.*;

/**
 * Информация о роли
 */
@Entity
@Table(name = "ROLES")
public class InfoRole implements Role {
    @Id
    @GeneratedValue
    @Column(name = "ROLE_ID")
    private Long id;
    @Column(name = "ROLE_NAME", nullable = false)
    private String name;

    /**
     * Создать роль
     * @param id идентификатор роли
     * @param name название роли
     */
    public InfoRole(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Создать пустую роль
     * @return роль
     */
    public static Role createEmpty(){
        return new InfoRole(null,null);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "InfoRole{" +
                "name='" + name + '\'' +
                '}';
    }
}
