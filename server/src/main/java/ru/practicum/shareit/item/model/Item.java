package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import ru.practicum.shareit.item.comment.model.Comment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Имя предмета не должно быть пустым")
    String name;

    @NotBlank(message = "Описание предмета не должно быть пустым")
    String description;

    @NotNull(message = "Статус доступности предмета должен быть определен")
    Boolean available;

    @Column(name = "owner_id")
    private Long ownerId;

    @OneToMany(mappedBy = "itemId")
    private List<Comment> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Item item = (Item) o;
        if (id == null) return false;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", description = '" + description + '\'' +
                ", available = " + available +
                ", ownerId = " + ownerId +
                '}';
    }
}
