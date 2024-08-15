package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where i.ownerId = :ownerId")
    List<Item> getAllByUserId(@Param("ownerId") Long ownerId);

    @Query("select i " +
            "from Item i " +
            "where (lower(i.name) like lower(concat('%', :text, '%')) " +
            "or lower(i.description) like lower(concat('%', :text, '%'))) " +
            "and i.available = true")
    List<Item> search(@Param("text") String text);

    boolean existsById(@Param("id") Long id);

    @Query("select i " +
            "from Item i " +
            "where i.ownerId = :ownerId " +
            "and i.id = :itemId")
    Optional<Item> findItemByOwnerId(@Param("ownerId") Long ownerId, @Param("itemId") Long itemId);
}
