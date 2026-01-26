package lista4.dbRepositories;

import lista4.dbModel.GameEntity;
import lista4.dbModel.MoveEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MoveRepository extends JpaRepository<MoveEntity, Long> {
    // Ta metoda przyda Ci się później do odtwarzania gry!
    // Spring sam ogarnie, jak wyciągnąć wszystkie ruchy danej gry po kolei.
    List<MoveEntity> findByGameOrderByMoveNumberAsc(GameEntity game);
}