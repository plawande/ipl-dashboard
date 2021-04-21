package com.plawande.ipldashboard.controller;

import com.plawande.ipldashboard.model.Match;
import com.plawande.ipldashboard.model.Team;
import com.plawande.ipldashboard.repository.MatchRepository;
import com.plawande.ipldashboard.repository.TeamRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class TeamController {

    private TeamRepository teamRepository;
    private MatchRepository matchRepository;

    public TeamController(TeamRepository teamRepository,
                          MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
    }

    @GetMapping("/team/{teamName}")
    public ResponseEntity<Team> getTeam(@PathVariable("teamName") String teamName) {
        Team team = teamRepository.findByTeamName(teamName);
        List<Match> matches = matchRepository.findLatestMatchesByTeam(teamName, 4);
        team.setMatches(matches);
        return ResponseEntity.ok(team);
    }

    @GetMapping("/team")
    public Iterable<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @GetMapping("/team/{teamName}/matches")
    public ResponseEntity<List<Match>> getMatchesForTeamByYear(@PathVariable("teamName") String teamName,
                                                               @RequestParam("year") int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year + 1, 1, 1 );
        List<Match> matches = matchRepository.getMatchesForTeamByYear(teamName, startDate, endDate);
        return ResponseEntity.ok(matches);
    }
}
