package com.plawande.ipldashboard.data;

import com.plawande.ipldashboard.model.Match;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class MatchDataProcessor implements ItemProcessor<MatchInput, Match> {
    @Override
    public Match process(MatchInput matchInput) throws Exception {
        Match match = new Match();
        match.setId(Long.valueOf(matchInput.getId()));
        match.setCity(matchInput.getCity());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        match.setDate(LocalDate.parse(matchInput.getDate(), dateTimeFormatter));
        match.setPlayerOfMatch(matchInput.getPlayer_of_match());
        match.setVenue(matchInput.getVenue());
        String firstInningsTeam, secondInningsTeam;
        if("bat".equalsIgnoreCase(matchInput.getToss_decision())) {
            firstInningsTeam = matchInput.getToss_winner();
            secondInningsTeam = firstInningsTeam.equalsIgnoreCase(matchInput.getTeam1())
                    ? matchInput.getTeam2() : matchInput.getTeam1();
        }else {
            secondInningsTeam = matchInput.getToss_winner();
            firstInningsTeam = secondInningsTeam.equalsIgnoreCase(matchInput.getTeam1())
                    ? matchInput.getTeam2() : matchInput.getTeam1();
        }
        match.setTeam1(firstInningsTeam);
        match.setTeam2(secondInningsTeam);
        match.setTossWinner(matchInput.getToss_winner());
        match.setTossDecision(matchInput.getToss_decision());
        match.setMatchWinner(matchInput.getWinner());
        match.setResult(matchInput.getResult());
        match.setResultMargin(matchInput.getResult_margin());
        match.setUmpire1(matchInput.getUmpire1());
        match.setUmpire2(matchInput.getUmpire2());
        return match;
    }
}
