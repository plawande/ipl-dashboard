package com.plawande.ipldashboard.repository;

import com.plawande.ipldashboard.model.Match;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByTeam1OrTeam2OrderByDateDesc(String team1, String team2, Pageable pageable);

    default List<Match> findLatestMatchesByTeam(String team, int count) {
        return findByTeam1OrTeam2OrderByDateDesc(team, team, PageRequest.of(0, count));
    }

    @Query("Select m from Match m " +
            "where (m.team1 = :team or m.team2 = :team) " +
            "and m.date between :startDate and :endDate " +
            "order by m.date desc")
    List<Match> getMatchesForTeamByYear(String team, LocalDate startDate, LocalDate endDate);
}
