package domain.entity;


import core.domain.authorization.Operation;

import javax.persistence.*;

/**
 * Информация об операции
 */
@Entity
@Table(name = "OPERATIONS")
public class InfoOperation implements Operation {
    @Id
    @GeneratedValue
    @Column(name = "OPER_ID")
    private Long id ;
    @Column(name = "OPER_NAME", nullable = false)
    private String name;

    /**
     * Создать операцию
     * @param id идентификатор операции
     * @param name имя операции
     */
    public InfoOperation(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Создать пустую операцию
     * @return операция
     */
    public static Operation createEmpty(){
        return new InfoOperation(null,null);
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
        return "InfoOperation{" +
                "name='" + name + '\'' +
                '}';
    }
}
